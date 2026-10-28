package com.marketplace.plv.user;

import com.marketplace.plv.shared.entity.ApiResponse;
import com.marketplace.plv.shared.entity.UpdateRequest;
import com.marketplace.plv.user.dto.PasswordUpdateRequest;
import com.marketplace.plv.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/me")
public class UserController {

	private final UserService userService;

	@PutMapping("/update/username")
	ApiResponse<UserResponse> changeUsername(@Valid @RequestBody UpdateRequest<String> request) {
		return userService.updateUsername(request);
	}

	@PutMapping("/update/password")
	ApiResponse<UserResponse> changePassword(@Valid @RequestBody PasswordUpdateRequest request) {
		return userService.updatePassword(request);
	}
}
