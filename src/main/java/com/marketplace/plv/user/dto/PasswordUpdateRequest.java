package com.marketplace.plv.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordUpdateRequest(
		@NotBlank(message = "Password is required")
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&.])\\S{8,40}$",
				message = "Password must have at least one of these characters, with no space: letter, number, and special character.")
		@Size(min = 8, max = 40, message = "Password length must be between or equal to 8 and 40 characters.")
		String newPassword,

		@NotBlank(message = "Old password is required")
		String oldPassword
) {
}
