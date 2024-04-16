package com.anshyeon.fashioncode.data.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "styles")
data class LocalStyle(
    @PrimaryKey val styleId: String = "",
    val userId: String = "",
    @ColumnInfo(name = "date") val date: String = "",
    @ColumnInfo(name = "image") val image: Bitmap? = null,
)