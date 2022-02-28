package com.example.passengerapp.bindingAdapters

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("textNumber")
fun TextView.textNumber(number: Number) {
    text = number.toString()
}