package com.anshyeon.fashioncode.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult


suspend fun uriToBitmap(
    context: Context,
    it: String
): Bitmap? {
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(it)
        .allowHardware(false) // Disable hardware bitmaps.
        .build()

    val drawable = (loader.execute(request) as SuccessResult).drawable
    return (drawable as BitmapDrawable).bitmap
}