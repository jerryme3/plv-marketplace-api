package com.marketplace.plv.seller.dto;

import jakarta.validation.constraints.NotNull;

public record SellerRequest(@NotNull Long userId) {
}
