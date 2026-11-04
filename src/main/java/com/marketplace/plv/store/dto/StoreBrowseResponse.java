package com.marketplace.plv.store.dto;

import java.time.Instant;

public record StoreBrowseResponse(
		Long id,
		String name,
		String description,
		long productCount,
		Instant createdAt
) {
}
