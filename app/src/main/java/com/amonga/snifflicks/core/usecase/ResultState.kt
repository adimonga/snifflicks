package com.amonga.snifflicks.core.usecase

sealed class ResultState<out T> {

	data class Success<out T>(val data: T): ResultState<T>()
	data class Error(val throwable: Throwable): ResultState<Nothing>()
	data object Empty: ResultState<Nothing>()
}
