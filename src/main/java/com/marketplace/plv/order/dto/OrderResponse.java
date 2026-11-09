package com.marketplace.plv.order.dto;

import com.marketplace.plv.order.OrderStatus;
import com.marketplace.plv.orderitem.dto.OrderItemResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
		Long id,
		Long buyerId,
		Long sellerId,
		BigDecimal totalAmount,
		OrderStatus status,
		String deliveryLocation,
		String currentLocation,
		List<OrderItemResponse> items,
		Instant createdAt,
		Instant updatedAt
) {
}
