package com.marketplace.plv.shared.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {

	protected HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

	public BaseException(String message) {
		super(message);
	}

	public BaseException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

}
