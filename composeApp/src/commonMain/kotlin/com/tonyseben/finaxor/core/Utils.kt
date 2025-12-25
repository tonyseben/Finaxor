package com.tonyseben.finaxor.core

// ============================================
// UTILITY FUNCTIONS
// ============================================

/**
 * Wrap a suspend function call in Result
 */
suspend inline fun <T> resultOf(block: () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: Exception) {
        Result.Error(AppError.UnknownError(e.message ?: "Unknown error", e))
    }
}

/**
 * Convert exception to appropriate AppError
 */
fun Throwable.toAppError(): AppError {
    return when (this) {
        is IllegalArgumentException -> AppError.ValidationError(
            field = "unknown",
            message = message ?: "Validation failed",
            cause = this
        )

        is IllegalStateException -> AppError.BusinessError(
            message = message ?: "Business rule violation",
            cause = this
        )

        is NoSuchElementException -> AppError.NotFoundError(
            resourceType = "Resource",
            resourceId = "unknown",
            cause = this
        )

        else -> AppError.UnknownError(
            message = message ?: "An error occurred",
            cause = this
        )
    }
}