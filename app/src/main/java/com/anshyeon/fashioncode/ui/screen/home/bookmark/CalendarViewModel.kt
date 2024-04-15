package com.anshyeon.fashioncode.ui.screen.home.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.anshyeon.fashioncode.data.model.LocalStyle
import com.anshyeon.fashioncode.data.repository.StyleRepository
import com.anshyeon.fashioncode.ui.graph.DetailHomeScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val styleRepository: StyleRepository,
) : ViewModel() {

    private val _appBarTitle = MutableStateFlow("")
    val appBarTitle: StateFlow<String> = _appBarTitle

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _snackBarText = MutableStateFlow("")
    val snackBarText: StateFlow<String> = _snackBarText

    private val _showSnackBar = MutableStateFlow(false)
    val showSnackBar: StateFlow<Boolean> = _showSnackBar

    val styleList = transformLocalStyleList().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private fun transformLocalStyleList(): Flow<List<LocalStyle>> {
        return styleRepository.getStyleListByRoom { }
    }

    fun onChangeAppBarTitle(newTitle: String) {
        _appBarTitle.value = newTitle
    }

    fun onChangeSelectedDate(newDate: LocalDate) {
        _selectedDate.value = newDate
    }

    fun dismissSnackBar() {
        _showSnackBar.value = false
    }

    fun navigateStyleCreate(navController: NavHostController, selectedDate: LocalDate) {
        navController.navigate("${DetailHomeScreen.StyleCreate.route}/${selectedDate}")
    }
}