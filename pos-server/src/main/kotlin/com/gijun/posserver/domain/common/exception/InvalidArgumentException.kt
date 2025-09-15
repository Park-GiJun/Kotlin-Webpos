package com.gijun.posserver.domain.common.exception

class InvalidArgumentException(
    argumentName: String,
    value: Any? = null,
    reason: String? = null
) : DomainException(
    buildString {
        append("Invalid argument: $argumentName")
        if (value != null) {
            append(" with value: $value")
        }
        if (reason != null) {
            append(" - $reason")
        }
    }
)