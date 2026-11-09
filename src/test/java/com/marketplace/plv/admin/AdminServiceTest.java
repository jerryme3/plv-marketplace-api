package com.marketplace.plv.admin;

import com.marketplace.plv.account.Account;
import com.marketplace.plv.account.AccountRepository;
import com.marketplace.plv.account.AccountRole;
import com.marketplace.plv.admin.dto.RoleUpdateRequest;
import com.marketplace.plv.shared.auth.AuthCookieService;
import com.marketplace.plv.shared.exceptions.ForbiddenAccessException;
import com.marketplace.plv.user.User;
import com.marketplace.plv.user.UserMapper;
import com.marketplace.plv.user.UserRepository;
import com.marketplace.plv.user.dto.UserResponse;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminServiceTest {

	@Test
	void promotionUpdatesTheTargetAccountsRole() {
		var users = mock(UserRepository.class);
		var accounts = mock(AccountRepository.class);
		var auth = mock(AuthCookieService.class);
		var mapper = mock(UserMapper.class);
		var account = mock(Account.class);
		var user = mock(User.class);
		when(auth.isValidSession()).thenReturn(true);
		when(users.findById(7L)).thenReturn(Optional.of(user));
		when(user.getAccount()).thenReturn(account);
		when(account.getRole()).thenReturn(AccountRole.ROLE_USER);
		when(account.getId()).thenReturn(70L);
		when(users.save(user)).thenReturn(user);
		when(mapper.entityToResponse(user)).thenReturn(new UserResponse(7L, 70L, "student", true, null, null, null));

		var result = new AdminService(users, accounts, auth, mapper)
				.promoteUserToAdmin(new RoleUpdateRequest(7L));

		assertEquals(true, result.successful());
		verify(accounts).promoteToAdmin(70L);
	}

	@Test
	void roleChangesRequireAValidSession() {
		var auth = mock(AuthCookieService.class);
		when(auth.isValidSession()).thenReturn(false);

		assertThrows(ForbiddenAccessException.class,
				() -> new AdminService(mock(UserRepository.class), mock(AccountRepository.class),
						auth, mock(UserMapper.class)).promoteUserToAdmin(new RoleUpdateRequest(7L)));
	}

	@Test
	void adminGreetingLooksUpTheAuthenticatedAccount() {
		var users = mock(UserRepository.class);
		var auth = mock(AuthCookieService.class);
		var admin = mock(User.class);
		when(auth.isValidSession()).thenReturn(true);
		when(auth.getUserIdFromContext()).thenReturn(70L);
		when(users.findByAccountId(70L)).thenReturn(Optional.of(admin));
		when(admin.getUsername()).thenReturn("administrator");

		assertEquals("Hello administrator!", new AdminService(
				users, mock(AccountRepository.class), auth, mock(UserMapper.class)).greetAdmin());
	}
}
