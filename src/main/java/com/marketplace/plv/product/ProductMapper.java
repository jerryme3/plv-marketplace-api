package com.marketplace.plv.product;

import com.marketplace.plv.product.dto.ProductRequest;
import com.marketplace.plv.product.dto.ProductResponse;
import com.marketplace.plv.shared.Mapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class ProductMapper implements Mapper<Product, ProductRequest, ProductResponse> {

	private final EntityManager entityManager;

	@Override
	public Product reqToEntity(ProductRequest request) {
		var store = entityManager.getReference(com.marketplace.plv.store.Store.class, request.storeId());
		var category = entityManager.getReference(com.marketplace.plv.category.Category.class, request.categoryId());
		return Product.builder()
				.store(store)
				.category(category)
				.title(request.title())
				.description(request.description())
				.price(request.price())
				.build();
	}

	@Override
	public ProductResponse entityToResponse(Product product) {
		return new ProductResponse(product.getId(), product.getStore().getId(), product.getCategory().getId(),
				product.getTitle(), product.getDescription(), product.getPrice(),
				product.getCreatedAt(), product.getUpdatedAt());
	}
}
