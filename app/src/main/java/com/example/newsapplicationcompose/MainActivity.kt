package com.example.newsapplicationcompose
import android.app.Application
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapplicationcompose.ui.NewsHomeScreen
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val apiRequestManager = ApiRequestManager(applicationContext)
        viewModel = ViewModelProvider(this, NewsViewModelFactory(apiRequestManager))
            .get(NewsViewModel::class.java)

        setContent {
            NewsHomeScreen()
        }
    }
}