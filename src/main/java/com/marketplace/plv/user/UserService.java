package com.marketplace.plv.user;

import com.marketplace.plv.account.AccountRepository;
import com.marketplace.plv.auth.AuthCookieService;
import com.marketplace.plv.shared.entity.ApiResponse;
import com.marketplace.plv.shared.entity.UpdateRequest;
import com.marketplace.plv.shared.exceptions.conflict.UsernameConflictException;
import com.marketplace.plv.shared.exceptions.notfound.UserNotFoundException;
import com.marketplace.plv.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final AuthCookieService authCookieService;
	private final UserMapper userMapper;

	public ApiResponse<UserResponse> updateUsername(UpdateRequest<String> request) {
		if (userRepository.existsByUsername(request.update())) throw new UsernameConflictException("Username already exists");

		var userId = authCookieService.getUserIdFromContext();

		var user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Invalid user ID"));
		user.setUsername(request.update());
		userRepository.save(user);
		
		var response = new UserResponse(userId, user.getAccount().getId(), user.getUsername(), user.isActive(), user.getCreatedAt(), user.getUpdatedAt(), user.getLastLogin());

		return ApiResponse.successful(response, "Username has been updated successfully!");
	}


}
