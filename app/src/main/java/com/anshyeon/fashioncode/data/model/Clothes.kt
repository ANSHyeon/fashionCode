package com.anshyeon.fashioncode.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Clothes(
    val clothesId: String,
    val userId: String,
    val type: ClothesType,
    val imageUri: String? = null,
    val imageUrl: String? = null
)

enum class ClothesType(name: String) {
    OUTER("아우터"),
    TOP("상의"),
    BOTTOM("하의"),
    DRESS("한벌옷"),
    SHOES("신발"),
    BAG("가방"),
    CAP("모자")
}