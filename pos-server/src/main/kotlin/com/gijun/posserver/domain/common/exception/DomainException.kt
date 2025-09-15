package com.gijun.posserver.domain.common.exception

abstract class DomainException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)