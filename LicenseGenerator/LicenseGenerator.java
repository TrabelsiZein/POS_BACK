/**
 * ZS Retail POS — License Generator Tool
 * =======================================
 * Standalone Java file (no Maven/build tool needed).
 * Requires: Java 11+, private_key.pem in the same folder.
 *
 * Usage:
 *   java LicenseGenerator.java <company> <appId> <expiresAt>
 *
 * Examples:
 *   java LicenseGenerator.java "ABC Store" "POS-CLIENT-001" "2027-03-25"
 *   java LicenseGenerator.java "My Dev Machine" "POS-DEV-001" "2099-12-31"
 *
 * Output:
 *   license.json  — ready to upload via Company Information page
 *
 * IMPORTANT: Keep private_key.pem SECRET. Never share it or commit it to git.
 */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class LicenseGenerator {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: java LicenseGenerator.java <company> <appId> <expiresAt>");
            System.err.println("  expiresAt format: YYYY-MM-DD");
            System.err.println("");
            System.err.println("Examples:");
            System.err.println("  java LicenseGenerator.java \"ABC Store\" \"POS-CLIENT-001\" \"2027-03-25\"");
            System.err.println("  java LicenseGenerator.java \"Dev Machine\" \"POS-DEV-001\" \"2099-12-31\"");
            System.exit(1);
        }

        String company   = args[0];
        String appId     = args[1];
        String expiresAt = args[2];
        String issuedAt  = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        // Validate date format
        LocalDate.parse(expiresAt); // throws if invalid

        // Load private key
        Path keyPath = Path.of("private_key.pem");
        if (!Files.exists(keyPath)) {
            System.err.println("ERROR: private_key.pem not found in current directory.");
            System.err.println("Make sure you run this tool from the LicenseGenerator folder.");
            System.exit(1);
        }

        String pem = Files.readString(keyPath)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(pem);
        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));

        // Build data JSON (compact, deterministic key order for reliable signing)
        String dataJson = "{"
            + "\"company\":\"" + escape(company) + "\","
            + "\"appId\":\"" + escape(appId) + "\","
            + "\"issuedAt\":\"" + issuedAt + "\","
            + "\"expiresAt\":\"" + expiresAt + "\""
            + "}";

        // Sign
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(dataJson.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(sig.sign());

        // Build final license JSON
        String licenseJson = "{\n"
            + "  \"data\": " + dataJson + ",\n"
            + "  \"signature\": \"" + signature + "\"\n"
            + "}";

        // Write output
        Path outputPath = Path.of("license.json");
        Files.writeString(outputPath, licenseJson);

        System.out.println("✅ License generated successfully!");
        System.out.println("   Company  : " + company);
        System.out.println("   App ID   : " + appId);
        System.out.println("   Issued   : " + issuedAt);
        System.out.println("   Expires  : " + expiresAt);
        System.out.println("   Output   : " + outputPath.toAbsolutePath());
        System.out.println("");
        System.out.println("📧 Send license.json to the customer.");
        System.out.println("   Customer uploads it via: Admin > Company Information > License section.");
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
