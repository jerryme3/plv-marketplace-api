package com.marketplace.plv.orderitem;

import com.marketplace.plv.orderitem.dto.OrderItemRequest;
import com.marketplace.plv.orderitem.dto.OrderItemResponse;
import com.marketplace.plv.shared.Mapper;
import org.springframework.stereotype.Component;

@Component
public final class OrderItemMapper implements Mapper<OrderItem, OrderItemRequest, OrderItemResponse> {
}
