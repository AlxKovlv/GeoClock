package com.example.geoclock.ui

import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.geoclock.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set custom color for the navigation bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val window = window
            val controller = window.insetsController
            if (controller != null) {
                controller.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
                window.navigationBarColor = ContextCompat.getColor(this, R.color.my_primary_color)
            }
        }

    }
}