package com.marketplace.plv.shared.exceptions.notfound;

import org.springframework.http.HttpStatus;

public class UsernameNotFoundException extends NotFoundException {

	public UsernameNotFoundException(String message) {
		super(message);
	}

	public UsernameNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}
