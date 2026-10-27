package com.marketplace.plv.shared.exceptions.notfound;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends NotFoundException {

	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}
