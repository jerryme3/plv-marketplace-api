package com.marketplace.plv.order;

import com.marketplace.plv.buyer.Buyer;
import com.marketplace.plv.buyer.BuyerRepository;
import com.marketplace.plv.cart.Cart;
import com.marketplace.plv.cart.CartRepository;
import com.marketplace.plv.cartitem.CartItem;
import com.marketplace.plv.cartitem.CartItemRepository;
import com.marketplace.plv.order.dto.OrderStatusUpdateRequest;
import com.marketplace.plv.orderitem.OrderItem;
import com.marketplace.plv.orderitem.OrderItemMapper;
import com.marketplace.plv.orderitem.OrderItemRepository;
import com.marketplace.plv.product.Product;
import com.marketplace.plv.product_stocks.ProductStocks;
import com.marketplace.plv.product_stocks.ProductStocksRepository;
import com.marketplace.plv.seller.Seller;
import com.marketplace.plv.seller.SellerRepository;
import com.marketplace.plv.seller.dto.SellerOrderUpdateRequest;
import com.marketplace.plv.shared.auth.AuthCookieService;
import com.marketplace.plv.shared.exceptions.ForbiddenAccessException;
import com.marketplace.plv.shared.exceptions.conflict.ConflictException;
import com.marketplace.plv.store.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrderServiceTest {

	private final OrderRepository orderRepository = mock(OrderRepository.class);
	private final OrderItemRepository orderItemRepository = mock(OrderItemRepository.class);
	private final BuyerRepository buyerRepository = mock(BuyerRepository.class);
	private final SellerRepository sellerRepository = mock(SellerRepository.class);
	private final CartRepository cartRepository = mock(CartRepository.class);
	private final CartItemRepository cartItemRepository = mock(CartItemRepository.class);
	private final ProductStocksRepository productStocksRepository = mock(ProductStocksRepository.class);
	private final AuthCookieService authCookieService = mock(AuthCookieService.class);

	private final OrderService orderService = new OrderService(
			orderRepository,
			orderItemRepository,
			new OrderItemMapper(),
			new OrderMapper(),
			buyerRepository,
			sellerRepository,
			cartRepository,
			cartItemRepository,
			productStocksRepository,
			authCookieService);

	private Buyer buyer;

	@BeforeEach
	void setUp() {
		buyer = mock(Buyer.class);
		when(buyer.getId()).thenReturn(7L);
		when(buyer.getDeliveryBuilding()).thenReturn("Dorm North");
		when(authCookieService.getUserIdFromContext()).thenReturn(70L);
		when(buyerRepository.findByAccountId(70L)).thenReturn(Optional.of(buyer));
		when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(orderItemRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
		when(orderItemRepository.findAllByOrderIdOrderById(anyLong())).thenReturn(List.of());
	}

	@Test
	void checkoutSplitsBySellerSnapshotsPricesAndReservesAllItems() {
		var firstSeller = seller(11L);
		var secondSeller = seller(12L);
		var cart = mock(Cart.class);
		when(cart.getId()).thenReturn(21L);
		when(cartRepository.findByBuyerId(7L)).thenReturn(Optional.of(cart));
		var first = cartItem(product(101L, firstSeller, "Notebook", "4.50"), 2);
		var second = cartItem(product(102L, firstSeller, "Pen", "1.25"), 4);
		var third = cartItem(product(103L, secondSeller, "Binder", "8.00"), 1);
		when(cartItemRepository.findAllByCartIdWithProductAndSeller(21L))
				.thenReturn(List.of(first, second, third));
		var firstStock = stock(first.getProduct(), 3);
		var secondStock = stock(first.getProduct(), 5);
		var thirdStock = stock(product(102L, firstSeller, "Pen", "1.25"), 10);
		var fourthStock = stock(product(103L, secondSeller, "Binder", "8.00"), 10);
		when(productStocksRepository.findAllByProductIdForUpdate(101L)).thenReturn(List.of(firstStock, secondStock));
		when(productStocksRepository.findAllByProductIdForUpdate(102L)).thenReturn(List.of(thirdStock));
		when(productStocksRepository.findAllByProductIdForUpdate(103L)).thenReturn(List.of(fourthStock));

		var result = orderService.checkout();

		assertEquals(2, result.body().size());
		assertEquals(new BigDecimal("14.00"), result.body().get(0).totalAmount());
		assertEquals(new BigDecimal("8.00"), result.body().get(1).totalAmount());
		assertEquals("Dorm North", result.body().get(0).deliveryLocation());
		assertEquals(2, result.body().get(0).items().size());
		assertEquals("Notebook", result.body().get(0).items().get(0).productTitle());
		assertEquals(new BigDecimal("4.50"), result.body().get(0).items().get(0).unitPrice());
		assertEquals(1, firstStock.getStockQuantity());
		assertEquals(5, secondStock.getStockQuantity());
		assertEquals(6, thirdStock.getStockQuantity());
		assertEquals(9, fourthStock.getStockQuantity());
		verify(cartItemRepository).deleteAllByCartId(21L);
	}

	@Test
	void checkoutDoesNotCreateOrdersOrClearCartWhenStockIsInsufficient() {
		var seller = seller(11L);
		var cart = mock(Cart.class);
		when(cart.getId()).thenReturn(21L);
		when(cartRepository.findByBuyerId(7L)).thenReturn(Optional.of(cart));
		var first = cartItem(product(101L, seller, "Notebook", "4.50"), 2);
		var second = cartItem(product(102L, seller, "Pen", "1.25"), 4);
		when(cartItemRepository.findAllByCartIdWithProductAndSeller(21L)).thenReturn(List.of(first, second));
		var firstStock = stock(first.getProduct(), 10);
		var secondStock = stock(second.getProduct(), 3);
		when(productStocksRepository.findAllByProductIdForUpdate(101L)).thenReturn(List.of(firstStock));
		when(productStocksRepository.findAllByProductIdForUpdate(102L)).thenReturn(List.of(secondStock));

		assertThrows(ConflictException.class, () -> orderService.checkout());

		verify(orderRepository, never()).save(any());
		verify(cartItemRepository, never()).deleteAllByCartId(anyLong());
	}

	@Test
	void buyerCanCancelOwnOrderAndReservedStockIsRestored() {
		var product = product(101L, seller(11L), "Notebook", "4.50");
		var order = Order.builder()
				.buyer(buyer)
				.seller(seller(11L))
				.totalAmount(new BigDecimal("9.00"))
				.status(OrderStatus.PENDING)
				.build();
		when(orderRepository.findByIdForUpdate(41L)).thenReturn(Optional.of(order));
		var item = OrderItem.builder()
				.order(order)
				.product(product)
				.productTitle("Notebook")
				.quantity(2)
				.unitPrice(new BigDecimal("4.50"))
				.build();
		when(orderItemRepository.findAllByOrderIdOrderById(41L)).thenReturn(List.of(item));
		var stock = stock(product, 2);
		when(productStocksRepository.findAllByProductIdForUpdate(101L)).thenReturn(List.of(stock));

		orderService.updateBuyerOrderStatus(41L, new OrderStatusUpdateRequest(OrderStatus.CANCELLED));

		assertEquals(OrderStatus.CANCELLED, order.getStatus());
		assertEquals(4, stock.getStockQuantity());
	}

	@Test
	void buyerCanRefundPaidOrderAndReservedStockIsRestored() {
		var product = product(101L, seller(11L), "Notebook", "4.50");
		var order = Order.builder()
				.buyer(buyer)
				.seller(seller(11L))
				.totalAmount(new BigDecimal("4.50"))
				.status(OrderStatus.PAID)
				.build();
		when(orderRepository.findByIdForUpdate(41L)).thenReturn(Optional.of(order));
		var item = OrderItem.builder()
				.order(order)
				.product(product)
				.productTitle("Notebook")
				.quantity(1)
				.unitPrice(new BigDecimal("4.50"))
				.build();
		when(orderItemRepository.findAllByOrderIdOrderById(41L)).thenReturn(List.of(item));
		var stock = stock(product, 2);
		when(productStocksRepository.findAllByProductIdForUpdate(101L)).thenReturn(List.of(stock));

		orderService.updateBuyerOrderStatus(41L, new OrderStatusUpdateRequest(OrderStatus.REFUNDED));

		assertEquals(OrderStatus.REFUNDED, order.getStatus());
		assertEquals(3, stock.getStockQuantity());
	}

	@Test
	void buyerCannotCancelAnOrderAfterPayment() {
		var order = Order.builder()
				.buyer(buyer)
				.totalAmount(BigDecimal.ONE)
				.status(OrderStatus.PAID)
				.build();
		when(orderRepository.findByIdForUpdate(41L)).thenReturn(Optional.of(order));

		assertThrows(ConflictException.class,
				() -> orderService.updateBuyerOrderStatus(41L, new OrderStatusUpdateRequest(OrderStatus.CANCELLED)));

		assertEquals(OrderStatus.PAID, order.getStatus());
		verify(productStocksRepository, never()).findAllByProductIdForUpdate(anyLong());
	}

	@Test
	void buyerCannotUpdateAnotherBuyersOrder() {
		var otherBuyer = mock(Buyer.class);
		when(otherBuyer.getId()).thenReturn(8L);
		var order = Order.builder()
				.buyer(otherBuyer)
				.totalAmount(BigDecimal.ONE)
				.status(OrderStatus.PENDING)
				.build();
		when(orderRepository.findByIdForUpdate(41L)).thenReturn(Optional.of(order));

		assertThrows(ForbiddenAccessException.class,
				() -> orderService.updateBuyerOrderStatus(41L, new OrderStatusUpdateRequest(OrderStatus.CANCELLED)));

		verify(productStocksRepository, never()).findAllByProductIdForUpdate(anyLong());
	}

	@Test
	void sellerCanAdvanceItsOrderAndUpdateCampusLocation() {
		var seller = seller(11L);
		var order = Order.builder()
				.buyer(buyer)
				.seller(seller)
				.totalAmount(BigDecimal.ONE)
				.status(OrderStatus.PENDING)
				.build();
		when(sellerRepository.findByAccountId(70L)).thenReturn(Optional.of(seller));
		when(orderRepository.findByIdForUpdate(41L)).thenReturn(Optional.of(order));

		orderService.updateSellerOrder(41L, new SellerOrderUpdateRequest(OrderStatus.PAID, "Campus library"));

		assertEquals(OrderStatus.PAID, order.getStatus());
		assertEquals("Campus library", order.getCurrentLocation());
	}

	@Test
	void sellerCanAdvanceOrderThroughTheCompleteLifecycle() {
		var seller = seller(11L);
		var order = Order.builder()
				.buyer(buyer)
				.seller(seller)
				.totalAmount(BigDecimal.ONE)
				.status(OrderStatus.PENDING)
				.build();
		when(sellerRepository.findByAccountId(70L)).thenReturn(Optional.of(seller));
		when(orderRepository.findByIdForUpdate(41L)).thenReturn(Optional.of(order));

		for (var status : List.of(
				OrderStatus.PAID,
				OrderStatus.CONFIRMED,
				OrderStatus.PACKED,
				OrderStatus.SHIPPED,
				OrderStatus.COMPLETED)) {
			orderService.updateSellerOrder(41L, new SellerOrderUpdateRequest(status, null));
		}

		assertEquals(OrderStatus.COMPLETED, order.getStatus());
	}

	@Test
	void sellerCannotSkipLifecycleStates() {
		var seller = seller(11L);
		var order = Order.builder()
				.buyer(buyer)
				.seller(seller)
				.totalAmount(BigDecimal.ONE)
				.status(OrderStatus.PENDING)
				.build();
		when(sellerRepository.findByAccountId(70L)).thenReturn(Optional.of(seller));
		when(orderRepository.findByIdForUpdate(41L)).thenReturn(Optional.of(order));

		assertThrows(ConflictException.class,
				() -> orderService.updateSellerOrder(41L, new SellerOrderUpdateRequest(OrderStatus.PACKED, null)));

		assertEquals(OrderStatus.PENDING, order.getStatus());
	}

	@Test
	void sellerCannotUpdateAnotherSellersOrder() {
		var seller = seller(11L);
		var anotherSeller = seller(12L);
		var order = Order.builder()
				.buyer(buyer)
				.seller(anotherSeller)
				.totalAmount(BigDecimal.ONE)
				.status(OrderStatus.PENDING)
				.build();
		when(sellerRepository.findByAccountId(70L)).thenReturn(Optional.of(seller));
		when(orderRepository.findByIdForUpdate(41L)).thenReturn(Optional.of(order));

		assertThrows(ForbiddenAccessException.class,
				() -> orderService.updateSellerOrder(41L, new SellerOrderUpdateRequest(null, "Student center")));
	}

	@Test
	void sellerCannotChangeLogisticsAfterOrderCompletion() {
		var seller = seller(11L);
		var order = Order.builder()
				.buyer(buyer)
				.seller(seller)
				.totalAmount(BigDecimal.ONE)
				.status(OrderStatus.COMPLETED)
				.build();
		when(sellerRepository.findByAccountId(70L)).thenReturn(Optional.of(seller));
		when(orderRepository.findByIdForUpdate(41L)).thenReturn(Optional.of(order));

		assertThrows(ConflictException.class,
				() -> orderService.updateSellerOrder(41L, new SellerOrderUpdateRequest(null, "Campus library")));
	}

	private static Seller seller(Long sellerId) {
		var seller = mock(Seller.class);
		when(seller.getId()).thenReturn(sellerId);
		return seller;
	}

	private static Product product(Long productId, Seller seller, String title, String price) {
		var store = mock(Store.class);
		when(store.getSeller()).thenReturn(seller);
		var product = mock(Product.class);
		when(product.getId()).thenReturn(productId);
		when(product.getStore()).thenReturn(store);
		when(product.getTitle()).thenReturn(title);
		when(product.getPrice()).thenReturn(new BigDecimal(price));
		return product;
	}

	private static CartItem cartItem(Product product, int quantity) {
		var cartItem = mock(CartItem.class);
		when(cartItem.getProduct()).thenReturn(product);
		when(cartItem.getQuantity()).thenReturn(quantity);
		return cartItem;
	}

	private static ProductStocks stock(Product product, int quantity) {
		var stock = new ProductStocks();
		stock.setProduct(product);
		stock.setStockQuantity(quantity);
		return stock;
	}
}
