package com.marketplace.plv.orderitem;

import com.marketplace.plv.cartitem.CartItem;
import com.marketplace.plv.order.Order;
import com.marketplace.plv.orderitem.dto.OrderItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class OrderItemMapper {

	public OrderItem fromCartItem(Order order, CartItem cartItem) {
		var product = cartItem.getProduct();
		return OrderItem.builder()
				.order(order)
				.product(product)
				.productTitle(product.getTitle())
				.quantity(cartItem.getQuantity())
				.unitPrice(product.getPrice())
				.build();
	}

	public OrderItemResponse entityToResponse(OrderItem orderItem) {
		return new OrderItemResponse(
				orderItem.getId(),
				orderItem.getOrder().getId(),
				orderItem.getProduct().getId(),
				orderItem.getProductTitle(),
				orderItem.getQuantity(),
				orderItem.getUnitPrice()
		);
	}
}
