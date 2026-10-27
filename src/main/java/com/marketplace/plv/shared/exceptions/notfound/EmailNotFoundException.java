package com.marketplace.plv.shared.exceptions.notfound;

import org.springframework.http.HttpStatus;

public class EmailNotFoundException extends NotFoundException {
	
	public EmailNotFoundException(String message) {
		super(message);
	}

	public EmailNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}
