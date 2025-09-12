package com.gijun.mainserver.domain.common.exception

class NullIdException(
    domain: String
) : IllegalArgumentException("$domain entity cannot have null ID")
