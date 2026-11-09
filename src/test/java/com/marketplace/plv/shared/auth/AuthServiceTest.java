package com.marketplace.plv.shared.auth;

import com.marketplace.plv.account.Account;
import com.marketplace.plv.account.AccountMapper;
import com.marketplace.plv.account.AccountRepository;
import com.marketplace.plv.account.dto.AccountRequest;
import com.marketplace.plv.buyer.BuyerMapper;
import com.marketplace.plv.buyer.BuyerRepository;
import com.marketplace.plv.cart.CartRepository;
import com.marketplace.plv.shared.exceptions.conflict.EmailConflictException;
import com.marketplace.plv.user.User;
import com.marketplace.plv.user.UserMapper;
import com.marketplace.plv.user.UserRepository;
import com.marketplace.plv.user.dto.UserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

	@Test
	void registrationCreatesAccountUserBuyerCartAndCookie() {
		var accountMapper = mock(AccountMapper.class);
		var userMapper = mock(UserMapper.class);
		var cookieService = mock(AuthCookieService.class);
		var accounts = mock(AccountRepository.class);
		var users = mock(UserRepository.class);
		var buyers = mock(BuyerRepository.class);
		var carts = mock(CartRepository.class);
		var account = mock(Account.class);
		var user = mock(User.class);
		var cookie = ResponseCookie.from("AUTH_TOKEN", "token").build();
		var request = new UserRequest("student", new AccountRequest("student@example.test", "TestPass123!"));
		when(accounts.existsByEmail("student@example.test")).thenReturn(false);
		when(users.existsByUsername("student")).thenReturn(false);
		when(accountMapper.reqToEntity(request.account())).thenReturn(account);
		when(accounts.save(account)).thenReturn(account);
		when(userMapper.reqToEntity(request)).thenReturn(user);
		when(users.save(user)).thenReturn(user);
		when(account.getId()).thenReturn(70L);
		when(account.getRole()).thenReturn(com.marketplace.plv.account.AccountRole.ROLE_USER);
		when(cookieService.generateCookie("70", "ROLE_USER")).thenReturn(cookie);

		var result = service(accountMapper, userMapper, cookieService, accounts, users, buyers, carts)
				.register(request);

		org.junit.jupiter.api.Assertions.assertSame(cookie, result);
		verify(buyers).save(any());
		verify(carts).save(any());
	}

	@Test
	void registrationRejectsAnExistingEmailBeforeCreatingEntities() {
		var accounts = mock(AccountRepository.class);
		when(accounts.existsByEmail("student@example.test")).thenReturn(true);
		var service = service(mock(AccountMapper.class), mock(UserMapper.class), mock(AuthCookieService.class),
				accounts, mock(UserRepository.class), mock(BuyerRepository.class), mock(CartRepository.class));
		var request = new UserRequest("student", new AccountRequest("student@example.test", "TestPass123!"));

		assertThrows(EmailConflictException.class, () -> service.register(request));

		verify(accounts, never()).save(any());
	}

	@Test
	void logoutMarksTheUserResolvedFromTheAccountIdInactive() {
		var users = mock(UserRepository.class);
		var auth = mock(AuthCookieService.class);
		var user = mock(User.class);
		when(auth.getUserIdFromContext()).thenReturn(70L);
		when(users.findByAccountId(70L)).thenReturn(Optional.of(user));
		var cookie = ResponseCookie.from("AUTH_TOKEN").maxAge(0).build();
		when(auth.clearCookie()).thenReturn(cookie);
		var service = service(mock(AccountMapper.class), mock(UserMapper.class), auth,
				mock(AccountRepository.class), users, mock(BuyerRepository.class), mock(CartRepository.class));

		org.junit.jupiter.api.Assertions.assertSame(cookie, service.logout());

		verify(user).setActive(false);
		verify(users).save(user);
	}

	private static AuthService service(
			AccountMapper accountMapper,
			UserMapper userMapper,
			AuthCookieService cookieService,
			AccountRepository accounts,
			UserRepository users,
			BuyerRepository buyers,
			CartRepository carts
	) {
		return new AuthService(accountMapper, userMapper, mock(BuyerMapper.class), cookieService,
				mock(AuthenticationManager.class), accounts, users, buyers, carts);
	}
}
