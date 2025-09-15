package com.gijun.mainserver.infrastructure.adapter.`in`.web.common

import com.gijun.mainserver.domain.common.exception.*
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEntityException::class)
    fun handleDuplicateEntityException(
        ex: DuplicateEntityException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ApiResponse.error(
                    code = "DUPLICATE_ENTITY",
                    message = ex.message
                )
            )
    }

    @ExceptionHandler(NullIdException::class)
    fun handleNullIdException(
        ex: NullIdException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    code = "NULL_ID",
                    message = ex.message ?: "ID cannot be null"
                )
            )
    }

    @ExceptionHandler(InvalidArgumentException::class)
    fun handleInvalidArgumentException(
        ex: InvalidArgumentException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    code = "INVALID_ARGUMENT",
                    message = ex.message
                )
            )
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(
        ex: ValidationException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    code = "VALIDATION_ERROR",
                    message = ex.message
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val errors = ex.bindingResult.allErrors
            .mapNotNull { error ->
                when (error) {
                    is FieldError -> "${error.field}: ${error.defaultMessage}"
                    else -> error.defaultMessage
                }
            }
            .joinToString(", ")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                code = "VALIDATION_ERROR",
                message = errors.ifEmpty { "Validation failed" }
            ))
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(
        ex: EntityNotFoundException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ApiResponse.error(
                    code = "ENTITY_NOT_FOUND",
                    message = ex.message
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiResponse.error(
                    code = "INTERNAL_ERROR",
                    message = "An unexpected error occurred"
                )
            )
    }
}