package com.marketplace.plv.order;

import com.marketplace.plv.order.dto.OrderResponse;
import com.marketplace.plv.order.dto.OrderStatusUpdateRequest;
import com.marketplace.plv.shared.entity.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	ApiResponse<List<OrderResponse>> checkout() {
		return orderService.checkout();
	}

	@GetMapping
	List<OrderResponse> getOrders() {
		return orderService.getBuyerOrders();
	}

	@GetMapping("/{orderId}")
	OrderResponse getOrder(@PathVariable Long orderId) {
		return orderService.getBuyerOrder(orderId);
	}

	@PatchMapping("/{orderId}/status")
	ApiResponse<OrderResponse> updateStatus(
			@PathVariable Long orderId,
			@Valid @RequestBody OrderStatusUpdateRequest request
	) {
		return orderService.updateBuyerOrderStatus(orderId, request);
	}
}
