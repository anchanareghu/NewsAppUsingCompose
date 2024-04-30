package com.example.newsapplicationcompose

import android.content.Context
import models.NewsApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class ApiRequestManager(private val context: Context) {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://newsapi.org/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getNewsHeadLines(
        fetchDataListener: OnFetchDataListener,
        category: String?,
        query: String?
    ) {
        val callNewsApi = retrofit.create(CallNewsApi::class.java)
        try {
            callNewsApi.headlinesApiCall(
                "in",
                category,
                query,
                context.getString(R.string.API_KEY)
            )?.enqueue(object : Callback<NewsApi?> {
                override fun onResponse(call: Call<NewsApi?>, response: Response<NewsApi?>) {
                    if (!response.isSuccessful) {
                        fetchDataListener.onError("Request Failed!")
                        return
                    }
                    val body = response.body()
                    if (body?.articles != null) {
                        fetchDataListener.onFetchData(body.articles, response.message())
                    } else {
                        fetchDataListener.onError("No data available!")
                    }
                }

                override fun onFailure(call: Call<NewsApi?>, t: Throwable) {
                    fetchDataListener.onError("Request Failed!")
                }
            })
        } catch (e: Exception) {
            fetchDataListener.onError("Request Failed!")
        }
    }

    interface CallNewsApi {
        @GET("top-headlines")
        fun headlinesApiCall(
            @Query("country") country: String?,
            @Query("category") category: String?,
            @Query("q") query: String?,
            @Query("apiKey") apiKey: String?
        ): Call<NewsApi?>?
    }
}


