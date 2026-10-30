package com.marketplace.plv.shared.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthCookieService {

	private final JwtEncoder encoder;
	private final String cookieName = "AUTH_TOKEN";

	public ResponseCookie generateCookie(String id, String scope) {
		var now = Instant.now();
		var expiresAt = now.plus(1, ChronoUnit.HOURS);

		var claims = JwtClaimsSet.builder()
				.issuer("self")
				.issuedAt(now)
				.expiresAt(expiresAt)
				.subject(id)
				.claim("scope", scope)
				.build();

		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		var token = this.encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

		long cookieMaxAge = 3600;

		return ResponseCookie
				.from(cookieName, token)
				.httpOnly(true)
				.secure(true)
				.path("/")
				.maxAge(cookieMaxAge)
				.sameSite("Strict")
				.build();

	}

	public ResponseCookie clearCookie() {
		return ResponseCookie
				.from(cookieName)
				.httpOnly(true)
				.secure(true)
				.path("/")
				.maxAge(0)
				.sameSite("Strict")
				.build();
	}

	public Long getUserIdFromContext() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null && auth.isAuthenticated()) return Long.parseLong(auth.getName());

		SecurityContextHolder.clearContext();
		return null;
	}

	public boolean isValidSession() {
		return getUserIdFromContext() != null;
	}

}
