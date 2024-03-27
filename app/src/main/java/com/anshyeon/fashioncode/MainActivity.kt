package com.anshyeon.fashioncode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.anshyeon.fashioncode.ui.screen.home.HomeScreen
import com.anshyeon.fashioncode.ui.theme.FashionCodeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Class
                .forName("androidx.compose.material.TabRowKt")
                .getDeclaredField("ScrollableTabRowMinimumTabWidth").apply {
                    isAccessible = true
                }.set(this, 0f)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            FashionCodeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}