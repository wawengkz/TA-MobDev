package com.example.familyflow.util

import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val exception: Exception, val code: Int? = null, val message: String? = null) : ApiResult<Nothing>()
}

object ApiErrorHandler {

    fun <T> handleApiError(e: Exception): ApiResult.Error {
        return when (e) {
            is SocketTimeoutException -> {
                Timber.e(e, "Network timeout")
                ApiResult.Error(e, message = "Network request timed out. Please try again.")
            }
            is IOException -> {
                Timber.e(e, "Network error")
                ApiResult.Error(e, message = "Network error. Please check your connection.")
            }
            else -> {
                Timber.e(e, "Unknown error")
                ApiResult.Error(e, message = "An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun <T> handleApiResponse(response: Response<T>): ApiResult<T> {
        val rawResponse = response.errorBody()?.string()
        Timber.e("Raw API Response: $rawResponse") // Debugging

        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResult.Success(body)
            } else {
                Timber.e("Response body is null")
                ApiResult.Error(
                    Exception("Response body is empty"),
                    response.code(),
                    "Server returned an empty response"
                )
            }
        } else {
            val errorMsg = rawResponse ?: "Unknown error"
            Timber.e("API error: ${response.code()} - $errorMsg")

            val errorMessage = when (response.code()) {
                401 -> "Authentication required. Please log in again."
                404 -> "Resource not found."
                500 -> "Server error. Try again later."
                else -> "Error: ${response.message()}"
            }

            ApiResult.Error(Exception(errorMsg), response.code(), errorMessage)
        }
    }
}
