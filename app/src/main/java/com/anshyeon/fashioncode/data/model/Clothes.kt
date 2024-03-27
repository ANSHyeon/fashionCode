package com.anshyeon.fashioncode.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "clothes")
@Serializable
data class Clothes(
    @PrimaryKey val clothesId: String = "",
    @ColumnInfo(name = "type") val type: ClothesType = ClothesType.ADD,
    @ColumnInfo(name = "image_url") val imageUrl: String? = null
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