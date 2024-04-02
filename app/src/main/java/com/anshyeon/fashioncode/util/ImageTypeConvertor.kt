package com.anshyeon.fashioncode.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Picture
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

object ImageTypeConvertor {

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

    fun createBitmapFromPicture(picture: Picture): Bitmap {
        val bitmap = Bitmap.createBitmap(
            picture.width,
            picture.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        canvas.drawPicture(picture)
        return bitmap
    }
}