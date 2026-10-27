package com.marketplace.plv.auth;

import com.marketplace.plv.account.Account;
import com.marketplace.plv.account.AccountMapper;
import com.marketplace.plv.account.AccountRepository;
import com.marketplace.plv.account.dto.AccountRequest;
import com.marketplace.plv.shared.exceptions.conflict.EmailConflictException;
import com.marketplace.plv.shared.exceptions.notfound.EmailNotFoundException;
import com.marketplace.plv.shared.exceptions.notfound.UserNotFoundException;
import com.marketplace.plv.user.User;
import com.marketplace.plv.user.UserMapper;
import com.marketplace.plv.user.UserRepository;
import com.marketplace.plv.user.dto.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
		var accountReq = request.account();
		var email = accountReq.email();

		if (accountRepository.existsByEmail(email)) throw new EmailConflictException("Invalid email");
		if (userRepository.existsByUsername(request.username())) throw new UserNotFoundException("Invalid username");

		var account = accountMapper.reqToEntity(accountReq,
				req -> new Account(req.email(), encoder.encode(req.password())));
		var savedAccount = accountRepository.save(account);
		var user = userMapper.reqToEntity(request,
				req -> User.builder().account(savedAccount).username(req.username()).build());
		userRepository.save(user);

		return authCookieService.generateCookie(savedAccount.getId().toString(), "ROLE_USER");
	}

	ResponseCookie login(AccountRequest request) {
		var email = request.email();

		var authentication = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, request.password())
		);

		var account = accountRepository.findByEmail(email).orElseThrow(() -> new EmailNotFoundException("There are no account with this email"));

		var id = account.getId().toString();
		var scope = authentication
				.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));

		var user = userRepository.findByAccount(account).orElseThrow(() -> new UserNotFoundException("There are no user with this account details"));
		user.setActive(true);
		user.setLastLogin(Instant.now());
		userRepository.save(user);

		return authCookieService.generateCookie(id, scope);
	}

	ResponseCookie logout() {
		var	auth = SecurityContextHolder.getContext().getAuthentication();

		authCookieService.getUserIdFromContext(e -> {
			if (auth != null && auth.isAuthenticated()) {
				var identifier = auth.getName();

				userRepository
						.findById(Long.parseLong(identifier))
						.ifPresent(b -> {
							b.setActive(false);
							userRepository.save(b);
				});
			}
		});

		SecurityContextHolder.clearContext();

		return authCookieService.clearCookie();
	}

}
