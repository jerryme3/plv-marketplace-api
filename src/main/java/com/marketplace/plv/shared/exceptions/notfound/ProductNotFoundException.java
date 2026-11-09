package com.marketplace.plv.shared.exceptions.notfound;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(HttpStatus status, String message) {
        super(message, status);
    }

}
