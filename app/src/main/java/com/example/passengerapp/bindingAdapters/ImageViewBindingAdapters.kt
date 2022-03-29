package com.example.passengerapp.bindingAdapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl")
fun ImageView.loadImage(url: String?) {
    url?.let {
        Picasso.get().load(it).into(this)
    }

}