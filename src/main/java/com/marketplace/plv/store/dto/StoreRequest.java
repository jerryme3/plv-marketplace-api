package com.marketplace.plv.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StoreRequest(
		@NotNull Long sellerId,
		@NotBlank @Size(max = 100) String name,
		String description
) {
}
