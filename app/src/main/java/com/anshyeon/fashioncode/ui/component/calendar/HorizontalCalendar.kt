package com.anshyeon.fashioncode.ui.component.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anshyeon.fashioncode.data.model.LocalStyle
import com.anshyeon.fashioncode.ui.theme.DarkGray
import com.anshyeon.fashioncode.ui.theme.Gray
import com.anshyeon.fashioncode.ui.theme.Orange
import com.anshyeon.fashioncode.util.DateFormatText
import com.anshyeon.fashioncode.util.conditional
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalCalendar(
    modifier: Modifier = Modifier,
    styleListState: List<LocalStyle>,
    selectedDate: LocalDate,
    onChangeAppBarTitle: (String) -> Unit,
    onChangeSelectedDate: (LocalDate) -> Unit
) {
    val configuration = LocalConfiguration.current
    val gridHeight = configuration.screenHeightDp.dp - 132.dp

    val initialPage = (selectedDate.year - 2020) * 12 + selectedDate.monthValue - 1
    var currentMonth by remember { mutableStateOf(selectedDate) }
    var currentPage by remember { mutableStateOf(initialPage) }
    val pagerState = rememberPagerState(initialPage = initialPage) { 10 * 12 }

    LaunchedEffect(pagerState.currentPage) {
        val addMonth = (pagerState.currentPage - currentPage).toLong()
        currentMonth = currentMonth.plusMonths(addMonth)
        currentPage = pagerState.currentPage
        onChangeAppBarTitle(DateFormatText.getCalendarFormat(currentMonth))
    }

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState
        ) { page ->
            val date = LocalDate.of(
                2020 + page / 12,
                page % 12 + 1,
                1
            )
            if (page in pagerState.currentPage - 1..pagerState.currentPage + 1) {
                CalendarMonthItem(
                    modifier = Modifier
                        .fillMaxWidth(),
                    styleListState = styleListState,
                    gridHeight = gridHeight,
                    currentDate = date,
                    selectedDate = selectedDate,
                    onSelectedDate = { onChangeSelectedDate(it) }
                )
            }
        }
    }
}

@Composable
fun CalendarMonthItem(
    modifier: Modifier = Modifier,
    styleListState: List<LocalStyle>,
    gridHeight: Dp,
    currentDate: LocalDate,
    selectedDate: LocalDate,
    onSelectedDate: (LocalDate) -> Unit
) {
    val lastDay by remember { mutableStateOf(currentDate.lengthOfMonth()) }
    val firstDayOfWeek by remember { mutableStateOf(currentDate.dayOfWeek.value) }
    val days by remember { mutableStateOf(IntRange(1, lastDay).toList()) }
    val weekNumber = when (firstDayOfWeek + days.size) {
        in 0..29 -> 4
        in 28..36 -> 5
        else -> 6
    }
    val dayHeight = gridHeight / weekNumber
    Column(modifier = modifier) {
        DayOfWeek()

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            columns = GridCells.Fixed(7),
            userScrollEnabled = false
        ) {
            for (i in 1 until firstDayOfWeek) {
                item {
                    Box(
                        modifier = Modifier.border(1.dp, Gray)
                    )
                }
            }
            items(days) { day ->
                val date = currentDate.withDayOfMonth(day)
                val styleByDate = styleListState.filter { it.date == date.toString() }
                val isSelected = remember(selectedDate) {
                    selectedDate.compareTo(date) == 0
                }
                CalendarDay(
                    Modifier.height(dayHeight),
                    styleByDate,
                    date = date,
                    isToday = date == LocalDate.now(),
                    isSelected = isSelected,
                    onSelectedDate = onSelectedDate
                )
            }
        }
    }
}

@Composable
fun CalendarDay(
    modifier: Modifier = Modifier,
    styleByDate: List<LocalStyle>,
    date: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    onSelectedDate: (LocalDate) -> Unit
) {
    Column(
        modifier = modifier
            .conditional(isSelected) {
                border(2.dp, DarkGray)
            }
            .conditional(isToday) {
                border(2.dp, Orange)
            }
            .conditional(!isToday && !isSelected) {
                border(0.5.dp, Gray)
            }
            .clickable { onSelectedDate(date) }

    ) {
        Text(
            modifier = Modifier.padding(6.dp),
            text = date.dayOfMonth.toString(),
            fontSize = 12.sp,
        )
        if (styleByDate.isNotEmpty()) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = styleByDate.last().image,
                contentDescription = null
            )
        }
    }
}

@Composable
fun DayOfWeek(
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        DayOfWeek.values().forEach { dayOfWeek ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(20.dp),
                text = dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.KOREAN),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = if (dayOfWeek == DayOfWeek.SUNDAY) Color.Red else Color.Black
            )
        }
    }
}