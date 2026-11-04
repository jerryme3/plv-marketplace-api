package com.marketplace.plv.shared.config;

import com.marketplace.plv.account.Account;
import com.marketplace.plv.account.AccountRepository;
import com.marketplace.plv.account.AccountRole;
import com.marketplace.plv.category.Category;
import com.marketplace.plv.category.CategoryRepository;
import com.marketplace.plv.product.Product;
import com.marketplace.plv.product.ProductRepository;
import com.marketplace.plv.product_stocks.ProductStocks;
import com.marketplace.plv.product_stocks.ProductStocksRepository;
import com.marketplace.plv.seller.Seller;
import com.marketplace.plv.seller.SellerRepository;
import com.marketplace.plv.store.Store;
import com.marketplace.plv.store.StoreRepository;
import com.marketplace.plv.user.User;
import com.marketplace.plv.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevelopmentDataSeeder implements ApplicationRunner {

	private static final String SELLER_EMAIL = "demo.seller@example.com";
	private static final String SELLER_USERNAME = "demo-seller";
	private static final String STORE_NAME = "Campus Tech Store";

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final SellerRepository sellerRepository;
	private final CategoryRepository categoryRepository;
	private final StoreRepository storeRepository;
	private final ProductRepository productRepository;
	private final ProductStocksRepository productStocksRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (accountRepository.existsByEmail(SELLER_EMAIL)) {
			return;
		}

		var account = accountRepository.save(Account.builder()
				.email(SELLER_EMAIL)
				.password(passwordEncoder.encode("Demo123!"))
				.role(AccountRole.ROLE_USER)
				.build());

		var user = userRepository.save(User.builder()
				.account(account)
				.username(SELLER_USERNAME)
				.active(true)
				.build());

		var seller = sellerRepository.save(Seller.builder()
				.user(user)
				.rating(new BigDecimal("4.75"))
				.verified(true)
				.build());

		var electronics = categoryRepository.findByName("Electronics")
				.orElseGet(() -> categoryRepository.save(Category.builder()
						.name("Electronics")
						.description("Computers, phones, accessories, and campus technology.")
						.build()));

		var books = categoryRepository.findByName("Books")
				.orElseGet(() -> categoryRepository.save(Category.builder()
						.name("Books")
						.description("Textbooks and study materials.")
						.build()));

		var store = storeRepository.findByName(STORE_NAME)
				.orElseGet(() -> storeRepository.save(Store.builder()
						.seller(seller)
						.name(STORE_NAME)
						.description("School essentials and technology for students.")
						.build()));

		seedProduct(electronics, store, "Wireless Study Headphones",
				"Comfortable wireless headphones for classes and study sessions.", 1499.00, 12);
		seedProduct(books, store, "Data Structures and Algorithms",
				"Sample textbook for computer science students.", 899.00, 8);
	}

	private void seedProduct(Category category, Store store, String title, String description,
							 double price, int stockQuantity) {
		if (productRepository.existsByTitleAndStore(title, store.getId())) {
			return;
		}

		var product = productRepository.save(Product.builder()
				.store(store)
				.category(category)
				.title(title)
				.description(description)
				.price(BigDecimal.valueOf(price))
				.build());

		var stock = new ProductStocks();
		stock.setProduct(product);
		stock.setStockQuantity(stockQuantity);
		productStocksRepository.save(stock);
	}
}
