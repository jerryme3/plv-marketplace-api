package com.marketplace.plv.shared.auth;

import com.marketplace.plv.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @SuppressWarnings("NullableProblems")
    @Override
    public UserDetails loadUserByUsername(String email) {
        var account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));

        return User.withUsername(account.getEmail())
                .password(account.getPassword())
                .authorities("ROLE_USER")
                .build();
    }
}