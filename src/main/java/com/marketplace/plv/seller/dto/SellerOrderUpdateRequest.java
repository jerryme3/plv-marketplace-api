package com.marketplace.plv.seller.dto;

import com.marketplace.plv.order.OrderStatus;
import jakarta.validation.constraints.Size;

public record SellerOrderUpdateRequest(
		OrderStatus status,
		@Size(max = 100) String currentLocation
) {
}
