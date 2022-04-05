package com.example.passengerapp.bindingAdapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.passengerapp.ui.util.TextInputResource

@BindingAdapter("textNumber")
fun TextView.textNumber(number: Number) {
    text = number.toString()
}

@BindingAdapter("processValidationResult")
fun TextView.processValidationResult(state: TextInputResource<String>) {
    when (state) {
        is TextInputResource.InputInProcess -> {
            animate().alpha(0f).setDuration(300).start()
        }
        is TextInputResource.ErrorInput -> {
            state.message?.let { text = it }
            animate().alpha(1f).setDuration(300).start()
        }
        is TextInputResource.SuccessInput -> {
            animate().alpha(0f).setDuration(300).start()
        }
    }
}