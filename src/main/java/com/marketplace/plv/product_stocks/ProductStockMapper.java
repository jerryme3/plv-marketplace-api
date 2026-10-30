package com.marketplace.plv.product_stocks;

import com.marketplace.plv.product.Product;
import com.marketplace.plv.product.ProductMapper;
import com.marketplace.plv.product.dto.ProductRequest;
import com.marketplace.plv.product_stocks.dto.ProductStockRequest;
import com.marketplace.plv.product_stocks.dto.ProductStockResponse;
import com.marketplace.plv.shared.Mapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductStockMapper implements Mapper<ProductStocks, ProductStockRequest, ProductStockResponse> {

	private final EntityManager entityManager;
	private final ProductMapper productMapper;

	@Override
	public ProductStocks reqToEntity(ProductStockRequest req) {
		ProductStocks productStocks = new ProductStocks();
		productStocks.setStockQuantity(req.stockQuantity());
		productStocks.setProduct(entityManager.getReference(Product.class, req.productId()));

		return productStocks;
	}

	@Override
	public ProductStockResponse entityToResponse(ProductStocks entity) {
		return new ProductStockResponse(productMapper.entityToResponse(entity.getProduct()), entity.getStockQuantity());
	}
}
