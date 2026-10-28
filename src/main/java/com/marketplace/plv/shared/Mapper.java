package com.marketplace.plv.shared;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public interface Mapper<T, R, S> {

	default T reqToEntity(R req) {
		Objects.requireNonNull(req);
		throw new UnsupportedOperationException("This mapper requires additional entities to create " + req.getClass().getSimpleName());
	}

	default S entityToResponse(T entity) {
		Objects.requireNonNull(entity);
		throw new UnsupportedOperationException("This entity response mapping is not supported by " + entity.getClass().getSimpleName());
	}
}
