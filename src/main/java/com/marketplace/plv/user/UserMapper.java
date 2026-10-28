package com.marketplace.plv.user;

import com.marketplace.plv.shared.Mapper;
import com.marketplace.plv.user.dto.UserRequest;
import com.marketplace.plv.user.dto.UserResponse;
import com.marketplace.plv.account.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class UserMapper implements Mapper<User, UserRequest, UserResponse> {

	private final AccountMapper accountMapper;

	@Override
	public User reqToEntity(UserRequest request) {
		var account = accountMapper.reqToEntity(request.account());
		return User.builder()
				.account(account)
				.username(request.username())
				.build();
	}

	@Override
	public UserResponse entityToResponse(User user) {
		return new UserResponse(user.getId(), user.getAccount().getId(), user.getUsername(), user.isActive(),
				user.getCreatedAt(), user.getUpdatedAt(), user.getLastLogin());
	}
}
