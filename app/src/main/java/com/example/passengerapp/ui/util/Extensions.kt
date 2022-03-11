package com.example.passengerapp.ui.util

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.passengerapp.R
import java.net.URLDecoder

fun String.urlDecode(): String = URLDecoder.decode(this, "utf8")

fun View.slideUp(context: Context, anitTime: Long, startOffSet: Long) {
    val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up).apply {
        duration = anitTime
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffSet
    }
    this.startAnimation(slideUp)
}

fun Fragment.slideUpViews(vararg views: View, animTime: Long = 300L, delay: Long = 150L) {
    for (i in views.indices) {
        views[i].slideUp(this.requireContext(), animTime, delay * i)
    }
}
