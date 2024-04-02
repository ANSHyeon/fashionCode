package com.anshyeon.fashioncode.ui.screen.home.style.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Style
import com.anshyeon.fashioncode.data.repository.AuthRepository
import com.anshyeon.fashioncode.data.repository.StyleRepository
import com.anshyeon.fashioncode.ui.graph.DetailHomeScreen
import com.anshyeon.fashioncode.util.SerializationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class StyleViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val styleRepository: StyleRepository,
) : ViewModel() {

    private val _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId

    private val _isGetStyleListLoading = MutableStateFlow(true)
    val isGetStyleListLoading: StateFlow<Boolean> = _isGetStyleListLoading

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    init {
        _userId.value = authRepository.getUserId()
    }

    val styleList = transformStyleList().map {
        val tempLikeList = MutableStateFlow<List<String>>(emptyList())
        it.map { style ->
            tempLikeList.value = emptyList()
            val response = styleRepository.getStyleLikeList(
                style.styleId,
                {},
                {}
            )
            response.collectLatest { likeList ->
                tempLikeList.value = likeList
            }
            style.copy(
                likeList = tempLikeList.value
            )
        }
    }.onCompletion {
        _isGetStyleListLoading.value = false
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(500),
        initialValue = emptyList()
    )

    private fun transformStyleList(): Flow<List<Style>> {
        return styleRepository.getStyleList(
            onComplete = { },
            onError = {
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }
        ).map {
            it.sortedByDescending { style -> style.createdDate }
        }
    }

    fun createLike(styleId: String) {
        viewModelScope.launch {
            styleRepository.createLike(styleId)
        }
    }

    fun deleteLike(styleId: String) {
        viewModelScope.launch {
            styleRepository.deleteLike(styleId)
        }
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateStyleCreate(navController: NavHostController) {
        navController.navigate(DetailHomeScreen.StyleCreate.route)
    }

    fun navigateStyleDetail(
        navController: NavHostController,
        style: Style,
    ) {
        val styleJson = SerializationUtils.toJson(style)
        val encodedStyleUrl = URLEncoder.encode(styleJson, StandardCharsets.UTF_8.toString())
        navController.navigate("${DetailHomeScreen.StyleDetail.route}/${encodedStyleUrl}")
    }
}