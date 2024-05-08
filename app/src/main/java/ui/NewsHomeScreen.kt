package ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.newsapplicationcompose.NewsViewModel
import com.example.newsapplicationcompose.R
import com.example.newsapplicationcompose.models.NavigationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsHomeScreen(viewModel: NewsViewModel) {
    val navController = rememberNavController()

    val newsList by viewModel.newsList.observeAsState(emptyList())
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    LaunchedEffect(Unit) {
        viewModel.getNewsHeadLines("general", null)
    }

    var searchText by remember { mutableStateOf("") }
    val filteredNewsList = newsList.filter {
        searchText.isBlank() || it.title.contains(searchText, ignoreCase = true)
    }

    var isSearching by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (isSearching) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("Search News here...") },
                            modifier = Modifier
                                .fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(cursorColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(18.dp),
                            textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
                        )
                    } else {
                        Text(
                            text = "NewsDaily",
                            style = LocalTextStyle.current.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(R.mipmap.ic_launcher_foreground),
                        contentDescription = "app_logo",
                        Modifier
                            .fillMaxHeight().width(54.dp)
                    )
                },
                actions = {
                    IconButton(onClick = {
                        isSearching = !isSearching
                        if (!isSearching) {
                            searchText = ""
                        }
                    }) {
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
