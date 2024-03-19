package com.anshyeon.fashioncode.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Comment(
    val commentId: String,
    val body: String,
    val writer: String,
    val nickName: String,
    val createdDate: String,
    val profileImageUri: String? = null,
    val profileImageUrl: String? = null,
) : Parcelable