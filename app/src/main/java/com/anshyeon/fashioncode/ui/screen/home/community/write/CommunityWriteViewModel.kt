package com.anshyeon.fashioncode.ui.screen.home.community.write

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CommunityWriteViewModel @Inject constructor(
) : ViewModel() {

    private val _selectedImageList = MutableStateFlow(emptyList<Uri>())
    val selectedImageList: StateFlow<List<Uri>> = _selectedImageList

    fun addImageUris(imageUris: List<Uri>) {
        _selectedImageList.value = mutableListOf<Uri>().apply {
            addAll(_selectedImageList.value)
            addAll(imageUris)
        }
    }

    fun removeImageUris(index: Int) {
        _selectedImageList.value = _selectedImageList.value.toMutableList().apply {
            removeAt(index)
        }.toList()
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }
}