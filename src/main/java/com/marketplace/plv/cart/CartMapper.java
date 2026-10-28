package com.marketplace.plv.cart;

import com.marketplace.plv.buyer.Buyer;
import com.marketplace.plv.cart.dto.CartRequest;
import com.marketplace.plv.cart.dto.CartResponse;
import com.marketplace.plv.shared.Mapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class CartMapper implements Mapper<Cart, CartRequest, CartResponse> {

	private final EntityManager entityManager;

	@Override
	public Cart reqToEntity(CartRequest request) {
		var buyer = entityManager.getReference(Buyer.class, request.buyerId());
		return Cart.builder().buyer(buyer).build();
	}

	@Override
	public CartResponse entityToResponse(Cart cart) {
		return new CartResponse(cart.getId(), cart.getBuyer().getId(), cart.getCreatedAt(), cart.getUpdatedAt());
	}
}
