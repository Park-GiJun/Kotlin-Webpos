package com.gijun.mainserver.domain.common.exception

/**
 * Exception thrown when trying to create a duplicate entity
 */
class DuplicateEntityException(
    entityType: String,
    identifier: String,
    cause: Throwable? = null
) : DomainException("$entityType with $identifier already exists", cause)