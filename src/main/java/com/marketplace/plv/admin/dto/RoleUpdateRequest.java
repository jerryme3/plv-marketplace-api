package com.marketplace.plv.admin.dto;

import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(
		@NotNull(message = "User ID is required")
		Long userId
) {
}
