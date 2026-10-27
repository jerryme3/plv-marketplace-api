package com.marketplace.plv.shared.exceptions.notfound;

import com.marketplace.plv.shared.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {

	public NotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND);
	}

	public NotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}
