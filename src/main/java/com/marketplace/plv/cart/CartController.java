package com.marketplace.plv.cart;

import com.marketplace.plv.cartitem.dto.CartItemDeletionRequest;
import com.marketplace.plv.cartitem.dto.CartItemRequest;
import com.marketplace.plv.cartitem.dto.CartItemResponse;
import com.marketplace.plv.shared.entity.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@PostMapping
	ApiResponse<CartItemResponse> addToCart(@Valid @RequestBody CartItemRequest request) {
		return cartService.addToCart(request);
	}

	@DeleteMapping
	Map<String, Object> removeFromCart(@Valid @RequestBody CartItemDeletionRequest request) {
		return cartService.removeFromCart(request);
	}


}
