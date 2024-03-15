package com.anshyeon.fashioncode.data.dataSource

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ImageDataSource @Inject constructor() {

    suspend fun uploadImage(uri: Uri): String {
        val storageRef = FirebaseStorage.getInstance().reference
        val location = "images/${uri.lastPathSegment}_${System.currentTimeMillis()}"
        val imageRef = storageRef.child(location)
        imageRef.putFile(uri).await()
        return location
    }

    suspend fun uploadImages(imageList: List<Uri>): List<String> = coroutineScope {
        val uploadImagesJob = imageList.map { imageUri ->
            async {
                uploadImage(imageUri)
            }
        }
        uploadImagesJob.awaitAll()
    }

    suspend fun downloadImage(location: String): String {
        val storageRef = FirebaseStorage.getInstance().reference
        return storageRef.child(location).downloadUrl.await().toString()
    }
}