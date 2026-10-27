package com.marketplace.plv.payment.dto;

import com.marketplace.plv.payment.PaymentMethod;
import com.marketplace.plv.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
		Long id,
		Long orderId,
		PaymentMethod paymentMethod,
		PaymentStatus paymentStatus,
		BigDecimal amount,
		Instant paidAt
) {
}
