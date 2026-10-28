package com.marketplace.plv.user;

import com.marketplace.plv.account.AccountRepository;
import com.marketplace.plv.shared.auth.AuthCookieService;
import com.marketplace.plv.shared.entity.ApiResponse;
import com.marketplace.plv.shared.entity.UpdateRequest;
import com.marketplace.plv.shared.exceptions.conflict.ConflictException;
import com.marketplace.plv.shared.exceptions.conflict.UsernameConflictException;
import com.marketplace.plv.shared.exceptions.notfound.EmailNotFoundException;
import com.marketplace.plv.shared.exceptions.notfound.UserNotFoundException;
import com.marketplace.plv.user.dto.PasswordUpdateRequest;
import com.marketplace.plv.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final AuthCookieService authCookieService;
	private final UserMapper userMapper;
	private final PasswordEncoder encoder;

	ApiResponse<UserResponse> updateUsername(UpdateRequest<String> request) {
		var userId = authCookieService.getUserIdFromContext();

		var user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Invalid user ID"));

		if (user.getUsername().equals(request.update()))
			throw new UsernameConflictException("Username cannot be the same as before");

		if (userRepository.existsByUsername(request.update()))
			throw new UsernameConflictException("Username already exists");

		user.setUsername(request.update());
		userRepository.save(user);
		
		var response = userMapper.entityToResponse(user);

		return ApiResponse.successful(response, "Username has been updated successfully!");
	}

	ApiResponse<UserResponse> updatePassword(PasswordUpdateRequest request) {
		Long userId = authCookieService.getUserIdFromContext();

		var user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("Invalid user ID"));
		var acc = accountRepository.findByEmail(user.getAccount().getEmail())
				.orElseThrow(() -> new EmailNotFoundException("Invalid user email"));

		if (!encoder.matches(request.oldPassword(), acc.getPassword()))
			throw new ConflictException("Wrong password", HttpStatus.BAD_REQUEST);

		if (encoder.matches(request.newPassword(), acc.getPassword()))
			throw new ConflictException("Cannot be as the same as your old password", HttpStatus.BAD_REQUEST);

		acc.setPassword(encoder.encode(request.newPassword()));
		user.setAccount(acc);
		accountRepository.save(acc);
		userRepository.save(user);

		var resp = userMapper.entityToResponse(user);

		return ApiResponse.successful(resp, "Password has been successfully updated!");
	}

}
