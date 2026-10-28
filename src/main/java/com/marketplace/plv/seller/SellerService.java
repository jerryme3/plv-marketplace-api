package com.marketplace.plv.seller;

import com.marketplace.plv.auth.AuthCookieService;
import com.marketplace.plv.seller.dto.SellerRequest;
import com.marketplace.plv.seller.dto.SellerResponse;
import com.marketplace.plv.shared.entity.ApiResponse;
import com.marketplace.plv.shared.exceptions.conflict.ConflictException;
import com.marketplace.plv.user.UserMapper;
import com.marketplace.plv.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerService {

	private final SellerRepository sellerRepository;
	private final UserRepository userRepository;

	private final AuthCookieService authCookieService;

	private final SellerMapper sellerMapper;
	private final UserMapper userMapper;


	public ApiResponse<SellerResponse> registerSeller(SellerRequest request) {
		Long userId = authCookieService.getUserIdFromContext();

		if (sellerRepository.existsByUserId(userId))
			throw new ConflictException("Seller already registered.");

		var seller = sellerMapper.reqToEntity(request);
		sellerRepository.save(seller);

		var resp = sellerMapper.entityToResponse(seller);

		return ApiResponse.successful(resp, "Registered as seller! Wait for verification.");
	}

}
