package com.marketplace.plv.shared.exceptions.notfound;

import org.springframework.http.HttpStatus;

public class ItemNotFoundException extends NotFoundException {

	public ItemNotFoundException(String message) {
		super(message);
	}

	public ItemNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}
