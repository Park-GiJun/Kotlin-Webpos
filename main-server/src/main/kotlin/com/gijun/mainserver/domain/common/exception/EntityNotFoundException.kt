package com.gijun.mainserver.domain.common.exception

/**
 * Exception thrown when an entity is not found
 */
class EntityNotFoundException(
    entityType: String,
    id: Any,
    cause: Throwable? = null
) : DomainException("$entityType with id $id not found", cause)