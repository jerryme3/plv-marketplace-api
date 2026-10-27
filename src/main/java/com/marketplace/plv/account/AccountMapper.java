package com.marketplace.plv.account;

import com.marketplace.plv.account.dto.AccountRequest;
import com.marketplace.plv.account.dto.AccountResponse;
import com.marketplace.plv.shared.Mapper;
import org.springframework.stereotype.Component;

@Component
public final class AccountMapper implements Mapper<Account, AccountRequest, AccountResponse> {
}
