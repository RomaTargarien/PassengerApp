package com.example.passengerapp.ui.util.extensions

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button

fun View.animateColorTransition(
    startColor: Int,
    endColor: Int,
    animationDuration: Long = 500L
) {
    ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor).apply {
        duration = animationDuration
        interpolator = DecelerateInterpolator()
        addUpdateListener {
            val color = it.animatedValue as Int
            when(this@animateColorTransition) {
                is ViewGroup -> {
                    setBackgroundColor(color)
                }
                is Button -> {
                    background.setTint(color)
                }
                else -> { }
            }
        }
    }.start()
}

fun View.animateMarginEnd(marginEnd: Float) {
    val params = this.layoutParams as ViewGroup.MarginLayoutParams
    params.marginEnd = marginEnd.toInt()
    layoutParams = params
}

fun View.rotate(degree: Float, duration: Long = 0) {
    animate().rotation(degree).setDuration(duration).start()
}

fun View.scale(from: Int, to: Int, duration: Long) {
    val anim = ValueAnimator.ofInt(from, to)
    anim.addUpdateListener { valueAnimator ->
        val newHeight = valueAnimator.animatedValue as Int
        val newLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
        newLayoutParams.height = newHeight
        layoutParams = newLayoutParams
    }
    anim.duration = duration
    anim.start()
}

fun View.setHeight(height: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.height = height
    layoutParams = params
}



