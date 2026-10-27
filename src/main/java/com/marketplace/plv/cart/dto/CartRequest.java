package com.marketplace.plv.cart.dto;

import jakarta.validation.constraints.NotNull;

public record CartRequest(@NotNull Long buyerId) {
}
