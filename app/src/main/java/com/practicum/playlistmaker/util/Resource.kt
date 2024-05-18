package com.practicum.playlistmaker.util

sealed class Resource<T>(val data: T? = null, val resultCode: Int? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(resultCode: Int, data: T? = null) : Resource<T>(data, resultCode)
}