package com.amonga.snifflicks.core.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class BaseSuspendingUseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) {

	/** Executes the use case asynchronously and returns a [ResultState].
	 *
	 * @return a [ResultState].
	 *
	 * @param parameters the input parameters to run the use case with
	 */
	suspend operator fun invoke(parameters: P): ResultState<R> {
		return try {
			withContext(coroutineDispatcher) {
				ResultState.Success(execute(parameters))
			}
		} catch (e: EmptyStateException) {
			return ResultState.Empty
		} catch (t: Throwable) {
			Timber.e(t)
			val result: ResultState<R>? = onException(t, parameters)
			result ?: ResultState.Error(t)
		}
	}

	abstract fun onException(throwable: Throwable, parameters: P): ResultState<R>?

	/**
	 * Override this to set the code to be executed.
	 * @throws EmptyStateException throw this for getting [ResultState.Empty]
	 * @throws Exception
	 */
	@Throws(EmptyStateException::class, Exception::class)
	protected abstract suspend fun execute(input: P): R
}
