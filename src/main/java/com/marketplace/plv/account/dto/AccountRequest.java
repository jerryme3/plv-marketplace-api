package com.marketplace.plv.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountRequest(

		@NotBlank(message = "Email is required")
		@Email(message = "Email is invalid")
		String email,

		@NotBlank(message = "Password is required")
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&.])\\S{8,40}$",
				message = "Password must have at least one of these characters, with no space: letter, number, and special character.")
		@Size(min = 8, max = 40, message = "Password length must be between or equal to 8 and 40 characters.")
		String password
) {
}
