package com.anshyeon.fashioncode.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Post(
    val postId: String,
    val title: String,
    val dody: String,
    val writer: User,
    val createdDate: String,
    val imageLocations: List<String>? = null,
    val imageUrlList: List<String> = emptyList(),
    val commentList: List<Comment> = emptyList()
) : Parcelable