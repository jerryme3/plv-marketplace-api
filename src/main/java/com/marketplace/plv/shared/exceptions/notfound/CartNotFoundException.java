package com.marketplace.plv.shared.exceptions.notfound;

import org.springframework.http.HttpStatus;

public class CartNotFoundException extends NotFoundException {

	public CartNotFoundException(String message) {
		super(message);
	}

	public CartNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}
