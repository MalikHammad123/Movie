package com.example.testapp_hammad

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.testapp_hammad.interfaces.MovieApi
import com.example.testapp_hammad.model.Movie
import com.example.testapp_hammad.model.SearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

    class MovieRepository {
        private val movieApi: MovieApi

        init {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.omdbapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            movieApi = retrofit.create(MovieApi::class.java)
        }

        fun getMovies(): LiveData<Result<List<Movie>>> {
            val data = MutableLiveData<Result<List<Movie>>>()

            val call = movieApi.getMovies()
            Log.d("TAG", "Request URL: ${call.request().url()}")

            call.enqueue(object : Callback<SearchResponse> {
                override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                    if (response.isSuccessful) {
                        data.value = Result.Success(response.body()?.Search ?: emptyList())
                    } else {
                        Log.d("TAG", "Unsuccessful response: ${response.code()}")
                        data.value = Result.Error("Unsuccessful response: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    Log.e("TAG", "Network error: ${t.message}")
                    data.value = Result.Error("Network error: ${t.message}")
                }
            })

            return data
        }        sealed class Result<out T> {
            data class Success<out T>(val data: T) : Result<T>()
            data class Error(val message: String) : Result<Nothing>()
        }

    }
