package com.gijun.posserver.infrastructure.adapter.`in`.web.common

import com.gijun.posserver.domain.common.exception.DomainException
import com.gijun.posserver.domain.common.exception.EntityNotFoundException
import com.gijun.posserver.domain.common.exception.DuplicateEntityException
import com.gijun.posserver.domain.common.exception.InvalidArgumentException
import com.gijun.posserver.domain.common.exception.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Entity not found: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("ENTITY_NOT_FOUND", ex.message ?: "Entity not found"))
    }

    @ExceptionHandler(DuplicateEntityException::class)
    fun handleDuplicateEntityException(ex: DuplicateEntityException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Duplicate entity: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error("DUPLICATE_ENTITY", ex.message ?: "Entity already exists"))
    }

    @ExceptionHandler(InvalidArgumentException::class)
    fun handleInvalidArgumentException(ex: InvalidArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Invalid argument: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("INVALID_ARGUMENT", ex.message ?: "Invalid argument"))
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(ex: ValidationException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Validation error: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("VALIDATION_ERROR", ex.message ?: "Validation failed"))
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Domain error: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("DOMAIN_ERROR", ex.message ?: "Domain error occurred"))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Invalid argument: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("INVALID_ARGUMENT", ex.message ?: "Invalid argument"))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Resource not found: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("RESOURCE_NOT_FOUND", ex.message ?: "Resource not found"))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val errors = ex.bindingResult.allErrors
            .filterIsInstance<FieldError>()
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }

        logger.warn("Validation failed: {}", errors)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("VALIDATION_ERROR", errors))
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Runtime error: {}", ex.message, ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("INTERNAL_ERROR", "Internal server error occurred"))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Unexpected error: {}", ex.message, ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"))
    }
}