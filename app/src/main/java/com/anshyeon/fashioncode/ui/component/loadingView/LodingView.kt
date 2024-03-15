package com.anshyeon.fashioncode.ui.component.loadingView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.anshyeon.fashioncode.ui.theme.Orange

@Composable
fun LoadingView(
    isLoading: Boolean,
    boxColor: Color = Color.Transparent,
    circularProgressIndicatorColor: Color = Orange
) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(boxColor),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = circularProgressIndicatorColor)
        }
    }
}
