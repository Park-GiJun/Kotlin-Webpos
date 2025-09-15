package com.gijun.mainserver.domain.common.exception

/**
 * Base class for all domain exceptions
 */
abstract class DomainException(
    override val message: String,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)