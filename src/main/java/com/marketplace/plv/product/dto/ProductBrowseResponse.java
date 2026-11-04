package com.marketplace.plv.product.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductBrowseResponse(
		Long id,
		String title,
		String description,
		BigDecimal price,
		int stockQuantity,
		CategorySummary category,
		StoreSummary store,
		Instant createdAt,
		Instant updatedAt
) {
}
