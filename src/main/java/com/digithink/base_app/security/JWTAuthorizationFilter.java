package com.digithink.base_app.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.digithink.base_app.model.UserAccount;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class JWTAuthorizationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Headers",
				"Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization, username, password,X-Company-ID");
		response.addHeader("Access-Control-Expose-Headers",
				"Access-Control-Allow-Origin, Access-Control-Allow-Credentials, Authorization");
		response.addHeader("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, PATCH, OPTIONS, CONNECT");

		if (request.getMethod().equals("OPTIONS")) {
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}

//		log.info("Read token from header");
//		log.info(request.getLocalAddr() + " :: " + request.getRemoteAddr());

		String jwtToken = request.getHeader(SecurityParams.JWT_HEADER_NAME);
		if (jwtToken == null || !jwtToken.startsWith(SecurityParams.HEADER_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}

//		try {
//			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SecurityParams.SECRET)).build();
//			String jwt = jwtToken.substring(SecurityParams.HEADER_PREFIX.length());
//			DecodedJWT decodedJWT = verifier.verify(jwt);
//			String username = decodedJWT.getSubject();
//
//			if (username != null) {
//				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//						username, null, null);
//				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//			}
//
//		} catch (Exception e) {
//			log.error("Token verification failed", e);
//		}

		try {
			String company = request.getHeader("X-Company-ID");
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SecurityParams.SECRET)).build();
			String jwt = jwtToken.substring(SecurityParams.HEADER_PREFIX.length());
			DecodedJWT decodedJWT = verifier.verify(jwt);
			String username = decodedJWT.getSubject();

			if (username != null && company != null) {
				UserAccount userAccount = new UserAccount();
				userAccount.setUsername(username);
				userAccount.setCompany(Long.parseLong(company)); // Assuming you have a setter for company
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						userAccount, null, null);
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}

		} catch (Exception e) {
			log.error("Token verification failed", e);
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		filterChain.doFilter(request, response);
	}
}
