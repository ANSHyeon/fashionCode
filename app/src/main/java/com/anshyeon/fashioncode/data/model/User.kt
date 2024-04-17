package com.anshyeon.fashioncode.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "users")
@Serializable
data class User(
    @PrimaryKey val userId: String = "",
    @ColumnInfo(name = "key") val key: String? = null,
    @ColumnInfo(name = "nick_name") val nickName: String = "",
    @ColumnInfo(name = "profile_url") val profileUrl: String? = null
)