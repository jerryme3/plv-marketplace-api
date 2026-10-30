package com.marketplace.plv.store;

import com.marketplace.plv.product.dto.ProductRequest;
import com.marketplace.plv.product_stocks.dto.ProductStockResponse;
import com.marketplace.plv.shared.entity.ApiResponse;
import com.marketplace.plv.store.dto.StoreRequest;
import com.marketplace.plv.store.dto.StoreResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/store")
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;

	@PostMapping("/register")
	ApiResponse<StoreResponse> registerStore(@Valid @RequestBody StoreRequest request) {
		return storeService.registerStore(request);
	}

	@PostMapping("/sell")
	ApiResponse<ProductStockResponse> sellProducts(@Valid @RequestBody ProductRequest request) {
		return storeService.sellProducts(request);
	}

}
