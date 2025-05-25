package com.amonga.snifflicks.module.movie.viewstate

import com.amonga.snifflicks.core.compose.interfaces.IViewState
import com.amonga.snifflicks.module.network.model.MovieDetails
import com.amonga.snifflicks.module.network.model.MovieVideo

data class MovieDetailsViewState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val movieDetails: MovieDetails? = null,
    val videos: List<MovieVideo> = emptyList()
) : IViewState {
    companion object {
        val INITIAL = MovieDetailsViewState()
    }
} 