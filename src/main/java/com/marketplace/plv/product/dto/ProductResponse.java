package com.marketplace.plv.product.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
		Long id,
		Long storeId,
		Long categoryId,
		String title,
		String description,
		BigDecimal price,
		int stockQuantity,
		Instant createdAt,
		Instant updatedAt
) {
}
