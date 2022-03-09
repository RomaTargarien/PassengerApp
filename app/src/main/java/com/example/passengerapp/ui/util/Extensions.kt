package com.example.passengerapp.ui.util

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

fun <T> Flow<T>.toStateFlow(initialValue: T, scope: CoroutineScope): StateFlow<T> {
    return this.stateIn(scope, SharingStarted.Lazily, initialValue)
}

fun Fragment.snackbar(message: String) {
    Snackbar.make(this.requireView(), message, Snackbar.LENGTH_LONG).show()
}