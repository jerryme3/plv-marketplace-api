package com.marketplace.plv.store;

import com.marketplace.plv.shared.Mapper;
import com.marketplace.plv.store.dto.StoreRequest;
import com.marketplace.plv.store.dto.StoreResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class StoreMapper implements Mapper<Store, StoreRequest, StoreResponse> {

	private final EntityManager entityManager;

	@Override
	public Store reqToEntity(StoreRequest request) {
		var seller = entityManager.getReference(com.marketplace.plv.seller.Seller.class, request.sellerId());
		return Store.builder().seller(seller).name(request.name()).description(request.description()).build();
	}

	@Override
	public StoreResponse entityToResponse(Store store) {
		return new StoreResponse(store.getId(), store.getSeller().getId(), store.getName(), store.getDescription(), store.getCreatedAt());
	}
}
