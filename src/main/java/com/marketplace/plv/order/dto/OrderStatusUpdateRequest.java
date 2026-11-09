package com.marketplace.plv.order.dto;

import com.marketplace.plv.order.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
		@NotNull OrderStatus status
) {
}
