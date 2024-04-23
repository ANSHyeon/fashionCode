package com.anshyeon.fashioncode.data.interceptror

import com.anshyeon.fashioncode.data.repository.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class DropBoxTokenInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository
) : Interceptor, Authenticator {

    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            val accessToken = tokenRepository.getDropBoxToken().first()
            val request = chain.request().putTokenHeader(accessToken)
            chain.proceed(request)
        }
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        val token = runBlocking(Dispatchers.IO) {
            val refreshToken = tokenRepository.getDropBoxRefreshToken()
            refreshToken?.let {
                tokenRepository.saveDropBoxToken(it)
                it
            }
        } ?: return null

        return response.request
            .newBuilder()
            .removeHeader(AUTHORIZATION)
            .addHeader(AUTHORIZATION, "Bearer $token")
            .build()
    }

    private fun Request.putTokenHeader(accessToken: String): Request {
        return this.newBuilder()
            .addHeader(AUTHORIZATION, "Bearer $accessToken")
            .build()
    }

    companion object {
        private const val AUTHORIZATION = "Authorization"
    }
}