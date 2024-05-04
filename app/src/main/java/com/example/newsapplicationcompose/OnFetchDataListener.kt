package com.example.newsapplicationcompose

import com.example.newsapplicationcompose.models.HeadLines

interface OnFetchDataListener {
    fun onFetchData(headLinesList: List<HeadLines?>?, message: String?)
    fun onError(message: String?)
}
