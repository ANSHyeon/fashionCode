package com.anshyeon.fashioncode.util

import androidx.compose.ui.Modifier

inline fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier) =
    if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }