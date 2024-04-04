package com.anshyeon.fashioncode.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: String = "",
    val key: String? = null,
    val nickName: String = "",
    val profileUrl: String? = null
)