package com.tonyseben.finaxor.data.auth

import com.tonyseben.finaxor.core.Result
import com.tonyseben.finaxor.domain.model.AuthUser
import dev.gitlive.firebase.auth.FirebaseAuth

/**
 * Service interface for authentication providers.
 * Each provider implements this to handle its specific auth flow.
 */
interface AuthService {

    /**
     * Authenticate with this provider's specific credentials
     */
    suspend fun authenticate(auth: FirebaseAuth): Result<AuthUser>
}
