package com.example.newsapplicationcompose

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import models.HeadLines

class NewsViewModel(context: Context) : ViewModel() {
    private val apiRequestManager = ApiRequestManager(context)

    val newsList: LiveData<List<HeadLines>> = MutableLiveData(emptyList())
    val errorMessage: LiveData<String?> = MutableLiveData(null)

    fun getNewsHeadLines() {
        apiRequestManager.getNewsHeadLines(object : OnFetchDataListener {
            override fun onFetchData(headLinesList: List<HeadLines?>?, message: String?) {
                (newsList as MutableLiveData).value =
                    (headLinesList ?: emptyList()) as List<HeadLines>
            }

            override fun onError(message: String?) {
                (errorMessage as MutableLiveData).value = message ?: "Unknown error occurred"
            }
        }, "general", null)
    }
}