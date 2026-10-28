package com.marketplace.plv.account;

import com.marketplace.plv.account.dto.AccountRequest;
import com.marketplace.plv.account.dto.AccountResponse;
import com.marketplace.plv.shared.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class AccountMapper implements Mapper<Account, AccountRequest, AccountResponse> {

	private final PasswordEncoder passwordEncoder;

	@Override
	public Account reqToEntity(AccountRequest request) {
		return Account.builder()
				.email(request.email())
				.password(passwordEncoder.encode(request.password()))
				.build();
	}

	@Override
	public AccountResponse entityToResponse(Account account) {
		return new AccountResponse(account.getId(), account.getEmail());
	}
}
