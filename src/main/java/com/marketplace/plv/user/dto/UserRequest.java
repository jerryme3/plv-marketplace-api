package com.marketplace.plv.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(

		@NotBlank(message = "Email is required")
		@Email(message = "Email is invalid")
		@Size(max = 100, message = "Email must not exceed 100 characters")
		String email,

		@NotBlank(message = "Password is required")
		String password,

		@NotBlank(message = "Username is required")
		@Size(max = 50, message = "Username must not exceed 50 characters")
		String username
) {
}
