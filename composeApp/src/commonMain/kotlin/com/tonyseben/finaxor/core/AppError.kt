package com.tonyseben.finaxor.core

/**
 * Application errors with proper categorization
 */
sealed class AppError(open val message: String, open val cause: Throwable? = null) {

    // Network errors
    data class NetworkError(
        override val message: String = "Network error occurred",
        override val cause: Throwable? = null
    ) : AppError(message, cause)

    // Authentication errors
    data class AuthError(
        override val message: String = "Authentication failed",
        override val cause: Throwable? = null
    ) : AppError(message, cause)

    // Permission errors
    data class PermissionError(
        override val message: String = "Permission denied",
        override val cause: Throwable? = null
    ) : AppError(message, cause)

    // Validation errors
    data class ValidationError(
        val field: String,
        override val message: String,
        override val cause: Throwable? = null
    ) : AppError(message, cause)

    // Not found errors
    data class NotFoundError(
        val resourceType: String,
        val resourceId: String,
        override val cause: Throwable? = null
    ) : AppError("$resourceType with id $resourceId not found", cause)

    // Business logic errors
    data class BusinessError(
        override val message: String,
        override val cause: Throwable? = null
    ) : AppError(message, cause)

    // Unknown errors
    data class UnknownError(
        override val message: String = "An unknown error occurred",
        override val cause: Throwable? = null
    ) : AppError(message, cause)
}