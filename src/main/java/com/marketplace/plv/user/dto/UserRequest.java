package com.marketplace.plv.user.dto;

import com.marketplace.plv.account.dto.AccountRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(

		@NotBlank(message = "Username is required")
		@Size(max = 50, message = "Username must not exceed 50 characters")
		String username,

		@NotNull(message = "Account is required")
		AccountRequest account
) {
}
