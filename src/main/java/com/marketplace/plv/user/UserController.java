package com.marketplace.plv.user;

import com.marketplace.plv.seller.SellerService;
import com.marketplace.plv.seller.dto.SellerRequest;
import com.marketplace.plv.seller.dto.SellerResponse;
import com.marketplace.plv.shared.entity.ApiResponse;
import com.marketplace.plv.shared.entity.UpdateRequest;
import com.marketplace.plv.user.dto.PasswordUpdateRequest;
import com.marketplace.plv.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/me")
public class UserController {

	private final UserService userService;
	private final SellerService sellerService;

	@PutMapping("/update/username")
	ApiResponse<UserResponse> changeUsername(@Valid @RequestBody UpdateRequest<String> request) {
		return userService.updateUsername(request);
	}

	@PutMapping("/update/password")
	ApiResponse<UserResponse> changePassword(@Valid @RequestBody PasswordUpdateRequest request) {
		return userService.updatePassword(request);
	}

	@PostMapping("/seller")
	ApiResponse<SellerResponse> beASeller(@Valid @RequestBody SellerRequest request) {
		return sellerService.registerSeller(request);
	}
}