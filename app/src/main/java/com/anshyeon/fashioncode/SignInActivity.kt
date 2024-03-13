package com.anshyeon.fashioncode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.anshyeon.fashioncode.ui.graph.AuthNavGraph
import com.anshyeon.fashioncode.ui.theme.FashionCodeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FashionCodeTheme {
                AuthNavGraph()
            }
        }
    }
}