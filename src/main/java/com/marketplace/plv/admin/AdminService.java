package com.marketplace.plv.admin;

import com.marketplace.plv.account.Account;
import com.marketplace.plv.account.AccountRepository;
import com.marketplace.plv.account.AccountRole;
import com.marketplace.plv.admin.dto.RoleUpdateRequest;
import com.marketplace.plv.shared.auth.AuthCookieService;
import com.marketplace.plv.shared.entity.ApiResponse;
import com.marketplace.plv.shared.exceptions.ForbiddenAccessException;
import com.marketplace.plv.shared.exceptions.notfound.UserNotFoundException;
import com.marketplace.plv.user.UserMapper;
import com.marketplace.plv.user.UserRepository;
import com.marketplace.plv.user.dto.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final AuthCookieService authCookieService;
	private final UserMapper userMapper;

	String greetAdmin() {
		if (!authCookieService.isValidSession()) throw new ForbiddenAccessException("Invalid session");

		var adminId = authCookieService.getUserIdFromContext();
		var admin = userRepository.findByAccountId(adminId).orElseThrow(() -> new UserNotFoundException("Invalid user ID"));

		return "Hello " + admin.getUsername() + "!";
	}

	ApiResponse<UserResponse> promoteUserToAdmin(RoleUpdateRequest request) {
		return updateRole(request, accountRepository::promoteToAdmin, r -> r.getRole() == AccountRole.ROLE_ADMIN,
				"User is already an admin.", " is now promoted to admin!");
	}

	ApiResponse<UserResponse> demoteAdminToUser(RoleUpdateRequest request) {
		return updateRole(request, accountRepository::demoteToUser, r -> r.getRole() == AccountRole.ROLE_USER,
				"User is already a regular user.", " is now demoted to user!");
	}

	@Transactional
	@SuppressWarnings("unchecked")
	<P extends Number> ApiResponse<UserResponse> updateRole(
			RoleUpdateRequest request,
			Consumer<P> action,
			Predicate<Account> conditionCheck,
			String failureMessage,
			String apiMessage
	) {
		if (!authCookieService.isValidSession()) {
			throw new ForbiddenAccessException("Invalid session");
		}

		var user = userRepository.findById(request.userId())
				.orElseThrow(() -> new UserNotFoundException("Invalid user ID"));
		var account = user.getAccount();

		if (conditionCheck.test(account)) {
			throw new IllegalArgumentException(failureMessage);
		}

		P accountId = (P) account.getId();
		action.accept(accountId);

		UserResponse response = userMapper.entityToResponse(userRepository.save(user));
		return ApiResponse.successful(response, response.username() + " " + apiMessage);
	}

}
