package com.marketplace.plv.cartitem;

import com.marketplace.plv.cartitem.dto.CartItemRequest;
import com.marketplace.plv.cartitem.dto.CartItemResponse;
import com.marketplace.plv.shared.Mapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class CartItemMapper implements Mapper<CartItem, CartItemRequest, CartItemResponse> {

	private final EntityManager entityManager;

	@Override
	public CartItem reqToEntity(CartItemRequest request) {
		var cart = entityManager.getReference(com.marketplace.plv.cart.Cart.class, request.cartId());
		var product = entityManager.getReference(com.marketplace.plv.product.Product.class, request.productId());
		return CartItem.builder().cart(cart).product(product).quantity(request.quantity()).build();
	}

	@Override
	public CartItemResponse entityToResponse(CartItem cartItem) {
		return new CartItemResponse(cartItem.getId(), cartItem.getCart().getId(), cartItem.getProduct().getId(),
				cartItem.getQuantity(), cartItem.getAddedAt());
	}
}
