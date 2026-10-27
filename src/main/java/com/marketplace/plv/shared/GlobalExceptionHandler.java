package com.marketplace.plv.shared;

import com.marketplace.plv.shared.exceptions.BaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@RestControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<Object> handleBaseException(BaseException e) {
		return ResponseEntity
				.status(e.getStatus())
				.body(
					Map.of(
							"detail", e.getStatus().getReasonPhrase(),
							"status", e.getStatus().value(),
							"message", e.getMessage()
					)
				);
	}

}
