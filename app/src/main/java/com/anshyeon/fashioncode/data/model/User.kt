package com.anshyeon.fashioncode.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: String = "",
    val key: String? = null,
    val nickName: String = "",
    val profileUri: String? = null,
    val profileUrl: String? = null
)