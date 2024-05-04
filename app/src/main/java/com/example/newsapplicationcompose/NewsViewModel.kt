package com.example.newsapplicationcompose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsapplicationcompose.models.HeadLines

class NewsViewModel(private val apiRequestManager: ApiRequestManager) : ViewModel() {
    private val _newsList: MutableLiveData<List<HeadLines>> = MutableLiveData(emptyList())
    val newsList: LiveData<List<HeadLines>> = _newsList

    private val _errorMessage: MutableLiveData<String?> = MutableLiveData(null)
    val errorMessage: LiveData<String?> = _errorMessage

    fun getNewsHeadLines(category: String?, query: String?) {
        apiRequestManager.getNewsHeadLines(category, query, object : OnFetchDataListener {

            override fun onFetchData(headLinesList: List<HeadLines?>?, message: String?) {
                _newsList.value = headLinesList?.filterNotNull() ?: emptyList()
                _errorMessage.value = message ?: "Unknown error occurred"
            }

            override fun onError(message: String?) {
                _errorMessage.value = message ?: "Unknown error occurred"
            }
        })
    }
}
