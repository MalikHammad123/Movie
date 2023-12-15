package com.example.testapp_hammad.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.testapp_hammad.MovieRepository
import com.example.testapp_hammad.model.Movie


class MovieViewModel : ViewModel() {
    private val movieRepository = MovieRepository()
    fun getMovies(): LiveData<MovieRepository.Result<List<Movie>>> {
        return movieRepository.getMovies()
    }
}

