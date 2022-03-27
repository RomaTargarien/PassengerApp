package com.example.passengerapp.ui.screens

import android.support.annotation.DrawableRes

interface GoBackAppBarBehavior

interface HasCustomAction {
    fun getCustomAction(): CustomAction

}

class CustomAction(
    @DrawableRes val iconRes: Int,
    val onCustomAction: () -> Unit
)