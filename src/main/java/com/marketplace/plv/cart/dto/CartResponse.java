package com.marketplace.plv.cart.dto;

import java.time.Instant;

public record CartResponse(Long id, Long buyerId, Instant createdAt, Instant updatedAt) {
}
