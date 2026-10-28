package com.marketplace.plv.shared.auth;

import com.marketplace.plv.account.dto.AccountRequest;
import com.marketplace.plv.user.dto.UserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<String> register(@Valid @RequestBody UserRequest request) {
		var cookie = authService.register(request);

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body("User registered successfully!");
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@Valid @RequestBody AccountRequest request) {
		var cookie = authService.login(request);

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body("Logged in successfully!");
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
		var cookie = authService.logout();

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body("Logged out successfully.");
	}

}
