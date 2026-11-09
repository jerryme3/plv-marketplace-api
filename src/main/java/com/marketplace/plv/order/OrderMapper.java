package com.marketplace.plv.order;

import com.marketplace.plv.buyer.Buyer;
import com.marketplace.plv.order.dto.OrderResponse;
import com.marketplace.plv.orderitem.dto.OrderItemResponse;
import com.marketplace.plv.seller.Seller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public final class OrderMapper {

	public Order createOrder(Buyer buyer, Seller seller, BigDecimal totalAmount, String deliveryLocation) {
		return Order.builder()
				.buyer(buyer)
				.seller(seller)
				.totalAmount(totalAmount)
				.deliveryLocation(deliveryLocation)
				.build();
	}

	public OrderResponse entityToResponse(Order order, List<OrderItemResponse> items) {
		return new OrderResponse(
				order.getId(),
				order.getBuyer().getId(),
				order.getSeller() == null ? null : order.getSeller().getId(),
				order.getTotalAmount(),
				order.getStatus(),
				order.getDeliveryLocation(),
				order.getCurrentLocation(),
				List.copyOf(items),
				order.getCreatedAt(),
				order.getUpdatedAt()
		);
	}
}
