package com.gijun.posserver.domain.common.exception

class DuplicateEntityException(
    entityName: String,
    identifier: Any
) : DomainException("$entityName already exists with identifier: $identifier")