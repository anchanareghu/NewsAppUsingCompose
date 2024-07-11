package com.example.newsapplicationcompose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapplicationcompose.data.HeadLines

class NewsViewModel(
    private val apiRequestManager: ApiRequestManager, savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _newsList: MutableLiveData<List<HeadLines>> = MutableLiveData()
    val newsList: LiveData<List<HeadLines>> = _newsList

    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _selectedCategory: MutableLiveData<String> =
        savedStateHandle.getLiveData("selectedCategory", "general")
    val selectedCategory: LiveData<String> = _selectedCategory

    init {
        getNewsHeadLines(_selectedCategory.value, null)
    }

    fun getNewsHeadLines(category: String?, query: String?) {
        _isLoading.value = true
        apiRequestManager.getNewsHeadLines(category, query, object : OnFetchDataListener {
            override fun onFetchData(newsList: List<HeadLines?>?, message: String?) {
                _newsList.value = newsList?.filterNotNull() ?: emptyList()
                _errorMessage.value = message ?: "Unknown error occurred"
                _isLoading.value = false
            }

            override fun onError(message: String?) {
                _errorMessage.value = message ?: "Unknown error occurred"
                _isLoading.value = false
            }
        })
    }
}


class NewsViewModelFactory(
    private val apiRequestManager: ApiRequestManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(apiRequestManager, SavedStateHandle()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
