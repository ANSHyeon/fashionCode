package com.anshyeon.fashioncode.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class User(
    val userId: String = "",
    val nickName: String = "",
    val email: String = "",
    val profileUri: String? = null,
    val profileUrl: String? = null
) : Parcelable