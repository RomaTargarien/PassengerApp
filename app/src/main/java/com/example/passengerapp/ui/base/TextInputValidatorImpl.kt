package com.example.passengerapp.ui.base

import android.content.Context
import com.example.passengerapp.R
import com.example.passengerapp.ui.util.TextInputResource

class TextInputValidatorImpl(val context: Context) : TextInputValidator {

    override fun validateName(name: String): TextInputResource<String> =
        when {
            name.length < MIN_NAME_LENGTH -> {
                TextInputResource.ErrorInput(
                    context.getString(
                        R.string.error_name_too_short,
                        MIN_NAME_LENGTH
                    )
                )
            }
            name.length > MAX_NAME_LENGTH -> {
                TextInputResource.ErrorInput(
                    context.getString(
                        R.string.error_name_too_long,
                        MAX_NAME_LENGTH
                    )
                )
            }
            name.isEmpty() -> {
                TextInputResource.ErrorInput(context.getString(R.string.error_empty_input))
            }
            else -> {
                TextInputResource.SuccessInput()
            }
        }


    override fun validateTrips(trips: String): TextInputResource<String> =
        if (trips.isEmpty()) {
            TextInputResource.ErrorInput(context.getString(R.string.error_empty_input))
        } else {
            TextInputResource.SuccessInput()
        }

    companion object {
        private const val MAX_NAME_LENGTH = 20
        private const val MIN_NAME_LENGTH = 3
    }
}