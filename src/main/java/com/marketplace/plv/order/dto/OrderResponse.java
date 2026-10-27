package com.marketplace.plv.order.dto;

import com.marketplace.plv.order.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(
		Long id,
		Long buyerId,
		BigDecimal totalAmount,
		OrderStatus status,
		Instant createdAt,
		Instant updatedAt
) {
}
