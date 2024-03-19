package com.anshyeon.fashioncode.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Post(
    val postId: String,
    val title: String,
    val body: String,
    val writer: String,
    val createdDate: String,
    val imageLocations: List<String>? = null,
    val profileImageUrl: String? = null,
    val imageUrlList: List<String> = emptyList(),
    val commentList: List<String> = emptyList()
) : Parcelable