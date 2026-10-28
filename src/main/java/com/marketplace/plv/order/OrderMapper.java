package com.marketplace.plv.order;

import com.marketplace.plv.order.dto.OrderRequest;
import com.marketplace.plv.order.dto.OrderResponse;
import com.marketplace.plv.shared.Mapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class OrderMapper implements Mapper<Order, OrderRequest, OrderResponse> {

	private final EntityManager entityManager;

	@Override
	public Order reqToEntity(OrderRequest request) {
		var buyer = entityManager.getReference(com.marketplace.plv.buyer.Buyer.class, request.buyerId());
		return Order.builder().buyer(buyer).totalAmount(request.totalAmount()).build();
	}

	@Override
	public OrderResponse entityToResponse(Order order) {
		return new OrderResponse(order.getId(), order.getBuyer().getId(), order.getTotalAmount(),
				order.getStatus(), order.getCreatedAt(), order.getUpdatedAt());
	}
}
