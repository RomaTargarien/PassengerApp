package com.example.youngchemist.ui.custom.snack_bar

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.passengerapp.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import kotlinx.android.synthetic.main.item_custom_snackbar.view.*
import kotlin.math.ceil

class CustomSnackBar(
    content: CustomSnackBarView,
    parent: ViewGroup
) : BaseTransientBottomBar<CustomSnackBar>(parent, content, content) {

    companion object {

        private var countDownTimer: CountDownTimer? = null
        private var onTik: ((Long) -> Unit)? = null

        fun make(viewGroup: ViewGroup, modelTitle: String): CustomSnackBar {
            val customView = LayoutInflater.from(viewGroup.context).inflate(
                R.layout.layout_custom_snakbar,
                viewGroup,
                false
            ) as CustomSnackBarView
            launchTimer()
            customView.timeLeft_circularProgressBar.apply {
                setProgressWithAnimation(100f, TIME_LEFT)
            }
            setOnTikListener {
                val number = ceil(it.toDouble() / 1000)
                customView.tv_time_left.text = number.toInt().toString()
            }
            customView.tv_model_title.text = modelTitle
            return CustomSnackBar(customView,viewGroup)
        }

        fun CustomSnackBar.setOnClickListener(listener: (CustomSnackBar) -> Unit): CustomSnackBar {
            this.view.tv_undo.setOnClickListener {
                listener(this)
            }
            return this
        }

        private fun launchTimer() {
            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer(TIME_LEFT, SECOND_IN_MILLIS) {
                override fun onTick(p0: Long) {
                    onTik?.let { tik ->
                        tik(p0)
                    }
                }

                override fun onFinish() {}
            }
            countDownTimer?.start()
        }

        private fun setOnTikListener(listener: (Long) -> Unit) {
            onTik = listener
        }

        private const val SECOND_IN_MILLIS = 1000L
        private const val TIME_LEFT = 3000L
    }
}