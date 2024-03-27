package com.anshyeon.fashioncode.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Clothes(
    val clothesId: String = "",
    val type: ClothesType = ClothesType.ADD,
    val imageUri: String? = null,
    val imageUrl: String? = null
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