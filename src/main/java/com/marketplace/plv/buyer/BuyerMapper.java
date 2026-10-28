package com.marketplace.plv.buyer;

import com.marketplace.plv.buyer.dto.BuyerRequest;
import com.marketplace.plv.buyer.dto.BuyerResponse;
import com.marketplace.plv.shared.Mapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class BuyerMapper implements Mapper<Buyer, BuyerRequest, BuyerResponse> {

	private final EntityManager entityManager;

	@Override
	public Buyer reqToEntity(BuyerRequest request) {
		var user = entityManager.getReference(com.marketplace.plv.user.User.class, request.userId());
		return Buyer.builder().user(user).deliveryBuilding(request.deliveryBuilding()).build();
	}

	@Override
	public BuyerResponse entityToResponse(Buyer buyer) {
		return new BuyerResponse(buyer.getId(), buyer.getUser().getId(), buyer.getDeliveryBuilding());
	}
}
