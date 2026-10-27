package com.marketplace.plv.order;

import com.marketplace.plv.order.dto.OrderRequest;
import com.marketplace.plv.order.dto.OrderResponse;
import com.marketplace.plv.shared.Mapper;
import org.springframework.stereotype.Component;

@Component
public final class OrderMapper implements Mapper<Order, OrderRequest, OrderResponse> {
}
