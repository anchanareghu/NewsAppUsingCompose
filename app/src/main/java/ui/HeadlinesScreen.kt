package ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.pullRefreshIndicatorTransform
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.newsapplicationcompose.NewsViewModel
import com.example.newsapplicationcompose.R
import com.example.newsapplicationcompose.models.HeadLines
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Headlines(
    newsList: List<HeadLines>,
    errorMessage: String?,
    onItemClick: (String) -> Unit,
    sourceLogos: Map<String, Int>,
    viewModel: NewsViewModel
) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1500)
        refreshing = false
    }

    val pullRefreshState = rememberPullRefreshState(refreshing, ::refresh)
    val rotation = animateFloatAsState(pullRefreshState.progress * 120, label = "")

    Box(
        Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        if (newsList.isNotEmpty()) {
            LazyColumn {
                if (refreshing) {
                    viewModel.getNewsHeadLines("general", null)
                }
                items(newsList) { newsItem ->
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
                    Text(
                        text = newsItem.title,
                        modifier = Modifier.padding(16.dp, 8.dp)
                    )
                    val sourceImageId = sourceLogos[newsItem.source?.name]
                    if (sourceImageId != null) {
                        Image(
                            painter = painterResource(sourceImageId),
                            contentDescription = newsItem.source?.name,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .size(80.dp, 48.dp)
                                .padding(12.dp, 0.dp)
                        )
                    } else {
                        Text(
                            text = newsItem.source?.name ?: " ",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.padding(16.dp, 8.dp)
                        )
                    }
                    Divider()
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
