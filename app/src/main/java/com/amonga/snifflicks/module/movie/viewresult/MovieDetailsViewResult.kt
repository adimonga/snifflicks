package com.amonga.snifflicks.module.movie.viewresult

import com.amonga.snifflicks.core.compose.interfaces.IViewResult
import com.amonga.snifflicks.module.network.model.MovieDetails
import com.amonga.snifflicks.module.network.model.MovieVideo

sealed interface MovieDetailsViewResult : IViewResult {
    data object Loading : MovieDetailsViewResult
    data class Success(
        val movieDetails: MovieDetails,
        val videos: List<MovieVideo> = emptyList()
    ) : MovieDetailsViewResult
    data class Error(val message: String) : MovieDetailsViewResult
} 