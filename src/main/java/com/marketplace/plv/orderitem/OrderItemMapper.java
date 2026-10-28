package com.marketplace.plv.orderitem;

import com.marketplace.plv.orderitem.dto.OrderItemRequest;
import com.marketplace.plv.orderitem.dto.OrderItemResponse;
import com.marketplace.plv.shared.Mapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class OrderItemMapper implements Mapper<OrderItem, OrderItemRequest, OrderItemResponse> {

	private final EntityManager entityManager;

	@Override
	public OrderItem reqToEntity(OrderItemRequest request) {
		var order = entityManager.getReference(com.marketplace.plv.order.Order.class, request.orderId());
		var product = entityManager.getReference(com.marketplace.plv.product.Product.class, request.productId());
		return OrderItem.builder().order(order).product(product).quantity(request.quantity())
				.unitPrice(request.unitPrice()).build();
	}

	@Override
	public OrderItemResponse entityToResponse(OrderItem orderItem) {
		return new OrderItemResponse(orderItem.getId(), orderItem.getOrder().getId(), orderItem.getProduct().getId(),
				orderItem.getQuantity(), orderItem.getUnitPrice());
	}
}
