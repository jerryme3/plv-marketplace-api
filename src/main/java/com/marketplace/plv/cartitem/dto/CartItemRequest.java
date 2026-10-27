package com.marketplace.plv.cartitem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequest(
		@NotNull Long cartId,
		@NotNull Long productId,
		@Positive int quantity
) {
}
