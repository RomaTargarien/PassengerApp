package com.example.passengerapp.ui.screens.contract

import android.support.annotation.DrawableRes

interface GoBackAppBarBehavior


interface CustomActionFragment {
    fun getCustomAction(): CustomAction
}

class CustomAction(
    @DrawableRes val iconRes: Int,
    val onCustomAction: () -> Unit
)