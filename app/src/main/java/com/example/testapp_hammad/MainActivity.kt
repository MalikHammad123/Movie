package com.example.testapp_hammad

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapp_hammad.adapter.MovieAdapter
import com.example.testapp_hammad.databinding.ActivityMainBinding
import com.example.testapp_hammad.viewmodel.MovieViewModel
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var movieViewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movieAdapter = MovieAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = movieAdapter
        }

        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
        movieViewModel.getMovies().observe(this, Observer { result ->
            when (result) {
                is MovieRepository.Result.Success -> {
                    val movies = result.data
                    movieAdapter.setMovies(movies)
                }
                is MovieRepository.Result.Error -> {
                    val errorMessage = result.message
                    // Handle error if needed
                }
            }
        })
    }
}
