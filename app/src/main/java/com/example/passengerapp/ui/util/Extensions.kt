package com.example.passengerapp.ui.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

fun <T> Flow<T>.toStateFlow(initialValue: T, scope: CoroutineScope): StateFlow<T> {
    return this.stateIn(scope, SharingStarted.Lazily, initialValue)
}