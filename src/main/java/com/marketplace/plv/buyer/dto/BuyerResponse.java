package com.marketplace.plv.buyer.dto;

public record BuyerResponse(
		Long id,
		Long userId,
		String deliveryBuilding
) {
}
