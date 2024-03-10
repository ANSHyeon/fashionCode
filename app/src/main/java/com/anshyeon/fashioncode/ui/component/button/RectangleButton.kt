package com.anshyeon.fashioncode.ui.component.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun RectangleButton(modifier: Modifier = Modifier, text: String, onclick: () -> Unit) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RectangleShape,
            onClick = {
                onclick()
            }
        ) {
            Text(text = text)
        }
    }
}
