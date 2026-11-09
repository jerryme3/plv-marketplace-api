package com.marketplace.plv.user;

import com.marketplace.plv.account.Account;
import com.marketplace.plv.account.AccountRepository;
import com.marketplace.plv.shared.auth.AuthCookieService;
import com.marketplace.plv.shared.entity.UpdateRequest;
import com.marketplace.plv.shared.exceptions.conflict.UsernameConflictException;
import com.marketplace.plv.user.dto.PasswordUpdateRequest;
import com.marketplace.plv.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {

	@Test
	void usernameUpdateUsesAuthenticatedAccountAndSavesTheNewName() {
		var users = mock(UserRepository.class);
		var accounts = mock(AccountRepository.class);
		var auth = mock(AuthCookieService.class);
		var mapper = mock(UserMapper.class);
		var encoder = mock(PasswordEncoder.class);
		var user = user();
		when(auth.getUserIdFromContext()).thenReturn(70L);
		when(users.findByAccountId(70L)).thenReturn(Optional.of(user));
		when(users.existsByUsername("new-name")).thenReturn(false);
		when(mapper.entityToResponse(user)).thenReturn(new UserResponse(1L, 70L, "new-name", true, null, null, null));

		new UserService(users, accounts, auth, mapper, encoder)
				.updateUsername(new UpdateRequest<>("new-name"));

		verify(users).save(user);
		org.junit.jupiter.api.Assertions.assertEquals("new-name", user.getUsername());
	}

	@Test
	void usernameUpdateRejectsDuplicateNames() {
		var users = mock(UserRepository.class);
		var user = user();
		when(users.findByAccountId(70L)).thenReturn(Optional.of(user));
		when(users.existsByUsername("taken")).thenReturn(true);
		var auth = mock(AuthCookieService.class);
		when(auth.getUserIdFromContext()).thenReturn(70L);

		assertThrows(UsernameConflictException.class,
				() -> new UserService(users, mock(AccountRepository.class), auth,
						mock(UserMapper.class), mock(PasswordEncoder.class))
						.updateUsername(new UpdateRequest<>("taken")));
	}

	@Test
	void passwordUpdateVerifiesOldPasswordAndPersistsTheEncodedReplacement() {
		var users = mock(UserRepository.class);
		var accounts = mock(AccountRepository.class);
		var auth = mock(AuthCookieService.class);
		var encoder = mock(PasswordEncoder.class);
		var account = Account.builder().email("buyer@example.test").password("old-hash").build();
		var user = User.builder().account(account).username("buyer").build();
		when(auth.getUserIdFromContext()).thenReturn(70L);
		when(users.findByAccountId(70L)).thenReturn(Optional.of(user));
		when(accounts.findByEmail("buyer@example.test")).thenReturn(Optional.of(account));
		when(encoder.matches("OldPass123!", "old-hash")).thenReturn(true);
		when(encoder.matches("NewPass123!", "old-hash")).thenReturn(false);
		when(encoder.encode("NewPass123!")).thenReturn("new-hash");

		new UserService(users, accounts, auth, mock(UserMapper.class), encoder)
				.updatePassword(new PasswordUpdateRequest("NewPass123!", "OldPass123!"));

		org.junit.jupiter.api.Assertions.assertEquals("new-hash", account.getPassword());
		verify(accounts).save(account);
	}

	private static User user() {
		return User.builder()
				.account(Account.builder().email("buyer@example.test").build())
				.username("buyer")
				.active(true)
				.build();
	}
}
