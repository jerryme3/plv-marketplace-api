package com.marketplace.plv.seller;

import com.marketplace.plv.shared.auth.AuthCookieService;
import com.marketplace.plv.seller.dto.SellerResponse;
import com.marketplace.plv.shared.entity.ApiResponse;
import com.marketplace.plv.shared.exceptions.conflict.ConflictException;
import com.marketplace.plv.shared.exceptions.ForbiddenAccessException;
import com.marketplace.plv.shared.exceptions.notfound.UserNotFoundException;
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


	public ApiResponse<SellerResponse> registerSeller() {
		var accountId = authCookieService.getUserIdFromContext();
		if (accountId == null) {
			throw new ForbiddenAccessException("Invalid session");
		}
		var user = userRepository.findByAccountId(accountId)
				.orElseThrow(() -> new UserNotFoundException("Invalid user ID"));

		if (sellerRepository.existsByUserId(user.getId()))
			throw new ConflictException("Seller already registered.");

		var seller = sellerRepository.save(Seller.builder().user(user).build());

		var resp = sellerMapper.entityToResponse(seller);

		return ApiResponse.successful(resp, "Seller profile created");
	}

}
