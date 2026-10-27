package com.marketplace.plv.shared.exceptions.conflict;

import org.springframework.http.HttpStatus;

public class EmailConflictException extends ConflictException {

	public EmailConflictException(String message) {
		super(message);
	}

	public EmailConflictException(String message, HttpStatus status) {
		super(message, status);
	}
}
