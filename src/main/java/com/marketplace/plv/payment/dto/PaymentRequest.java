package com.marketplace.plv.payment.dto;

import com.marketplace.plv.payment.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
		@NotNull Long orderId,
		@NotNull PaymentMethod paymentMethod,
		@NotNull @DecimalMin("0.00") BigDecimal amount
) {
}
