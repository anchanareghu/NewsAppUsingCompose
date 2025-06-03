package com.example.newsapplicationcompose

import android.content.Context
import com.example.newsapplicationcompose.data.NewsApi
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
        category: String?,
        query: String?,
        callback: OnFetchDataListener
    ) {
        val callNewsApi = retrofit.create(CallNewsApi::class.java)
        callNewsApi.headlinesApiCall(
//            "us",
            category,
            query,
            context.getString(R.string.API_KEY)
        )?.enqueue(object : Callback<NewsApi?> {
            override fun onResponse(call: Call<NewsApi?>, response: Response<NewsApi?>) {
                if (!response.isSuccessful) {
                    callback.onFetchData(null, "Request Failed!")
                    return
                }
                val body = response.body()
                if (body?.articles != null) {
                    callback.onFetchData(body.articles, response.message())
                } else {
                    callback.onFetchData(null, "No data available!")
                }
            }

            override fun onFailure(call: Call<NewsApi?>, t: Throwable) {
                callback.onError("Request Failed!")
            }
        })
    }

    interface CallNewsApi {
        @GET("top-headlines")
        fun headlinesApiCall(
//            @Query("country") country: String?,
            @Query("category") category: String?,
            @Query("q") query: String?,
            @Query("apiKey") apiKey: String?
        ): Call<NewsApi?>?
    }
}
