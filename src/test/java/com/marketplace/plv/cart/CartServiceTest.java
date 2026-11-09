package com.marketplace.plv.cart;

import com.marketplace.plv.buyer.Buyer;
import com.marketplace.plv.buyer.BuyerRepository;
import com.marketplace.plv.cartitem.CartItem;
import com.marketplace.plv.cartitem.CartItemMapper;
import com.marketplace.plv.cartitem.CartItemRepository;
import com.marketplace.plv.cartitem.dto.CartItemDeletionRequest;
import com.marketplace.plv.cartitem.dto.CartItemRequest;
import com.marketplace.plv.cartitem.dto.CartItemResponse;
import com.marketplace.plv.product.ProductRepository;
import com.marketplace.plv.shared.auth.AuthCookieService;
import com.marketplace.plv.shared.exceptions.ForbiddenAccessException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceTest {

	@Test
	void addToCartUsesAuthenticatedBuyersCart() {
		var cartRepository = mock(CartRepository.class);
		var itemRepository = mock(CartItemRepository.class);
		var buyerRepository = mock(BuyerRepository.class);
		var productRepository = mock(ProductRepository.class);
		var auth = mock(AuthCookieService.class);
		var mapper = mock(CartItemMapper.class);
		var buyer = mock(Buyer.class);
		var cart = mock(Cart.class);
		var product = mock(com.marketplace.plv.product.Product.class);
		var item = mock(CartItem.class);
		when(auth.isValidSession()).thenReturn(true);
		when(auth.getUserIdFromContext()).thenReturn(70L);
		when(buyerRepository.findByAccountId(70L)).thenReturn(Optional.of(buyer));
		when(buyer.getId()).thenReturn(7L);
		when(cartRepository.findByBuyerId(7L)).thenReturn(Optional.of(cart));
		when(cart.getId()).thenReturn(21L);
		when(productRepository.findById(101L)).thenReturn(Optional.of(product));
		when(mapper.reqToEntity(new CartItemRequest(101L, 2), 21L)).thenReturn(item);
		when(itemRepository.save(item)).thenReturn(item);
		when(mapper.entityToResponse(item)).thenReturn(new CartItemResponse(1L, 21L, 101L, 2, null));

		new CartService(cartRepository, itemRepository, buyerRepository, productRepository, auth, mapper)
				.addToCart(new CartItemRequest(101L, 2));

		verify(itemRepository).save(item);
	}

	@Test
	void removeFromCartRejectsItemsOwnedByAnotherBuyer() {
		var buyerRepository = mock(BuyerRepository.class);
		var itemRepository = mock(CartItemRepository.class);
		var auth = mock(AuthCookieService.class);
		var buyer = mock(Buyer.class);
		var otherBuyer = mock(Buyer.class);
		var cart = mock(Cart.class);
		var item = mock(CartItem.class);
		when(auth.isValidSession()).thenReturn(true);
		when(auth.getUserIdFromContext()).thenReturn(70L);
		when(buyerRepository.findByAccountId(70L)).thenReturn(Optional.of(buyer));
		when(buyer.getId()).thenReturn(7L);
		when(otherBuyer.getId()).thenReturn(8L);
		when(cart.getBuyer()).thenReturn(otherBuyer);
		when(item.getCart()).thenReturn(cart);
		when(itemRepository.findById(4L)).thenReturn(Optional.of(item));

		var service = new CartService(
				mock(CartRepository.class),
				itemRepository,
				buyerRepository,
				mock(ProductRepository.class),
				auth,
				mock(CartItemMapper.class));

		assertThrows(ForbiddenAccessException.class,
				() -> service.removeFromCart(new CartItemDeletionRequest(4L)));
		verify(itemRepository, never()).delete(any(CartItem.class));
	}
}
