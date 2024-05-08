package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newsapplicationcompose.ApiRequestManager
import com.example.newsapplicationcompose.NewsViewModel
import com.example.newsapplicationcompose.OnFetchDataListener
import com.example.newsapplicationcompose.R
import com.example.newsapplicationcompose.models.HeadLines

@Composable
fun NewsCategories(
    onItemClick: (String) -> Unit,
    viewModel: NewsViewModel
) {
    val selectedCategory = remember { mutableStateOf("Business") }
    val (newsList, setNewsList) = remember { mutableStateOf<List<HeadLines?>?>(null) }
    val (errorMessage, setErrorMessage) = remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

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

    LaunchedEffect(selectedCategory.value) {
        isLoading = true
        apiRequestManager.getNewsHeadLines(selectedCategory.value, null, listener)
    }

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
            ) {
                TabRow(
                    selectedTabIndex = listOf(
                        "Business",
                        "Entertainment",
                        "Health",
                        "Science",
                        "Sports"
                    ).indexOf(selectedCategory.value),
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.onBackground,
                ) {
                    listOf(
                        "Business",
                        "Entertainment",
                        "Health",
                        "Science",
                        "Sports"
                    ).forEach { category ->
                        Tab(
                            selected = selectedCategory.value == category,
                            onClick = {
                                selectedCategory.value = category
                                isLoading = true
                                apiRequestManager.getNewsHeadLines(category, null, listener)
                            },
                            text = {
                                Text(
                                    text = category,
                                    fontSize = 8.sp
                                )
                            },
                        )
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
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
                        }
                    }
                    newsList?.let { list ->
                        Headlines(
                            newsList = list.filterNotNull(),
                            errorMessage = errorMessage,
                            onItemClick = onItemClick,
                            sourceLogos = sourceLogos,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    )
}
