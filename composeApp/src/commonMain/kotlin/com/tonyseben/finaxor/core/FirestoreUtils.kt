package com.tonyseben.finaxor.core

fun generateFirestoreId(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..20)
        .map { chars.random() }
        .joinToString("")
}