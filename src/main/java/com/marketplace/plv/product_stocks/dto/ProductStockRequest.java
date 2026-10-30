package com.marketplace.plv.product_stocks.dto;

import jakarta.validation.constraints.NotNull;

public record ProductStockRequest(

		@NotNull(message = "Product ID required")
		Long productId,

		@NotNull(message = "Stock number is required")
		Integer stockQuantity
) {
}
