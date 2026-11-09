package com.marketplace.plv.order;

import com.marketplace.plv.account.Account;
import com.marketplace.plv.account.AccountRepository;
import com.marketplace.plv.buyer.Buyer;
import com.marketplace.plv.buyer.BuyerRepository;
import com.marketplace.plv.cart.Cart;
import com.marketplace.plv.cart.CartRepository;
import com.marketplace.plv.cartitem.CartItem;
import com.marketplace.plv.cartitem.CartItemRepository;
import com.marketplace.plv.category.Category;
import com.marketplace.plv.category.CategoryRepository;
import com.marketplace.plv.orderitem.OrderItemRepository;
import com.marketplace.plv.product.Product;
import com.marketplace.plv.product.ProductRepository;
import com.marketplace.plv.product_stocks.ProductStocks;
import com.marketplace.plv.product_stocks.ProductStocksRepository;
import com.marketplace.plv.seller.Seller;
import com.marketplace.plv.seller.SellerRepository;
import com.marketplace.plv.seller.dto.SellerOrderUpdateRequest;
import com.marketplace.plv.shared.exceptions.conflict.ConflictException;
import com.marketplace.plv.store.Store;
import com.marketplace.plv.store.StoreRepository;
import com.marketplace.plv.user.User;
import com.marketplace.plv.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CheckoutTransactionIntegrationTest {

	@Autowired private OrderService orderService;
	@Autowired private OrderRepository orderRepository;
	@Autowired private OrderItemRepository orderItemRepository;
	@Autowired private AccountRepository accountRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private BuyerRepository buyerRepository;
	@Autowired private SellerRepository sellerRepository;
	@Autowired private CategoryRepository categoryRepository;
	@Autowired private StoreRepository storeRepository;
	@Autowired private ProductRepository productRepository;
	@Autowired private ProductStocksRepository productStocksRepository;
	@Autowired private CartRepository cartRepository;
	@Autowired private CartItemRepository cartItemRepository;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void insufficientStockRollsBackEveryReservationAndPreservesCart() {
		var buyerAccount = saveAccount("rollback-buyer@example.test");
		var buyerUser = userRepository.save(User.builder()
				.account(buyerAccount).username("rollback-buyer").active(true).build());
		var buyer = buyerRepository.save(Buyer.builder().user(buyerUser).deliveryBuilding("North Dorm").build());
		var sellerAccount = saveAccount("rollback-seller@example.test");
		var sellerUser = userRepository.save(User.builder()
				.account(sellerAccount).username("rollback-seller").active(true).build());
		var seller = sellerRepository.save(Seller.builder().user(sellerUser).build());
		var category = categoryRepository.save(Category.builder().name("Rollback test").build());
		var store = storeRepository.save(Store.builder().seller(seller).name("Rollback test store").build());
		var firstProduct = saveProduct(store, category, "Rollback product one", "2.00");
		var secondProduct = saveProduct(store, category, "Rollback product two", "3.00");
		saveStock(firstProduct, 5);
		saveStock(secondProduct, 1);
		var cart = cartRepository.save(Cart.builder().buyer(buyer).build());
		cartItemRepository.save(CartItem.builder().cart(cart).product(firstProduct).quantity(2).build());
		cartItemRepository.save(CartItem.builder().cart(cart).product(secondProduct).quantity(2).build());
		SecurityContextHolder.getContext().setAuthentication(
				UsernamePasswordAuthenticationToken.authenticated(
						buyerAccount.getId().toString(), null, List.of()));

		assertThrows(ConflictException.class, () -> orderService.checkout());

		var stockByProductId = productStocksRepository.findAllByProductIds(
						List.of(firstProduct.getId(), secondProduct.getId())).stream()
				.collect(java.util.stream.Collectors.toMap(
						stock -> stock.getProduct().getId(), ProductStocks::getStockQuantity));
		assertEquals(5, stockByProductId.get(firstProduct.getId()));
		assertEquals(1, stockByProductId.get(secondProduct.getId()));
		assertEquals(0, orderRepository.count());
		assertEquals(0, orderItemRepository.count());
		assertEquals(2, cartItemRepository.count());
	}

	@Test
	void sellerCanUpdateAnOrderUnderTheDatabaseLock() {
		var unique = UUID.randomUUID().toString();
		var buyerAccount = saveAccount("buyer-" + unique + "@example.test");
		var buyerUser = userRepository.save(User.builder()
				.account(buyerAccount).username("buyer-" + unique).active(true).build());
		var buyer = buyerRepository.save(Buyer.builder().user(buyerUser).build());
		var sellerAccount = saveAccount("seller-" + unique + "@example.test");
		var sellerUser = userRepository.save(User.builder()
				.account(sellerAccount).username("seller-" + unique).active(true).build());
		var seller = sellerRepository.save(Seller.builder().user(sellerUser).build());
		var order = orderRepository.save(Order.builder()
				.buyer(buyer)
				.seller(seller)
				.totalAmount(new BigDecimal("12.00"))
				.status(OrderStatus.PENDING)
				.build());
		SecurityContextHolder.getContext().setAuthentication(
				UsernamePasswordAuthenticationToken.authenticated(
						sellerAccount.getId().toString(), null, List.of()));

		orderService.updateSellerOrder(
				order.getId(), new SellerOrderUpdateRequest(OrderStatus.PAID, "Campus library"));

		var updated = orderRepository.findById(order.getId()).orElseThrow();
		assertEquals(OrderStatus.PAID, updated.getStatus());
		assertEquals("Campus library", updated.getCurrentLocation());
	}

	private Account saveAccount(String email) {
		return accountRepository.save(Account.builder()
				.email(email)
				.password("not-used-by-this-test")
				.build());
	}

	private Product saveProduct(Store store, Category category, String title, String price) {
		return productRepository.save(Product.builder()
				.store(store)
				.category(category)
				.title(title)
				.price(new BigDecimal(price))
				.build());
	}

	private void saveStock(Product product, int quantity) {
		var stock = new ProductStocks();
		stock.setProduct(product);
		stock.setStockQuantity(quantity);
		productStocksRepository.save(stock);
	}
}
