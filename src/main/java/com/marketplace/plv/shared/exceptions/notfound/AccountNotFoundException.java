package com.marketplace.plv.shared.exceptions.notfound;

import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends NotFoundException {

	public AccountNotFoundException(String message) {
		super(message);
	}

	public AccountNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}
