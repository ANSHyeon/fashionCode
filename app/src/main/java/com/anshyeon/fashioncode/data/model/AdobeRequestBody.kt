package com.anshyeon.fashioncode.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AdobeRequestBody(
    val input: AdobeInput,
    val output: AdobeOutPut,
)

@Serializable
data class AdobeInput(
    val href: String,
    val storage: String,
)

@Serializable
data class AdobeOutPut(
    val href: String,
    val storage: String,
)