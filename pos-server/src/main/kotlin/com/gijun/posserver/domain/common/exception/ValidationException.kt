package com.gijun.posserver.domain.common.exception

class ValidationException(
    field: String,
    value: Any? = null,
    message: String
) : DomainException(
    buildString {
        append("Validation failed for field: $field")
        if (value != null) {
            append(" with value: $value")
        }
        append(" - $message")
    }
)