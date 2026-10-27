package com.marketplace.plv.cartitem;

import com.marketplace.plv.cartitem.dto.CartItemRequest;
import com.marketplace.plv.cartitem.dto.CartItemResponse;
import com.marketplace.plv.shared.Mapper;
import org.springframework.stereotype.Component;

@Component
public final class CartItemMapper implements Mapper<CartItem, CartItemRequest, CartItemResponse> {
}
