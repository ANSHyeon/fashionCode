package com.anshyeon.fashioncode.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: String = "",
    val nickName: String = "",
    val email: String = "",
    val profileUri: String? = null,
    val profileUrl: String? = null
)