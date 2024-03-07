package com.anshyeon.fashioncode.ui.screen.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.ui.theme.FashionCodeTheme

@Composable
fun SignInScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.2f))
        Text(
            text = "Fashion Code",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.fillMaxHeight(0.3f)
        )
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))
        SignInGoogleButton()
    }
}

@Composable
fun SignInGoogleButton() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {},
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google_sign_in),
            contentDescription = "Google sign button"
        )
    }
}