package com.marketplace.plv.orderitem.dto;

import java.math.BigDecimal;

public record OrderItemResponse(Long id, Long orderId, Long productId, int quantity, BigDecimal unitPrice) {
}
