package com.anshyeon.fashioncode.ui.screen.signin.launch

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.BuildConfig
import com.anshyeon.fashioncode.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun SignInScreen(
    navController: NavHostController,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val googleSignInClient = getGoogleSignInClient(context)
    val signInRequestCode = 1
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract(googleSignInClient)) {
            try {
                val account = it?.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                        }
                    }
                }
            } catch (e: ApiException) {
            }
        }

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
        SignInGoogleButton {
            authResultLauncher.launch(signInRequestCode)
        }
    }
}

@Composable
fun SignInGoogleButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick),
        shadowElevation = 3.dp
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google_sign_in),
            contentDescription = "Google sign button"
        )
    }
}

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
        .build()

    return GoogleSignIn.getClient(context, signInOptions)
}


class AuthResultContract(private val googleSignInClient: GoogleSignInClient) :
    ActivityResultContract<Int, Task<GoogleSignInAccount>?>() {
    override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {
        return when (resultCode) {
            Activity.RESULT_OK -> GoogleSignIn.getSignedInAccountFromIntent(intent)
            else -> null
        }
    }

    override fun createIntent(context: Context, input: Int): Intent {
        return googleSignInClient.signInIntent.putExtra("input", input)
    }
}