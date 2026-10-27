package com.marketplace.plv.auth;

import com.marketplace.plv.account.Account;
import com.marketplace.plv.account.AccountMapper;
import com.marketplace.plv.account.AccountRepository;
import com.marketplace.plv.account.dto.AccountRequest;
import com.marketplace.plv.user.User;
import com.marketplace.plv.user.UserMapper;
import com.marketplace.plv.user.UserRepository;
import com.marketplace.plv.user.dto.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AccountMapper accountMapper;
	private final UserMapper userMapper;
	private final AuthCookieService authCookieService;
	private final AuthenticationManager authManager;
	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder encoder;

	@Transactional
	ResponseCookie register(UserRequest request) {
		var email = request.email();

		if (accountRepository.existsByEmail(email)) throw new IllegalArgumentException("Invalid email");
		if (userRepository.existsByUsername(request.username())) throw new IllegalArgumentException("Invalid username");

		var account = accountMapper.reqToEntity(request,
				req -> new Account(req.email(), encoder.encode(req.password())));
		var savedAccount = accountRepository.save(account);
		var user = userMapper.reqToEntity(request,
				req -> User.builder().account(savedAccount).username(req.username()).active(true).build());
		userRepository.save(user);

		return authCookieService.generateCookie(savedAccount.getId().toString(), "ROLE_USER");
	}

	ResponseCookie login(AccountRequest request) {
		var email = request.email();

		var authentication = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, request.password())
		);

		var account = accountRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("There are no account with this email"));

		var id = account.getId().toString();
		var scope = authentication
				.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));

		return authCookieService.generateCookie(id, scope);
	}

}
