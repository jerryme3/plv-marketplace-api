package com.marketplace.plv.payment;

import com.marketplace.plv.payment.dto.PaymentRequest;
import com.marketplace.plv.payment.dto.PaymentResponse;
import com.marketplace.plv.shared.Mapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class PaymentMapper implements Mapper<Payment, PaymentRequest, PaymentResponse> {

	private final EntityManager entityManager;

	@Override
	public Payment reqToEntity(PaymentRequest request) {
		var order = entityManager.getReference(com.marketplace.plv.order.Order.class, request.orderId());
		return Payment.builder().order(order).paymentMethod(request.paymentMethod())
				.amount(request.amount()).build();
	}

	@Override
	public PaymentResponse entityToResponse(Payment payment) {
		return new PaymentResponse(payment.getId(), payment.getOrder().getId(), payment.getPaymentMethod(),
				payment.getPaymentStatus(), payment.getAmount(), payment.getPaidAt());
	}
}
