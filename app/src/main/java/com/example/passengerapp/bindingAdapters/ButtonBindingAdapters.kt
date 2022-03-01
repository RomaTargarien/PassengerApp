package com.example.passengerapp.bindingAdapters

import android.widget.Button
import androidx.databinding.BindingAdapter


@BindingAdapter("enableButton")
fun Button.enableButton(isButtonEnabled: Boolean) {
    isEnabled = isButtonEnabled
    alpha = if (isButtonEnabled) 1f else 0.5f
}