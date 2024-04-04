package com.anshyeon.fashioncode.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val commentId: String,
    val postId: String,
    val body: String,
    val writer: String,
    val nickName: String,
    val createdDate: String,
    val profileImageUrl: String? = null,
    val replyList: List<Reply>? = null,
)