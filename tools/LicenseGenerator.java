/**
 * Standalone license generator (no Maven needed).
 *
 * Usage:
 *   java LicenseGenerator.java "<company>" "<installationId>" "<expiresAt>"
 *
 * Example:
 *   java LicenseGenerator.java "ABC Store" "A1B2C3...F9" "2030-12-31"
 *
 * Requires private_key.pem in same folder.
 */
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.util.Base64;

public class LicenseGenerator {

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("Usage: java LicenseGenerator.java <company> <installationId> <expiresAt(yyyy-MM-dd)>");
			System.exit(1);
		}

		String company = args[0];
		String installationId = args[1];
		String expiresAt = args[2];
		String issuedAt = LocalDate.now().toString();
		LocalDate.parse(expiresAt);

		PrivateKey privateKey = loadPrivateKey(Path.of("private_key.pem"));

		String dataJson = "{"
				+ "\"company\":\"" + escape(company) + "\","
				+ "\"installationId\":\"" + escape(installationId) + "\","
				+ "\"issuedAt\":\"" + issuedAt + "\","
				+ "\"expiresAt\":\"" + expiresAt + "\""
				+ "}";

		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(privateKey);
		signature.update(dataJson.getBytes(StandardCharsets.UTF_8));
		String signatureB64 = Base64.getEncoder().encodeToString(signature.sign());

		String finalJson = "{\n"
				+ "  \"data\": " + dataJson + ",\n"
				+ "  \"signature\": \"" + signatureB64 + "\"\n"
				+ "}\n";

		Files.writeString(Path.of("license.json"), finalJson, StandardCharsets.UTF_8);
		System.out.println("License generated successfully: " + Path.of("license.json").toAbsolutePath());
	}

	private static PrivateKey loadPrivateKey(Path path) throws Exception {
		String pem = Files.readString(path, StandardCharsets.UTF_8)
				.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s+", "");
		byte[] bytes = Base64.getDecoder().decode(pem);
		return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
	}

	private static String escape(String value) {
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
