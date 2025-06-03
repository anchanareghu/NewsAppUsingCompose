package com.example.newsapplicationcompose.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.newsapplicationcompose.NewsViewModel
import com.example.newsapplicationcompose.R
import com.example.newsapplicationcompose.data.HeadLines
import com.example.newsapplicationcompose.ui.Headlines
import com.example.newsapplicationcompose.ui.NewsCategories
import com.example.newsapplicationcompose.ui.WebView

@Composable
fun NewsNavGraph(
    navController: NavHostController,
    newsList: List<HeadLines>,
    errorMessage: String?,
    modifier: Modifier,
    onItemClick: (String) -> Unit,
) {
    val viewModel: NewsViewModel = viewModel()
    NavHost(navController = navController, startDestination = "headlines", modifier = modifier) {
        composable(route = "headlines") {
            Headlines(
                newsList = newsList,
                errorMessage = errorMessage,
                onItemClick = onItemClick,
                onRefresh = {
                    viewModel.getNewsHeadLines(viewModel.selectedCategory.value, null)
                }
            )
        }
        composable(route = "categories") {
            NewsCategories(onItemClick = onItemClick)
        }
        composable(
            route = "web-view/{url}",
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
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
    NavigationBar(modifier = modifier, containerColor = Color.Gray, tonalElevation = 5.dp) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(item) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = colorResource(id = R.color.lilac),
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.White
                ),
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = item.icon, contentDescription = item.name)
                        if (selected) {
                            Text(
                                text = item.name,
                                textAlign = TextAlign.Center,
                                style = LocalTextStyle.current.copy(fontSize = 8.sp)
                            )
                        }
                    }
                }
            )
        }
    }
}