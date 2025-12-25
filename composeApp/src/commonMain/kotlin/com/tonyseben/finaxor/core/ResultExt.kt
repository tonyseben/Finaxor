package com.tonyseben.finaxor.core

// ============================================
// RESULT EXTENSION FUNCTIONS
// ============================================

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (AppError) -> Unit): Result<T> {
    if (this is Result.Error) action(error)
    return this
}

inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) action()
    return this
}

fun <T> Result<T>.getOrNull(): T? = when (this) {
    is Result.Success -> data
    else -> null
}

fun <T> Result<T>.getOrThrow(): T = when (this) {
    is Result.Success -> data
    is Result.Error -> throw error.cause ?: Exception(error.message)
    is Result.Loading -> throw IllegalStateException("Result is still loading")
}

fun <T> Result<T>.getOrDefault(default: T): T = when (this) {
    is Result.Success -> data
    else -> default
}

fun <T> Result<T>.getOrElse(onError: (AppError) -> T): T = when (this) {
    is Result.Success -> data
    is Result.Error -> onError(error)
    is Result.Loading -> throw IllegalStateException("Result is still loading")
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
    is Result.Loading -> this
}

inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
    is Result.Success -> transform(data)
    is Result.Error -> this
    is Result.Loading -> this
}