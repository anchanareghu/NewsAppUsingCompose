package com.example.newsapplicationcompose

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.pullRefreshIndicatorTransform
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.newsapplicationcompose.models.sourceLogos
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.newsapplicationcompose.models.HeadLines
import com.example.newsapplicationcompose.models.NavigationItem

class MainActivity : AppCompatActivity() {
    private val apiRequestManager: ApiRequestManager by lazy {
        ApiRequestManager(applicationContext)
    }

    private val viewModel: NewsViewModel by viewModels {
        NewsViewModelFactory(apiRequestManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContent {
            NewsHomeScreen(viewModel)
        }
    }
}

class NewsViewModelFactory(private val apiRequestManager: ApiRequestManager) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(apiRequestManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsHomeScreen(viewModel: NewsViewModel) {
    val navController = rememberNavController()

    val newsList by viewModel.newsList.observeAsState(emptyList())
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    LaunchedEffect(Unit) {
        viewModel.getNewsHeadLines("general",null)
    }

    var searchText by remember { mutableStateOf("") }
    val filteredNewsList = newsList.filter {
        searchText.isBlank() || it.title.contains(searchText, ignoreCase = true)
    }

    var isSearching by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearching) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("Search News here...", fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(4.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(cursorColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(18.dp),
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                        )
                    } else {
                        Text("News", textAlign = TextAlign.Center, fontSize = 24.sp)
                    }
                },
                actions = {
                    IconButton(onClick = { isSearching = !isSearching }) {
                        Icon(
                            if (isSearching) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(R.color.lilac))
            )
        },
        bottomBar = {
            BottomNavigationBar(
                items = listOf(
                    NavigationItem("Headlines", "headlines", Icons.Default.Home),
                    NavigationItem("Categories", "categories", Icons.Default.List),
                ),
                navController = navController,
                onItemClick = { navController.navigate(it.route) }
            )
        }
    ) { innerPadding ->
        NewsNavGraph(
            navController = navController,
            newsList = filteredNewsList,
            errorMessage = errorMessage,
            modifier = Modifier.padding(innerPadding),
            onItemClick = { url -> navController.navigate("web-view/${Uri.encode(url)}") },
            viewModel = viewModel
        )
    }
}

@Composable
fun NewsNavGraph(
    navController: NavHostController,
    newsList: List<HeadLines>,
    errorMessage: String?,
    modifier: Modifier,
    onItemClick: (String) -> Unit,
    viewModel: NewsViewModel
) {
    NavHost(navController = navController, startDestination = "headlines", modifier = modifier) {
        composable(route = "headlines") {
            Headlines(
                newsList = newsList,
                errorMessage = errorMessage,
                onItemClick = onItemClick,
                sourceLogos = sourceLogos,
                viewModel = viewModel
            )
        }
        composable(route = "categories") {
            NewsCategories(onItemClick = onItemClick, viewModel = viewModel)
        }
        composable(route = "web-view/{url}") { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")
            if (url != null) {
                WebView(url)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<NavigationItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (NavigationItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    NavigationBar(modifier = modifier, containerColor = Color.DarkGray, tonalElevation = 5.dp) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(item) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.DarkGray,
                    unselectedIconColor = Color.Gray
                ),
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = item.icon, contentDescription = item.name)
                        if (selected) {
                            Text(text = item.name, textAlign = TextAlign.Center, fontSize = 10.sp)
                        }
                    }
                })
        }
    }
}

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
                    viewModel.getNewsHeadLines("general",null)
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
fun WebView(url: String) {
    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
        WebView(context).apply { loadUrl(url) }
    })
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

@Composable
fun ShowToastMessage(context: Context, message: String?) {
    message?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
}

