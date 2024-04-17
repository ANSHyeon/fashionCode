package com.anshyeon.fashioncode.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Reply(
    val replyId: String,
    val commentId: String,
    val body: String,
    val writer: String,
    val createdDate: String,
)