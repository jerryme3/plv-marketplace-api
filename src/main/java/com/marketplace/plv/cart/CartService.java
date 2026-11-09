package com.marketplace.plv.cart;

import com.marketplace.plv.cartitem.dto.CartItemDeletionRequest;
import com.marketplace.plv.cartitem.CartItemMapper;
import com.marketplace.plv.cartitem.dto.CartItemResponse;
import com.marketplace.plv.shared.exceptions.notfound.CartNotFoundException;
import com.marketplace.plv.shared.exceptions.notfound.ItemNotFoundException;
import com.marketplace.plv.shared.exceptions.notfound.ProductNotFoundException;
import org.springframework.stereotype.Service;

import com.marketplace.plv.buyer.BuyerRepository;
import com.marketplace.plv.cartitem.CartItemRepository;
import com.marketplace.plv.cartitem.dto.CartItemRequest;
import com.marketplace.plv.product.ProductRepository;
import com.marketplace.plv.shared.auth.AuthCookieService;
import com.marketplace.plv.shared.entity.ApiResponse;
import com.marketplace.plv.shared.exceptions.ForbiddenAccessException;
import com.marketplace.plv.shared.exceptions.notfound.UserNotFoundException;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BuyerRepository buyerRepository;
    private final ProductRepository productRepository;

    private final AuthCookieService authCookieService;

    private final CartItemMapper cartItemMapper;

    ApiResponse<CartItemResponse> addToCart(CartItemRequest request) {
        if (!authCookieService.isValidSession()) throw new ForbiddenAccessException("Invalid session");
        if (productRepository.findById(request.productId()).isEmpty()) throw new ProductNotFoundException("Invalid product ID");

        var accountId = authCookieService.getUserIdFromContext();
        var buyer   = buyerRepository.findByAccountId(accountId).orElseThrow(() -> new UserNotFoundException("Invalid user ID"));
        var cart    = cartRepository.findByBuyerId(buyer.getId()).orElseThrow(() -> new CartNotFoundException("Invalid cart ID"));

        var cartItem = cartItemMapper.reqToEntity(request, cart.getId());
        var response = cartItemMapper.entityToResponse(cartItemRepository.save(cartItem));

        return ApiResponse.successful(response, "Product has been added to your cart!");
    }

    Map<String, Object> removeFromCart(CartItemDeletionRequest request) {
        if (!authCookieService.isValidSession()) throw new ForbiddenAccessException("Invalid session");

        var accountId = authCookieService.getUserIdFromContext();
        var buyer = buyerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new UserNotFoundException("Invalid user ID"));
        var cartItem = cartItemRepository.findById(request.cartItemId())
                .orElseThrow(() -> new ItemNotFoundException("Invalid item ID"));
        if (!cartItem.getCart().getBuyer().getId().equals(buyer.getId())) {
            throw new ForbiddenAccessException("You can only remove items from your own cart");
        }
        cartItemRepository.delete(cartItem);

        return Map.of("Message", "Product removed from cart");
    }

}