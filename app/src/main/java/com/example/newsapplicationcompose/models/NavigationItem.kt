package com.example.newsapplicationcompose.models

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)
