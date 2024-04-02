package com.anshyeon.fashioncode.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Style(
    val styleId: String,
    val writer: String,
    val createdDate: String,
    val nickName: String,
    val profileImageUrl: String? = null,
    val imageLocation: String? = null,
    val imageUrl: String? = null,
)