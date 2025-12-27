package com.tonyseben.finaxor.data.mapper

import com.tonyseben.finaxor.domain.model.AuthUser
import dev.gitlive.firebase.auth.FirebaseUser

/**
 * Maps Firebase User to domain AuthUser
 */
fun FirebaseUser.toAuthUser(): AuthUser {
    return AuthUser(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoURL,
        isEmailVerified = isEmailVerified,
        providerId = providerData.firstOrNull()?.providerId ?: "unknown"
    )
}
