package com.anshyeon.fashioncode.data.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothes")
data class Clothes(
    @PrimaryKey val clothesId: String = "",
    val userId: String = "",
    @ColumnInfo(name = "type") val type: ClothesType = ClothesType.ADD,
    @ColumnInfo(name = "image") val image: Bitmap? = null,
)

enum class ClothesType(name: String) {
    ADD("추가하기"),
    OUTER("아우터"),
    TOP("상의"),
    BOTTOM("하의"),
    DRESS("한벌옷"),
    SHOES("신발"),
    BAG("가방"),
    CAP("모자")
}