package com.marketplace.plv.seller;

import com.marketplace.plv.seller.dto.SellerResponse;
import org.springframework.stereotype.Component;

@Component
public final class SellerMapper {

	public SellerResponse entityToResponse(Seller seller) {
		return new SellerResponse(seller.getId(), seller.getUser().getId(), seller.getRating(), seller.isVerified());
	}
}
