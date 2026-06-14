package com.uansari.newswise.core.common.result

sealed class Result<out T> {
    data object Loading : Result<Nothing>()

    data class Error(
        val exception: Throwable? = null, val message: String? = null
    ) : Result<Nothing>()

    data class Success<T>(val data: T) : Result<T>()
}

fun <T> Result<T>.isSuccess() = this is Result.Success
fun <T> Result<T>.isError() = this is Result.Error
fun <T> Result<T>.isLoading() = this is Result.Loading

fun <T> Result<T>.getOrNull() = (this as? Result.Success)?.data

fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
    is Result.Loading -> this
}