package com.marketplace.plv.product;

import com.marketplace.plv.product.dto.ProductRequest;
import com.marketplace.plv.product.dto.ProductResponse;
import com.marketplace.plv.shared.Mapper;
import org.springframework.stereotype.Component;

@Component
public final class ProductMapper implements Mapper<Product, ProductRequest, ProductResponse> {
}
