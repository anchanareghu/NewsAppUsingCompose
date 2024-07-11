package com.example.newsapplicationcompose.ui

import com.example.newsapplicationcompose.data.HeadLines

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newsapplicationcompose.ApiRequestManager
import com.example.newsapplicationcompose.NewsViewModel
import com.example.newsapplicationcompose.OnFetchDataListener
import com.example.newsapplicationcompose.R

@Composable
fun NewsCategories(
    onItemClick: (String) -> Unit,
) {
    var selectedCategory by rememberSaveable { mutableStateOf("Business") }
    val (newsList, setNewsList) = remember { mutableStateOf<List<HeadLines?>?>(null) }
    val (errorMessage, setErrorMessage) = remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val categories = listOf(
        "Business",
        "Entertainment",
        "Health",
        "Science",
        "Sports"
    )

    val listener = remember {
        object : OnFetchDataListener {
            override fun onFetchData(newsList: List<HeadLines?>?, message: String?) {
                setNewsList(newsList)
                setErrorMessage(message)
                isLoading = false
            }

            override fun onError(message: String?) {
                setErrorMessage(message)
                isLoading = false
            }
        }
    }

    val apiRequestManager = ApiRequestManager(LocalContext.current)

    LaunchedEffect(selectedCategory) {
        isLoading = true
        apiRequestManager.getNewsHeadLines(selectedCategory, null, listener)
    }
    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_category_24),
                        contentDescription = "Category Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(categories.size) { categoryIndex ->
                            val category = categories[categoryIndex]
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (selectedCategory.equals(
                                                category,
                                                ignoreCase = true
                                            )
                                        )
                                            MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                        else
                                            Color.Transparent,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable {
                                        selectedCategory = category
                                        isLoading = true
                                        apiRequestManager.getNewsHeadLines(category, null, listener)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 12.sp,
                                    color = if (
                                        selectedCategory.equals(
                                            category,
                                            ignoreCase = true
                                        )
                                    )
                                        Color.DarkGray
                                    else
                                        Color.Gray,
                                    fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                if (isLoading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                } else {
                    if (errorMessage?.isNotEmpty() == true) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.failedtoload),
                                contentDescription = errorMessage,
                                modifier = Modifier.size(400.dp, 200.dp)
                            )
                            ShowToastMessage(
                                LocalContext.current,
                                message = errorMessage
                            )
                        }
                    } else {
                        newsList?.let { list ->
                            Headlines(
                                newsList = list.filterNotNull(),
                                errorMessage = errorMessage,
                                onItemClick = onItemClick,
                                onRefresh = {
                                    isLoading = true
                                    apiRequestManager.getNewsHeadLines(
                                        selectedCategory,
                                        null,
                                        listener
                                    )
                                }
                            )
                        }
                    }
                }
            }
        })
}


@Preview
@Composable
fun NewsCategoriesPreview() {
    NewsCategories(onItemClick = {})
}
