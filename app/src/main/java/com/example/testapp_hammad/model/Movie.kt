package com.example.testapp_hammad.model

data class SearchResponse(
    val Search: List<Movie>,
)

data class Movie(
    val Title: String,
    val Year: String,
)

