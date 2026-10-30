package com.marketplace.plv.store;

import com.marketplace.plv.product.ProductMapper;
import com.marketplace.plv.product.ProductRepository;
import com.marketplace.plv.product.dto.ProductRequest;
import com.marketplace.plv.product_stocks.ProductStockMapper;
import com.marketplace.plv.product_stocks.ProductStocksRepository;
import com.marketplace.plv.product_stocks.dto.ProductStockRequest;
import com.marketplace.plv.product_stocks.dto.ProductStockResponse;
import com.marketplace.plv.seller.SellerRepository;
import com.marketplace.plv.shared.auth.AuthCookieService;
import com.marketplace.plv.shared.entity.ApiResponse;
import com.marketplace.plv.shared.exceptions.ForbiddenAccessException;
import com.marketplace.plv.shared.exceptions.notfound.UserNotFoundException;
import com.marketplace.plv.store.dto.StoreRequest;
import com.marketplace.plv.store.dto.StoreResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final SellerRepository sellerRepository;
	private final StoreRepository storeRepository;
	private final ProductStocksRepository productStocksRepository;
	private final ProductRepository productRepository;

	private final StoreMapper storeMapper;
	private final ProductMapper productMapper;
	private final ProductStockMapper productStockMapper;

	private final AuthCookieService authCookieService;

	@Transactional
	ApiResponse<StoreResponse> registerStore(StoreRequest request) {
		Long userId = authCookieService.getUserIdFromContext();

		var seller = sellerRepository.findByUserId(userId)
				.orElseThrow(() -> new UserNotFoundException("Invalid user ID"));

		var newStoreRequest =new StoreRequest(seller.getId(), request.name(), request.description());

		var store = storeMapper.reqToEntity(newStoreRequest);
		var storeResponse = storeMapper.entityToResponse(storeRepository.save(store));

		return ApiResponse.successful(storeResponse, "Store has been created! Sell now!");
	}

	@Transactional
	ApiResponse<ProductStockResponse> sellProducts(ProductRequest request) {
		if (!authCookieService.isValidSession()) throw new ForbiddenAccessException("Invalid session");

		var product = productMapper.reqToEntity(request);
		var saved = productRepository.save(product);
		var productStock = productStockMapper.reqToEntity(new ProductStockRequest(saved.getId(), request.stockQuantity()));

		productStocksRepository.save(productStock);

		var response = productStockMapper.entityToResponse(productStock);

		return ApiResponse.successful(response, "Product has been posted! Wait for buyers!");
	}

}
