package com.anshyeon.fashioncode.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Comment(
    val commentId: String,
    val dody: String,
    val writer: User,
    val createdDate: String
) : Parcelable