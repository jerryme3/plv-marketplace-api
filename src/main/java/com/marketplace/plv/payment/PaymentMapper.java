package com.marketplace.plv.payment;

import com.marketplace.plv.payment.dto.PaymentRequest;
import com.marketplace.plv.payment.dto.PaymentResponse;
import com.marketplace.plv.shared.Mapper;
import org.springframework.stereotype.Component;

@Component
public final class PaymentMapper implements Mapper<Payment, PaymentRequest, PaymentResponse> {
}
