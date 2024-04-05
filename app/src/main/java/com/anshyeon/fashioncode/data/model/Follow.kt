package com.anshyeon.fashioncode.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Follow(
    val followId: String,
    val follower: String,
    val following: String,
    val nickName: String? = null,
    val profileUrl: String? = null,
)