package com.marketplace.plv.cartitem.dto;

import java.time.Instant;

public record CartItemResponse(Long id, Long cartId, Long productId, int quantity, Instant addedAt) {
}
