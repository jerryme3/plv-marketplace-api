package com.marketplace.plv.seller;

import com.marketplace.plv.seller.dto.SellerRequest;
import com.marketplace.plv.seller.dto.SellerResponse;
import com.marketplace.plv.shared.Mapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class SellerMapper implements Mapper<Seller, SellerRequest, SellerResponse> {

	private final EntityManager entityManager;

	@Override
	public Seller reqToEntity(SellerRequest request) {
		var user = entityManager.getReference(com.marketplace.plv.user.User.class, request.userId());
		return Seller.builder().user(user).build();
	}

	@Override
	public SellerResponse entityToResponse(Seller seller) {
		return new SellerResponse(seller.getId(), seller.getUser().getId(), seller.getRating(), seller.isVerified());
	}
}
