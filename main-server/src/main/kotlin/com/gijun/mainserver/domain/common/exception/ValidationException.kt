package com.gijun.mainserver.domain.common.exception

/**
 * Exception thrown when validation fails
 */
class ValidationException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, cause)