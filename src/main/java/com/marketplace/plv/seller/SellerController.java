package com.marketplace.plv.seller;

import com.marketplace.plv.order.OrderService;
import com.marketplace.plv.order.dto.OrderResponse;
import com.marketplace.plv.shared.entity.ApiResponse;
import com.marketplace.plv.seller.dto.SellerOrderUpdateRequest;
import com.marketplace.plv.seller.dto.SellerResponse;
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
@RequestMapping("/api/v1/seller")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class SellerController {

	private final SellerService sellerService;
	private final OrderService orderService;

	@PostMapping("/register")
	ApiResponse<SellerResponse> registerSeller() {
		return sellerService.registerSeller();
	}

	@GetMapping("/orders")
	List<OrderResponse> getOrders() {
		return orderService.getSellerOrders();
	}

	@PatchMapping("/orders/{orderId}")
	ApiResponse<OrderResponse> updateOrder(
			@PathVariable Long orderId,
			@Valid @RequestBody SellerOrderUpdateRequest request
	) {
		return orderService.updateSellerOrder(orderId, request);
	}
}
