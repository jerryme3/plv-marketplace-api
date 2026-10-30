package com.marketplace.plv.product_stocks.dto;

import com.marketplace.plv.product.dto.ProductResponse;

public record ProductStockResponse(
		ProductResponse product,
		Integer stockQuantity
) {
}
