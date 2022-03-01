package com.example.passengerapp.ui.base

import android.content.Context
import com.example.passengerapp.R
import com.example.passengerapp.ui.util.TextInputResource

class ValidationImpl(val context: Context) : Validation {

    override fun validateName(name: String): TextInputResource<String> {
        if (name.length < MIN_NAME_LENGTH) {
            return TextInputResource.ErrorInput(
                context.getString(
                    R.string.error_name_too_short,
                    MIN_NAME_LENGTH
                )
            )
        }
        if (name.length > MAX_NAME_LENGTH) {
            return TextInputResource.ErrorInput(
                context.getString(
                    R.string.error_name_too_long,
                    MAX_NAME_LENGTH
                )
            )
        }
        if (name.isEmpty()) {
            return TextInputResource.ErrorInput(context.getString(R.string.error_empty_input))
        }
        return TextInputResource.SuccessInput()
    }

    override fun validateTrips(trips: String): TextInputResource<String> {
        if (trips.isEmpty()) {
            return TextInputResource.ErrorInput(context.getString(R.string.error_empty_input))
        }
        return TextInputResource.SuccessInput()
    }

    companion object {
        private const val MAX_NAME_LENGTH = 20
        private const val MIN_NAME_LENGTH = 3
    }
}