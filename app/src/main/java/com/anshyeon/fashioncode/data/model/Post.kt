package com.anshyeon.fashioncode.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val postId: String,
    val title: String,
    val body: String,
    val writer: String,
    val createdDate: String,
    val imageUrlList: List<String> = emptyList(),
    val thumbnailImageUrl: String? = null,
    val commentList: List<String> = emptyList()
)