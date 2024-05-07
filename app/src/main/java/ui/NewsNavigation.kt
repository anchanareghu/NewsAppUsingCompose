package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.newsapplicationcompose.NewsViewModel
import com.example.newsapplicationcompose.models.HeadLines
import com.example.newsapplicationcompose.models.NavigationItem
import com.example.newsapplicationcompose.models.sourceLogos

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
                }
            )
        }
    }
}
