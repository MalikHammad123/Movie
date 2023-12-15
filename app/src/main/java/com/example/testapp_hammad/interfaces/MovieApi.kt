package com.example.testapp_hammad.interfaces

import com.example.testapp_hammad.model.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
interface MovieApi {
    @GET("?s=love&type=movie&y=2000&apikey=54f8a7a0")
    fun getMovies(): Call<SearchResponse>
}
