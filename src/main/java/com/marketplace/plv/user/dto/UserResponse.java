package com.marketplace.plv.user.dto;

import java.time.Instant;

public record UserResponse(
		Long id,
		Long accountId,
		String username,
		boolean active,
		Instant createdAt,
		Instant updatedAt,
		Instant lastLogin
) {
}
