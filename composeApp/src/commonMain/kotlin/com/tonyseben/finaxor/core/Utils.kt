package com.tonyseben.finaxor.core

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

// ============================================
// UTILITY FUNCTIONS
// ============================================

fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()

fun formatCurrency(amount: Double): String {
    val rounded = amount.toLong()
    val formatted = rounded.toString().reversed().chunked(3).joinToString(",").reversed()
    return "₹$formatted"
}


fun formatDate(millis: Long): String {
    val instant = Instant.fromEpochMilliseconds(millis)
    val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "${localDate.dayOfMonth}/${localDate.monthNumber}/${localDate.year}"
}

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