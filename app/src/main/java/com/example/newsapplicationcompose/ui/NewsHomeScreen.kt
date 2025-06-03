package com.example.newsapplicationcompose.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.newsapplicationcompose.NewsViewModel
import com.example.newsapplicationcompose.R
import com.example.newsapplicationcompose.ui.navigation.BottomNavigationBar
import com.example.newsapplicationcompose.ui.navigation.NavigationItem
import com.example.newsapplicationcompose.ui.navigation.NewsNavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsHomeScreen() {
    val navController = rememberNavController()
    val viewModel: NewsViewModel = viewModel()

    val newsList by viewModel.newsList.observeAsState(emptyList())
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchText by rememberSaveable { mutableStateOf("") }

    val filteredNewsList =
        if (isSearching && searchText.isNotEmpty()) {
            newsList.filter { it.title.contains(searchText, ignoreCase = true) }
        } else {
            newsList
        }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            if (currentDestination != "categories") {
                TopAppBar(
                    title = {
                        if (isSearching) {
                            TextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                modifier = Modifier
                                    .fillMaxWidth().padding(4.dp),
                                placeholder = {
                                    Text(
                                        text = "Search for news with keywords here...",
                                        color = Color.DarkGray,
                                        fontSize = 12.sp
                                    )
                                },
                            )
                        } else {
                            Text(
                                text = "NewsDaily",
                                color = Color.DarkGray,
                                style = LocalTextStyle.current.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            isSearching = !isSearching
                            searchText = ""
                        }) {
                            Icon(
                                imageVector = if (isSearching) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "search_icon",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(R.color.lilac))
                )
            }
        },
        bottomBar = {
            if (currentDestination != "web-view/{url}") {
                BottomNavigationBar(
                    items = listOf(
                        NavigationItem("Headlines", "headlines", Icons.Default.Home),
                        NavigationItem("Categories", "categories", Icons.Default.List),
                    ),
                    navController = navController,
                    onItemClick = { navController.navigate(it.route) }
                )
            }
        }
    ) { innerPadding ->
        NewsNavGraph(
            navController = navController,
            newsList = filteredNewsList,
            errorMessage = errorMessage,
            modifier = Modifier.padding(innerPadding),
            onItemClick = { url ->
                navController.navigate("web-view/${Uri.encode(url)}")
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NewsHomeScreenPreview() {
    NewsHomeScreen()
}