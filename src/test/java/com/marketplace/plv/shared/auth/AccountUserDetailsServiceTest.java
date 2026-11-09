package com.marketplace.plv.shared.auth;

import com.marketplace.plv.account.Account;
import com.marketplace.plv.account.AccountRepository;
import com.marketplace.plv.account.AccountRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AccountUserDetailsServiceTest {

	@Test
	void loadsPasswordAndRoleFromTheAccount() {
		var accounts = mock(AccountRepository.class);
		var account = Account.builder()
				.email("student@example.test")
				.password("encoded-password")
				.role(AccountRole.ROLE_USER)
				.build();
		when(accounts.findByEmail("student@example.test")).thenReturn(Optional.of(account));

		var details = new AccountUserDetailsService(accounts).loadUserByUsername("student@example.test");

		assertEquals("encoded-password", details.getPassword());
		assertEquals("ROLE_USER", details.getAuthorities().iterator().next().getAuthority());
	}

	@Test
	void missingAccountIsReportedAsAnUnknownUsername() {
		var accounts = mock(AccountRepository.class);
		when(accounts.findByEmail("missing@example.test")).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class,
				() -> new AccountUserDetailsService(accounts).loadUserByUsername("missing@example.test"));
	}
}
