package com.marketplace.plv.shared;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.function.Function;

@Component
public interface Mapper<T, R, S> {

	default T reqToEntity(R req, Function<R, T> func) {
		Objects.requireNonNull(req);

		return Objects.requireNonNull(func).apply(req);
	}
}
