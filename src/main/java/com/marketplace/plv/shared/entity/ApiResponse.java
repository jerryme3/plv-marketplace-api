package com.marketplace.plv.shared.entity;

import java.time.Instant;

public record ApiResponse<T>(
		T body,
		String message,
		boolean successful,
		Instant responseTime
) {

	public static <O> ApiResponse<O> failed(String message) {
		return new ApiResponse<>(null, message, false, Instant.now());
	}

	public static <O> ApiResponse<O> successful(O body, String message) {
		return new ApiResponse<>(body, message, true, Instant.now());
	}

}
