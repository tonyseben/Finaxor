package com.tonyseben.finaxor.domain.usecase

import com.tonyseben.finaxor.core.Result
import kotlinx.coroutines.flow.Flow

/**
 * Base Use Case interface for operations that return a Flow
 */
interface FlowUseCase<in P, out R> {
    operator fun invoke(params: P): Flow<Result<R>>
}