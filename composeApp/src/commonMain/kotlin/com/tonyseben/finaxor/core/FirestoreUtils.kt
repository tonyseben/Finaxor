package com.tonyseben.finaxor.core

import dev.gitlive.firebase.firestore.Timestamp

fun generateFirestoreId(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..20)
        .map { chars.random() }
        .joinToString("")
}

/**
 * Converts a Firestore Timestamp to epoch milliseconds.
 */
fun Timestamp.toEpochMillis(): Long = (seconds * 1000) + (nanoseconds / 1_000_000)

/**
 * Safely converts a Firestore value to Long (handles Timestamp, Long, Double, etc.).
 */
fun Any?.toTimestampLong(): Long = when (this) {
    is Timestamp -> this.toEpochMillis()
    is Long -> this
    is Double -> this.toLong()
    is Number -> this.toLong()
    else -> 0L
}