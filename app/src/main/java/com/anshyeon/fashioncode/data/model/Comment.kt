package com.anshyeon.fashioncode.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val commentId: String,
    val body: String,
    val writer: String,
    val nickName: String,
    val createdDate: String,
    val profileImageUri: String? = null,
    val profileImageUrl: String? = null,
)