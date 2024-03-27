package com.anshyeon.fashioncode.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DropBoxRequestBody(
    @SerialName("commit_info") val commitInfo: CommitInfo,
    val duration: Int,
)

@Serializable
data class DropBoxDownloadRequestBody(
    val path: String,
)

@Serializable
data class CommitInfo(
    @SerialName("autorename") val autoReName: Boolean,
    val mode: String,
    val mute: Boolean,
    val path: String,
    @SerialName("strict_conflict") val strictConflict: Boolean,
)