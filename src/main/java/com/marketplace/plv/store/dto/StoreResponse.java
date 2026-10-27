package com.marketplace.plv.store.dto;

import java.time.Instant;

public record StoreResponse(Long id, Long sellerId, String name, String description, Instant createdAt) {
}
