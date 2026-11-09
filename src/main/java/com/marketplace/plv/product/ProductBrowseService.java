package com.marketplace.plv.product;

import com.marketplace.plv.product.dto.CategorySummary;
import com.marketplace.plv.product.dto.ProductBrowseResponse;
import com.marketplace.plv.product.dto.StoreSummary;
import com.marketplace.plv.product_stocks.ProductStocksRepository;
import com.marketplace.plv.shared.exceptions.notfound.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductBrowseService {

	private static final Map<String, String> SORT_FIELDS = Map.of(
			"title", "title",
			"price", "price",
			"createdAt", "createdAt",
			"updatedAt", "updatedAt"
	);

	private static final Set<String> VALID_DIRECTIONS = Set.of("asc", "desc");

	private final ProductRepository productRepository;
	private final ProductStocksRepository productStocksRepository;

	public Page<ProductBrowseResponse> browse(
			String search,
			Long categoryId,
			Long storeId,
			int page,
			int size,
			String sort,
			String direction
	) {
		String normalizedSort = SORT_FIELDS.getOrDefault(sort, "createdAt");
		String normalizedDirection = VALID_DIRECTIONS.contains(direction) ? direction : "desc";
		var pageable = PageRequest.of(
				page,
				size,
				normalizedDirection.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
				normalizedSort
		);
		String normalizedSearch = search == null || search.isBlank() ? null : search.trim();
		String searchPattern = normalizedSearch == null ? null : "%" + normalizedSearch + "%";

		var productPage = productRepository.browse(searchPattern, categoryId, storeId, pageable);
		var productIds = productPage.getContent().stream()
				.map(Product::getId)
				.toList();
		Map<Long, Integer> stocksByProductId = Collections.emptyMap();
		if (!productIds.isEmpty()) {
			stocksByProductId = productStocksRepository.findAllByProductIds(productIds).stream()
					.collect(Collectors.groupingBy(
							stock -> stock.getProduct().getId(),
							Collectors.summingInt(stock -> stock.getStockQuantity() == null ? 0 : stock.getStockQuantity())
					));
		}
		Map<Long, Integer> finalStocksByProductId = stocksByProductId;

		return productPage.map(product -> toBrowseResponse(
				product,
				finalStocksByProductId.getOrDefault(product.getId(), 0)
		));
	}

	public ProductBrowseResponse getById(Long productId) {
		var product = productRepository.findById(productId)
				.orElseThrow(() -> new NotFoundException("Product not found"));
		var stocks = productStocksRepository.findAllByProductIds(Collections.singleton(product.getId()));
		int stockQuantity = stocks.stream()
				.mapToInt(stock -> stock.getStockQuantity() == null ? 0 : stock.getStockQuantity())
				.sum();
		return toBrowseResponse(product, stockQuantity);
	}

	private ProductBrowseResponse toBrowseResponse(Product product, int stockQuantity) {
		return new ProductBrowseResponse(
				product.getId(),
				product.getTitle(),
				product.getDescription(),
				product.getPrice(),
				stockQuantity,
				new CategorySummary(product.getCategory().getId(), product.getCategory().getName()),
				new StoreSummary(product.getStore().getId(), product.getStore().getName()),
				product.getCreatedAt(),
				product.getUpdatedAt()
		);
	}
}
