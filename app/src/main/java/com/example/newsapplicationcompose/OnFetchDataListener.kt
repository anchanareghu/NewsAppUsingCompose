package com.example.newsapplicationcompose

import models.HeadLines

interface OnFetchDataListener {
    fun onFetchData(headLinesList: List<HeadLines?>?, message: String?)
    fun onError(message: String?)
}
