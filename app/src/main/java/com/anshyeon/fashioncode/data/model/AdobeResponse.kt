package com.anshyeon.fashioncode.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdobeAuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int
)

@Serializable
data class AdobeCutoutResponse(
    @SerialName("_links") val links: Self,
)

@Serializable
data class Self(
    val self: Href,
)

@Serializable
data class Href(
    val href: String,
)