package com.example.passengerapp.ui.util

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

fun <T> Flow<T>.toStateFlow(initialValue: T, scope: CoroutineScope): StateFlow<T> {
    return this.stateIn(scope, SharingStarted.Lazily, initialValue)
}

fun Fragment.snackbar(message: String) {
    Snackbar.make(this.requireView(), message, Snackbar.LENGTH_LONG).show()
}

fun View.rotate(degree: Float) {
    animate().rotation(degree).setDuration(0).start()
}

fun View.animateMarginEnd(marginEnd: Float) {
    val params = this.layoutParams as ViewGroup.MarginLayoutParams
    params.marginEnd = marginEnd.toInt()
    layoutParams = params
}

fun Fragment.setUpKeyBoardEventListener(action: (Boolean) -> Unit) {
    setEventListener(requireActivity(),object : KeyboardVisibilityEventListener {
        override fun onVisibilityChanged(isOpen: Boolean) {
            action(isOpen)
        }
    })
}