package com.gijun.mainserver.domain.common.exception

/**
 * Exception thrown when invalid arguments are provided
 */
class InvalidArgumentException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, cause)