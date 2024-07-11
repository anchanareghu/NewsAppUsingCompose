package com.example.newsapplicationcompose

import com.example.newsapplicationcompose.data.HeadLines


interface OnFetchDataListener {
    fun onFetchData(newsList: List<HeadLines?>?, message: String?)
    fun onError(message: String?)
}
