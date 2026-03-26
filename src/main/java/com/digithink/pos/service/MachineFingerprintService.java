package com.digithink.pos.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Computes a stable machine fingerprint on startup and keeps it cached in memory.
 *
 * Windows strategy:
 * - MachineGuid from registry
 * - System drive volume serial (C:)
 *
 * installationId = SHA-256(machineGuid + "|" + volumeSerial), upper-hex.
 */
@Service
public class MachineFingerprintService {

	private static final Logger log = LoggerFactory.getLogger(MachineFingerprintService.class);

	private static volatile String installationId;

	@PostConstruct
	public void init() {
		installationId = computeInstallationId();
		log.info("Machine installation ID initialized: {}", mask(installationId));
	}

	public String getInstallationId() {
		if (installationId == null || installationId.isBlank()) {
			installationId = computeInstallationId();
		}
		return installationId;
	}

	private String computeInstallationId() {
		try {
			String machineGuid = readWindowsMachineGuid();
			String volumeSerial = readWindowsSystemVolumeSerial();
			String combined = normalize(machineGuid) + "|" + normalize(volumeSerial);
			return sha256Hex(combined).toUpperCase(Locale.ROOT);
		} catch (Exception e) {
			log.error("Failed to compute machine fingerprint: {}", e.getMessage());
			throw new IllegalStateException("Unable to compute machine installation ID", e);
		}
	}

	private String readWindowsMachineGuid() throws Exception {
		Process process = new ProcessBuilder("reg", "query", "HKLM\\SOFTWARE\\Microsoft\\Cryptography", "/v", "MachineGuid")
				.redirectErrorStream(true)
				.start();
		String output = readProcessOutput(process);
		process.waitFor();
		for (String line : output.split("\\R")) {
			if (line.toLowerCase(Locale.ROOT).contains("machineguid")) {
				String[] parts = line.trim().split("\\s+");
				return parts[parts.length - 1].trim();
			}
		}
		throw new IllegalStateException("MachineGuid not found");
	}

	private String readWindowsSystemVolumeSerial() throws Exception {
		Process process = new ProcessBuilder("cmd", "/c", "vol C:")
				.redirectErrorStream(true)
				.start();
		String output = readProcessOutput(process);
		process.waitFor();
		for (String line : output.split("\\R")) {
			String lower = line.toLowerCase(Locale.ROOT);
			if (lower.contains("serial number is")) {
				return line.substring(lower.indexOf("serial number is") + "serial number is".length()).trim();
			}
		}
		throw new IllegalStateException("Volume serial number not found");
	}

	private String readProcessOutput(Process process) throws Exception {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
			return br.lines().collect(Collectors.joining("\n"));
		}
	}

	private String normalize(String input) {
		return input == null ? "" : input.trim().replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
	}

	private String sha256Hex(String value) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	private String mask(String id) {
		if (id == null || id.length() < 12) return "N/A";
		return id.substring(0, 6) + "..." + id.substring(id.length() - 6);
	}
}
