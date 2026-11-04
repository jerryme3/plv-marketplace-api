package com.marketplace.plv.product;

import com.marketplace.plv.product.dto.ProductBrowseResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
public class ProductBrowseController {

	private final ProductBrowseService productBrowseService;

	@GetMapping
	public Page<ProductBrowseResponse> browse(
			@RequestParam(required = false) String search,
			@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) Long storeId,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
			@RequestParam(defaultValue = "createdAt") String sort,
			@RequestParam(defaultValue = "desc") String direction
	) {
		return productBrowseService.browse(search, categoryId, storeId, page, size, sort, direction);
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ProductBrowseResponse> getById(@PathVariable Long productId) {
		return ResponseEntity.ok(productBrowseService.getById(productId));
	}
}
