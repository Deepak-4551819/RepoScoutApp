package com.justunfold.reposcoutapp.core.network

sealed interface ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>
    data class Error(
        val message: String,
        val statusCode: Int? = null,
        val isRateLimit: Boolean = false,
        val resetTimeEpochSeconds: Long? = null
    ) : ApiResult<Nothing>
}

inline fun <T> ApiResult<T>.onSuccess(action: (value: T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T> ApiResult<T>.onError(action: (error: ApiResult.Error) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) action(this)
    return this
}
