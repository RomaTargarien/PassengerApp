package com.example.passengerapp.ui.util

import android.util.Log


inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: Exception) {
        Log.d(TAG, e.localizedMessage)
        Resource.Error(e.localizedMessage)
    }
}

const val TAG = "ErrorMessage"