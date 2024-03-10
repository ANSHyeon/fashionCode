package com.anshyeon.fashioncode

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.anshyeon.fashioncode.ui.screen.home.HomeScreen
import com.anshyeon.fashioncode.ui.screen.signin.launch.SignInViewModel
import com.anshyeon.fashioncode.ui.theme.FashionCodeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<SignInViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.tryLogin(this)

        lifecycleScope.launch {
            viewModel.loginResult.collect { isLogin ->
                if (!isLogin) {
                    startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                }
            }
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