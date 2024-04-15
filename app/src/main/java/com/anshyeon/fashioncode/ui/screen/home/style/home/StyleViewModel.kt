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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
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

    private val _styleList = MutableStateFlow<List<Style>>(emptyList())
    val styleList: StateFlow<List<Style>> = _styleList

    private val _isGetStyleListLoading = MutableStateFlow(true)
    val isGetStyleListLoading: StateFlow<Boolean> = _isGetStyleListLoading

    private val _isNavigate = MutableStateFlow(false)
    val isNavigate: StateFlow<Boolean> = _isNavigate

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    init {
        _userId.value = authRepository.getUserId()
        getStyleList()
    }

    fun getStyleList() {
        viewModelScope.launch {
            transformStyleList().map {
                _isGetStyleListLoading.value = true
                val styleListWithLikes = viewModelScope.async {
                    it.map { style ->
                        viewModelScope.async {
                            val tempLikeList = mutableListOf<String>()
                            val response = styleRepository.getStyleLikeList(
                                style.styleId,
                                {},
                                {}
                            )
                            response.collectLatest { likeList ->
                                tempLikeList.addAll(likeList)
                            }
                            style.copy(
                                isLike = tempLikeList.any { it == _userId.value },
                                likeCount = tempLikeList.size,
                                likeList = tempLikeList.toList()
                            )
                        }
                    }
                }
                styleListWithLikes.await().map { it.await() }
            }.onCompletion {
                _isGetStyleListLoading.value = false
            }.collectLatest {
                _styleList.value = it
            }
        }
    }


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

    fun setStyleLike(index: Int, isCheck: Boolean, count: Int) {
        val tempList = _styleList.value.toMutableList()
        tempList[index] = tempList[index].copy(
            isLike = isCheck,
            likeCount = count
        )
        _styleList.value = tempList.toList()
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun clearIsNavigate() {
        _isNavigate.value = false
    }

    fun navigateStyleCreate(navController: NavHostController) {
        _isNavigate.value = true
        navController.navigate("${DetailHomeScreen.StyleCreate.route}/${""}")
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