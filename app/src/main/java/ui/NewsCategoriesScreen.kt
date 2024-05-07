package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.newsapplicationcompose.ApiRequestManager
import com.example.newsapplicationcompose.NewsViewModel
import com.example.newsapplicationcompose.OnFetchDataListener
import com.example.newsapplicationcompose.R
import com.example.newsapplicationcompose.models.HeadLines
import com.example.newsapplicationcompose.models.sourceLogos

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
            }

            override fun onError(message: String?) {
                setErrorMessage(message)
            }
        }
    }
    val apiRequestManager = ApiRequestManager(LocalContext.current)

    LaunchedEffect(selectedCategory.value) {
        isLoading = true
        apiRequestManager.getNewsHeadLines(selectedCategory.value, null, listener)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopBar(
                items = listOf(
                    "General",
                    "Business",
                    "Entertainment",
                    "Health",
                    "Science",
                    "Sports"
                ),
                selectedItem = selectedCategory.value,
                onItemSelected = { category -> selectedCategory.value = category })
        },
        content = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    errorMessage?.let {
                        if (it.isNotEmpty()) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.failedtoload),
                                    contentDescription = errorMessage,
                                    modifier = Modifier.size(400.dp, 200.dp)
                                )
                                ShowToastMessage(LocalContext.current, message = errorMessage)
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



@Composable
fun TopBar(items: List<String>, selectedItem: String, onItemSelected: (String) -> Unit) {
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        items.forEach { item ->
            TopBarItem(
                text = item,
                isSelected = item == selectedItem,
                onClick = { onItemSelected(item) },
                selectedColor = R.color.lilac02
            )
        }
    }
}

@Composable
fun TopBarItem(text: String, isSelected: Boolean, onClick: () -> Unit, selectedColor: Int) {
    Box(modifier = Modifier.clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            modifier = Modifier
                .padding(16.dp, 8.dp)
                .then(
                    if (isSelected) {
                        Modifier.drawWithContent {
                            drawContent()
                            drawLine(
                                color = Color(selectedColor),
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 8f
                            )
                        }
                    } else {
                        Modifier
                    }
                ),
            color = if (isSelected) Color(selectedColor) else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}