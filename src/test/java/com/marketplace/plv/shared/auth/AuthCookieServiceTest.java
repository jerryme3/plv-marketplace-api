package com.marketplace.plv.shared.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

class AuthCookieServiceTest {

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void readsTheAuthenticatedAccountId() {
		var authentication = UsernamePasswordAuthenticationToken.authenticated("70", null, List.of());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		assertEquals(70L, new AuthCookieService(mock(JwtEncoder.class)).getUserIdFromContext());
	}

	@Test
	void clearsContextWhenThereIsNoAuthentication() {
		SecurityContextHolder.getContext().setAuthentication(null);

		assertNull(new AuthCookieService(mock(JwtEncoder.class)).getUserIdFromContext());
	}
}
