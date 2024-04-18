package com.anshyeon.fashioncode.ui.screen.home.style.create

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Clothes
import com.anshyeon.fashioncode.data.model.ClothesType
import com.anshyeon.fashioncode.data.repository.StyleRepository
import com.anshyeon.fashioncode.network.extentions.onError
import com.anshyeon.fashioncode.network.extentions.onException
import com.anshyeon.fashioncode.network.extentions.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
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

    private val _selectedClothesList = MutableStateFlow<List<Clothes>>(emptyList())
    val selectedClothesList: StateFlow<List<Clothes>> = _selectedClothesList

    private val _isCutOutLoading = MutableStateFlow(false)
    val isCutOutLoading: StateFlow<Boolean> = _isCutOutLoading

    private val _isCreateStyleLoading = MutableStateFlow(false)
    val isCreateStyleLoading: StateFlow<Boolean> = _isCreateStyleLoading

    private val _isInsertStyleLoading = MutableStateFlow(false)
    val isInsertStyleLoading: StateFlow<Boolean> = _isInsertStyleLoading

    private val _isDeleteClothesLoading = MutableStateFlow(false)
    val isDeleteClothesLoading: StateFlow<Boolean> = _isDeleteClothesLoading

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    init {
        viewModelScope.launch {
            getLocalClothesList()
        }
    }

    private suspend fun getLocalClothesList() {
        transformLocalMessageList().collectLatest {
            _clothesList.value = mutableListOf(Clothes()).apply {
                addAll(it)
            }.toList()
        }
    }

    private fun transformLocalMessageList(): Flow<List<Clothes>> {
        return styleRepository.getClothesListByRoom(
            onComplete = { }
        )
    }

    fun cutoutImage(context: Context, bitmap: Bitmap) {
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
                context
            ) {
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }
            _isCutOutLoading.value = false
        }
    }

    fun createStyle(navController: NavHostController, bitmap: Bitmap) {
        _isCreateStyleLoading.value = true
        viewModelScope.launch {
            val result = styleRepository.createStylePost(
                bitmap
            )
            result.onSuccess {
                _isCreateStyleLoading.value = false
                navigateBack(navController)
            }.onError { _, _ ->
                _isCreateStyleLoading.value = false
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }.onException {
                _isCreateStyleLoading.value = false
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }
        }
    }

    fun insertStyle(
        navController: NavHostController,
        bitmap: Bitmap,
        selectedDate: String
    ) {
        _isInsertStyleLoading.value = true
        viewModelScope.launch {
            styleRepository.saveStyle(bitmap, selectedDate) {
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }
            _isInsertStyleLoading.value = false
            navigateBack(navController)
        }
    }

    fun deleteClothes(
        clothes: Clothes,
    ) {
        _isDeleteClothesLoading.value = true
        viewModelScope.launch {
            styleRepository.deleteClothes(clothes) {
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }
            _isDeleteClothesLoading.value = false
        }
    }

    fun addSelectedClothes(clothes: Clothes) {
        _selectedClothesList.value =
            _selectedClothesList.value.toMutableList().apply { add(clothes) }.toList()
    }

    fun removeSelectedClothes(index: Int) {
        _selectedClothesList.value =
            _selectedClothesList.value.toMutableList().apply { removeAt(index) }.toList()
    }

    fun changeClothesType(type: ClothesType) {
        currentClothesType = type
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }
}