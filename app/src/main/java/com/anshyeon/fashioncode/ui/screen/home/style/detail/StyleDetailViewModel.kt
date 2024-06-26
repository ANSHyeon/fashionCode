package com.anshyeon.fashioncode.ui.screen.home.style.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.Style
import com.anshyeon.fashioncode.data.repository.AuthRepository
import com.anshyeon.fashioncode.data.repository.StyleRepository
import com.anshyeon.fashioncode.ui.graph.BottomNavItem
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
class StyleDetailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val styleRepository: StyleRepository
) : ViewModel() {

    private val _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId

    private val _styleList = MutableStateFlow<List<Style>>(emptyList())
    var styleList: StateFlow<List<Style>> = _styleList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isGetStyleListLoading = MutableStateFlow(false)
    val isGetStyleListLoading: StateFlow<Boolean> = _isGetStyleListLoading

    private val _isGetStyleListComplete = MutableStateFlow(false)
    val isGetStyleListComplete: StateFlow<Boolean> = _isGetStyleListComplete

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    init {
        _userId.value = authRepository.getUserId()
    }

    fun getStyleList(userId: String) {
        _isGetStyleListLoading.value = true
        viewModelScope.launch {
            transformReplyList(userId)
                .map {
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
                }
                .onCompletion {
                    _isGetStyleListLoading.value = false
                    _isGetStyleListComplete.value = true
                }.collectLatest {
                    _styleList.value = it
                }
        }
    }

    private fun transformReplyList(userId: String): Flow<List<Style>> {
        return styleRepository.getStyleListWithWriter(
            userId,
            onComplete = { },
            onError = {
                _showSnackBar.value = true
                _snackBarText.value = "잠시 후 다시 시도해 주십시오"
            }
        ).map {
            it.sortedBy { reply -> reply.createdDate }
        }
    }

    fun createLike(styleId: String) {
        viewModelScope.launch {
            styleRepository.createLike(
                styleId
            )
        }
    }

    fun deleteLike(styleId: String) {
        viewModelScope.launch {
            styleRepository.deleteLike(
                styleId
            )
        }
    }

    fun setStyleLike(styleId: String, isCheck: Boolean, count: Int) {
        val tempList = _styleList.value.toMutableList()
        tempList.forEachIndexed { index, style ->
            if (style.styleId == styleId) {
                tempList[index] = tempList[index].copy(
                    isLike = isCheck,
                    likeCount = count
                )
                _styleList.value = tempList.toList()
            }
        }
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }

    fun navigateOtherStyleDetail(
        navController: NavHostController,
        style: Style,
    ) {
        val styleJson = SerializationUtils.toJson(style)
        val encodedStyleUrl = URLEncoder.encode(styleJson, StandardCharsets.UTF_8.toString())
        navController.navigate("${DetailHomeScreen.StyleDetail.route}/${encodedStyleUrl}") {
            popUpTo(BottomNavItem.Style.route)
        }
    }

    fun navigateOtherUserProfile(navController: NavHostController, userId: String?) {
        navController.navigate("${DetailHomeScreen.OtherProfile.route}/${userId}")
    }
}