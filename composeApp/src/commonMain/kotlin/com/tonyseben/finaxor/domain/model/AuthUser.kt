package com.tonyseben.finaxor.domain.model

/**
 * Represents the authenticated user from Firebase Auth.
 * Separate from User (Firestore profile) to maintain separation of concerns.
 */
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean,
    val providerId: String
)
