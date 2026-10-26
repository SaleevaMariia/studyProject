package org.study.common.error;

public record FieldErrorResponse(
        String field,
        String message
) {
}
