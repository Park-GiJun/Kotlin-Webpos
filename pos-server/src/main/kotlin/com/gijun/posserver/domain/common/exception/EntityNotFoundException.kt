package com.gijun.posserver.domain.common.exception

class EntityNotFoundException(
    entityName: String,
    identifier: Any
) : DomainException("$entityName not found with identifier: $identifier")