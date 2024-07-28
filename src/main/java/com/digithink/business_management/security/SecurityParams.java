package com.digithink.business_management.security;

public class SecurityParams {
	public static final String JWT_HEADER_NAME = "Authorization";
	public static final String SECRET = "securityCode";
	public static final long EXPIRATION = 5 * 30 * 24 * 3600 * 1000L; // 5 months
	public static final String HEADER_PREFIX = "Bearer ";
}
