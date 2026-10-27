package com.marketplace.plv.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(
		@NotNull Long storeId,
		@NotNull Long categoryId,
		@NotBlank @Size(max = 100) String title,
		String description,
		@NotNull @DecimalMin("0.00") BigDecimal price,
		@PositiveOrZero int stockQuantity
) {
}
