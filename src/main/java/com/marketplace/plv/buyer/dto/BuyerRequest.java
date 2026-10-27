package com.marketplace.plv.buyer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BuyerRequest(
		@NotNull Long userId,
		@Size(max = 100) String deliveryBuilding
) {
}
