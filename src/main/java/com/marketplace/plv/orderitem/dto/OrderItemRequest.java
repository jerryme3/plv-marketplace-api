package com.marketplace.plv.orderitem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderItemRequest(
		@NotNull Long orderId,
		@NotNull Long productId,
		@Positive int quantity,
		@NotNull @DecimalMin("0.00") BigDecimal unitPrice
) {
}
