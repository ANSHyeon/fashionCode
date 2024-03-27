package com.anshyeon.fashioncode.ui.screen.home.style.create

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Clothes
import com.anshyeon.fashioncode.data.model.ClothesType
import com.anshyeon.fashioncode.data.repository.StyleRepository
import com.anshyeon.fashioncode.network.model.ApiResultSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StyleCreateViewModel @Inject constructor(
    private val styleRepository: StyleRepository
) : ViewModel() {

    private var currentClothesType: ClothesType = ClothesType.OUTER

    private val _clothesList = MutableStateFlow(listOf(Clothes()))
    val clothesList: StateFlow<List<Clothes>> = _clothesList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isCutOutLoading = MutableStateFlow(false)
    val isCutOutLoading: StateFlow<Boolean> = _isCutOutLoading

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    fun cutoutImage(bitmap: Bitmap) {
        _isCutOutLoading.value = true
        viewModelScope.launch {
            val getDropBoxTokenJob = viewModelScope.async {
                styleRepository.getDropBoxToken()
            }
            val getAdobeTokenJob = viewModelScope.async {
                styleRepository.getAdobeLoginToken()
            }
            val dropboxToken = getDropBoxTokenJob.await()
            val getDropBoxLinkJob = viewModelScope.async {
                styleRepository.getDropBoxLink(dropboxToken, bitmap)
            }
            val adobeToken = getAdobeTokenJob.await()
            val (dropBoxLink, path) = getDropBoxLinkJob.await()

            styleRepository.createClothes(
                currentClothesType,
                adobeToken,
                dropboxToken,
                dropBoxLink,
                path,
                {},
                {
                    _showSnackBar.value = true
                    _snackBarText.value = "잠시 후 다시 시도해 주십시오"
                }
            ).collectLatest { response ->
                if (response is ApiResultSuccess) {
                    addClothes(response.data)
                }
                _isCutOutLoading.value = false
            }
        }
    }

    fun changeClothesType(type: ClothesType) {
        currentClothesType = type
    }

    fun addClothes(clothes: Clothes) {
        _clothesList.value = _clothesList.value.toMutableList().apply {
            add(clothes)
        }.toList()
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }
}