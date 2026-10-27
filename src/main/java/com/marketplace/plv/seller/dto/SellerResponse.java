package com.marketplace.plv.seller.dto;

import java.math.BigDecimal;

public record SellerResponse(
		Long id,
		Long userId,
		BigDecimal rating,
		boolean verified
) {
}
