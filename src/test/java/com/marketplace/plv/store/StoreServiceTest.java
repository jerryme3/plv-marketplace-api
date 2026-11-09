package com.marketplace.plv.store;

import com.marketplace.plv.product.Product;
import com.marketplace.plv.product.ProductMapper;
import com.marketplace.plv.product.ProductRepository;
import com.marketplace.plv.product.dto.ProductRequest;
import com.marketplace.plv.product_stocks.ProductStockMapper;
import com.marketplace.plv.product_stocks.ProductStocks;
import com.marketplace.plv.product_stocks.ProductStocksRepository;
import com.marketplace.plv.product_stocks.dto.ProductStockResponse;
import com.marketplace.plv.seller.Seller;
import com.marketplace.plv.seller.SellerRepository;
import com.marketplace.plv.shared.auth.AuthCookieService;
import com.marketplace.plv.shared.exceptions.ForbiddenAccessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StoreServiceTest {

	private static final ProductRequest REQUEST =
			new ProductRequest(8L, 3L, "Campus notebook", "A ruled notebook", new BigDecimal("4.25"), 10);

	@Test
	void sellerCanPostProductToOwnedStore() {
		var sellers = mock(SellerRepository.class);
		var stores = mock(StoreRepository.class);
		var stocks = mock(ProductStocksRepository.class);
		var products = mock(ProductRepository.class);
		var productMapper = mock(ProductMapper.class);
		var stockMapper = mock(ProductStockMapper.class);
		var auth = mock(AuthCookieService.class);
		var seller = mock(Seller.class);
		var store = mock(Store.class);
		var product = mock(Product.class);
		var stock = new ProductStocks();
		when(auth.isValidSession()).thenReturn(true);
		when(auth.getUserIdFromContext()).thenReturn(70L);
		when(sellers.findByAccountId(70L)).thenReturn(Optional.of(seller));
		when(seller.getId()).thenReturn(5L);
		when(stores.findById(8L)).thenReturn(Optional.of(store));
		when(store.getSeller()).thenReturn(seller);
		when(productMapper.reqToEntity(REQUEST)).thenReturn(product);
		when(products.save(product)).thenReturn(product);
		when(stockMapper.reqToEntity(any())).thenReturn(stock);
		when(stockMapper.entityToResponse(stock)).thenReturn(new ProductStockResponse(null, 10));

		var service = service(sellers, stores, stocks, products, productMapper, stockMapper, auth);
		service.sellProducts(REQUEST);

		verify(products).save(product);
		verify(stocks).save(stock);
	}

	@Test
	void sellerCannotPostProductToAnotherSellersStore() {
		var sellers = mock(SellerRepository.class);
		var stores = mock(StoreRepository.class);
		var auth = mock(AuthCookieService.class);
		var seller = mock(Seller.class);
		var store = mock(Store.class);
		when(sellers.findByAccountId(70L)).thenReturn(Optional.of(seller));
		when(seller.getId()).thenReturn(5L);
		when(stores.findById(8L)).thenReturn(Optional.of(store));
		when(store.getSeller()).thenReturn(mock(Seller.class));
		when(auth.isValidSession()).thenReturn(true);
		when(auth.getUserIdFromContext()).thenReturn(70L);

		var service = service(
				sellers, stores, mock(ProductStocksRepository.class), mock(ProductRepository.class),
				mock(ProductMapper.class), mock(ProductStockMapper.class), auth);

		assertThrows(ForbiddenAccessException.class, () -> service.sellProducts(REQUEST));
	}

	private static StoreService service(
			SellerRepository sellers,
			StoreRepository stores,
			ProductStocksRepository stocks,
			ProductRepository products,
			ProductMapper productMapper,
			ProductStockMapper stockMapper,
			AuthCookieService auth
	) {
		return new StoreService(
				sellers, stores, stocks, products, mock(StoreMapper.class),
				productMapper, stockMapper, auth);
	}

}
