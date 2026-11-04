package com.marketplace.plv.shared;

import com.marketplace.plv.shared.entity.ApiResponse;
import com.marketplace.plv.shared.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<Object> handleBaseException(BaseException e) {
		return handle(e.getStatus(), e.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
		return handle(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUnexpectedException(Exception e) {
		return handle(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
	}

	private static ResponseEntity<Object> handle(HttpStatus status, String message) {
		return ResponseEntity
				.status(status)
				.body(ApiResponse.failed(message));
	}

}
