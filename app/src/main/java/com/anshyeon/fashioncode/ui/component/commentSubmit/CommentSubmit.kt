package com.anshyeon.fashioncode.ui.component.commentSubmit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anshyeon.fashioncode.ui.theme.Gray

@Composable
fun CommentSubmit(
    text: String,
    onTextChanged: (body: String) -> Unit,
    onclick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            modifier = Modifier
                .height(30.dp)
                .weight(1f),
            value = text,
            onValueChange = { onTextChanged(it) },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .background(color = Gray, shape = RoundedCornerShape(size = 5.dp)),
                    contentAlignment = Alignment.CenterStart

                ) {
                    innerTextField()
                }
            },
        )
        IconButton(
            modifier = Modifier
                .size(24.dp),
            onClick = {
                if (text.isNotEmpty()) {
                    onclick()
                }
            }) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
            )
        }
    }
}