package com.marketplace.plv.seller;

import com.marketplace.plv.shared.auth.AuthCookieService;
import com.marketplace.plv.shared.exceptions.ForbiddenAccessException;
import com.marketplace.plv.shared.exceptions.conflict.ConflictException;
import com.marketplace.plv.user.User;
import com.marketplace.plv.user.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SellerServiceTest {

	@Test
	void sellerRegistrationUsesTheAuthenticatedAccount() {
		var sellers = mock(SellerRepository.class);
		var users = mock(UserRepository.class);
		var auth = mock(AuthCookieService.class);
		var user = mock(User.class);
		when(auth.getUserIdFromContext()).thenReturn(70L);
		when(users.findByAccountId(70L)).thenReturn(Optional.of(user));
		when(user.getId()).thenReturn(7L);
		when(sellers.existsByUserId(7L)).thenReturn(false);
		when(sellers.save(any(Seller.class))).thenAnswer(invocation -> invocation.getArgument(0));

		new SellerService(sellers, users, auth, new SellerMapper()).registerSeller();

		verify(sellers).save(argThat(seller -> seller.getUser() == user));
	}

	@Test
	void sellerRegistrationRejectsDuplicateProfiles() {
		var sellers = mock(SellerRepository.class);
		var users = mock(UserRepository.class);
		var auth = mock(AuthCookieService.class);
		var user = mock(User.class);
		when(auth.getUserIdFromContext()).thenReturn(70L);
		when(users.findByAccountId(70L)).thenReturn(Optional.of(user));
		when(user.getId()).thenReturn(7L);
		when(sellers.existsByUserId(7L)).thenReturn(true);

		assertThrows(ConflictException.class,
				() -> new SellerService(sellers, users, auth, new SellerMapper()).registerSeller());
		verify(sellers, never()).save(any());
	}

	@Test
	void sellerRegistrationRequiresAnAuthenticatedAccount() {
		var auth = mock(AuthCookieService.class);
		when(auth.getUserIdFromContext()).thenReturn(null);

		assertThrows(ForbiddenAccessException.class,
				() -> new SellerService(mock(SellerRepository.class), mock(UserRepository.class),
						auth, new SellerMapper()).registerSeller());
	}
}
