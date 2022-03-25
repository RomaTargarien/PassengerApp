package com.example.passengerapp.ui.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.passengerapp.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}