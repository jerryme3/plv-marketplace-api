package com.marketplace.plv.product;

import com.marketplace.plv.category.Category;
import com.marketplace.plv.product_stocks.ProductStocks;
import com.marketplace.plv.product_stocks.ProductStocksRepository;
import com.marketplace.plv.shared.exceptions.notfound.NotFoundException;
import com.marketplace.plv.store.Store;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductBrowseServiceTest {

	@Test
	void browseTrimsSearchAndSumsStockRows() {
		var productRepository = mock(ProductRepository.class);
		var stocksRepository = mock(ProductStocksRepository.class);
		var service = new ProductBrowseService(productRepository, stocksRepository);
		var product = product(10L);
		when(productRepository.browse(eq("%notebook%"), eq(2L), eq(3L), any()))
				.thenReturn(new PageImpl<>(List.of(product)));
		var firstStock = mock(ProductStocks.class);
		var secondStock = mock(ProductStocks.class);
		when(firstStock.getProduct()).thenReturn(product);
		when(firstStock.getStockQuantity()).thenReturn(2);
		when(secondStock.getProduct()).thenReturn(product);
		when(secondStock.getStockQuantity()).thenReturn(3);
		when(stocksRepository.findAllByProductIds(List.of(10L))).thenReturn(List.of(firstStock, secondStock));

		var result = service.browse("  notebook  ", 2L, 3L, 0, 10, "price", "asc");

		assertEquals(5, result.getContent().getFirst().stockQuantity());
		verify(productRepository).browse(eq("%notebook%"), eq(2L), eq(3L), any(PageRequest.class));
	}

	@Test
	void browseUsesSafeDefaultsForUnknownSortAndDirection() {
		var productRepository = mock(ProductRepository.class);
		var stocksRepository = mock(ProductStocksRepository.class);
		when(productRepository.browse(isNull(), isNull(), isNull(), any()))
				.thenReturn(new PageImpl<>(List.of()));

		new ProductBrowseService(productRepository, stocksRepository)
				.browse(null, null, null, 0, 10, "bad-field", "sideways");

		verify(productRepository).browse(isNull(), isNull(), isNull(), any(PageRequest.class));
	}

	@Test
	void getByIdThrowsWhenProductDoesNotExist() {
		var productRepository = mock(ProductRepository.class);
		when(productRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class,
				() -> new ProductBrowseService(productRepository, mock(ProductStocksRepository.class)).getById(99L));
	}

	private static Product product(Long id) {
		var category = mock(Category.class);
		when(category.getId()).thenReturn(2L);
		when(category.getName()).thenReturn("Books");
		var store = mock(Store.class);
		when(store.getId()).thenReturn(3L);
		when(store.getName()).thenReturn("Campus store");
		var product = mock(Product.class);
		when(product.getId()).thenReturn(id);
		when(product.getTitle()).thenReturn("Notebook");
		when(product.getPrice()).thenReturn(new BigDecimal("3.50"));
		when(product.getCategory()).thenReturn(category);
		when(product.getStore()).thenReturn(store);
		return product;
	}
}
