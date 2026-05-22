package com.example.ecommerce.common;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        Map<String, String> validationErrors
) {
    public static ApiError of(int status, String error) {
        return new ApiError(Instant.now(), status, error, null);
    }

    public static ApiError validation(Map<String, String> errors) {
        return new ApiError(Instant.now(), 400, "Validation failed.", errors);
    }
}
