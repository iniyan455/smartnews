package iniyan.com.smartnews.Interface

import iniyan.com.smartnews.Model.News
import iniyan.com.smartnews.Model.websitebean
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface NewsService {

    //https://newsapi.org/v2/sources?apiKey=00320d4b86af4cb9bf0e432fd6f063e3

    @get:GET("v2/sources?apiKey=00320d4b86af4cb9bf0e432fd6f063e3")

    val sources: Call<websitebean>


    @GET
    fun getNewsFromSource(@Url url: String): Call<News>
}