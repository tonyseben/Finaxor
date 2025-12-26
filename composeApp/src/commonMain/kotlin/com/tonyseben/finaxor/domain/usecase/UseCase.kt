package com.tonyseben.finaxor.domain.usecase

import com.tonyseben.finaxor.core.Result

/**
 * Base Use Case interface for operations that return a single result
 */
interface UseCase<in P, out R> {
    suspend operator fun invoke(params: P): Result<R>
}