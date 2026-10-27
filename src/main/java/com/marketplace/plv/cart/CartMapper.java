package com.marketplace.plv.cart;

import com.marketplace.plv.cart.dto.CartRequest;
import com.marketplace.plv.cart.dto.CartResponse;
import com.marketplace.plv.shared.Mapper;
import org.springframework.stereotype.Component;

@Component
public final class CartMapper implements Mapper<Cart, CartRequest, CartResponse> {
}
