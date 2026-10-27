package com.marketplace.plv.shared.exceptions.conflict;

import com.marketplace.plv.shared.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {

	public ConflictException(String message) {
		super(message, HttpStatus.CONFLICT);
	}

	public ConflictException(String message, HttpStatus status) {
		super(message, status);
	}
}
