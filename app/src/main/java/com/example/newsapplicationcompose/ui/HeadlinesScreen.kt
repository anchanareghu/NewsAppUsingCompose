package com.example.newsapplicationcompose.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.pullRefreshIndicatorTransform
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.newsapplicationcompose.R
import com.example.newsapplicationcompose.data.HeadLines
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Headlines(
    newsList: List<HeadLines>,
    errorMessage: String?,
    onItemClick: (String) -> Unit,
    onRefresh: () -> Unit
) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1500)
        onRefresh()
        refreshing = false
    }

    val pullRefreshState = rememberPullRefreshState(refreshing, ::refresh)
    val rotation = animateFloatAsState(pullRefreshState.progress * 120, label = "")

    Box(
        Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.lilac))
            .pullRefresh(pullRefreshState)
    ) {
        if (newsList.isNotEmpty()) {
            LazyColumn {
                items(newsList) { newsItem ->
                    ElevatedCard(
                        modifier = Modifier.padding(16.dp, 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onItemClick(newsItem.url) },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                newsItem.urlToImage,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            newsItem.source?.let { source ->
                                Text(
                                    text = source.name.uppercase(Locale.getDefault()),
                                    color = colorResource(id = R.color.lilac02),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(16.dp, 8.dp),
                                    style = LocalTextStyle.current.copy(fontSize = 12.sp)
                                )
                            }

                            val formattedDate = newsItem.getFormattedDate()

                            Text(
                                text = formattedDate,
                                color = colorResource(id = R.color.lilac02),
                                fontSize = 10.sp,
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier.padding(16.dp, 8.dp),
                                style = LocalTextStyle.current.copy(fontSize = 12.sp)
                            )
                        }
                        Text(
                            text = newsItem.title,
                            modifier = Modifier.padding(16.dp, 8.dp),
                            style = LocalTextStyle.current.copy(fontSize = 16.sp)
                        )

                    }
                }
            }
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopCenter)
                    .pullRefreshIndicatorTransform(pullRefreshState)
                    .rotate(rotation.value),
                shape = RoundedCornerShape(10.dp),
                color = Color.DarkGray,
                elevation = if (pullRefreshState.progress > 0 || refreshing) 20.dp else 0.dp,
            ) {
                Box {
                    if (refreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(25.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    }
                }
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(400.dp, 200.dp)
                        .align(Alignment.Center),
                    painter = painterResource(R.drawable.failedtoload),
                    contentDescription = errorMessage,
                )
                ShowToastMessage(LocalContext.current, message = errorMessage)
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeadlinesPreview() {
    Headlines(
        newsList = listOf(
            HeadLines(),
            HeadLines()
        ),
        errorMessage = null,
        onItemClick = { url -> /* Handle item click */ },
        onRefresh = { /* Handle refresh action */ }
    )
}
