package com.marketplace.plv.shared.exceptions.conflict;

import org.springframework.http.HttpStatus;

public class UsernameConflictException extends ConflictException {

	public UsernameConflictException(String message) {
		super(message);
	}

	public UsernameConflictException(String message, HttpStatus status) {
		super(message, status);
	}
}
