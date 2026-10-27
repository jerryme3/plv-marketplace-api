package com.marketplace.plv.account;

import com.marketplace.plv.account.dto.AccountResponse;
import com.marketplace.plv.shared.Mapper;
import com.marketplace.plv.user.dto.UserRequest;
import org.springframework.stereotype.Component;

@Component
public final class AccountMapper implements Mapper<Account, UserRequest, AccountResponse> {
}
