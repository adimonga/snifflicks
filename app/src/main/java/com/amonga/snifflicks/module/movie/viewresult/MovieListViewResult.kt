package com.amonga.snifflicks.module.movie.viewresult

import androidx.paging.PagingData
import com.amonga.snifflicks.core.compose.interfaces.IViewResult
import com.amonga.snifflicks.module.network.model.Movie
import kotlinx.coroutines.flow.Flow

sealed interface MovieListViewResult : IViewResult {
    data class UpdatePopularMovies(val movies: Flow<PagingData<Movie>>) : MovieListViewResult
    data class UpdateUpcomingMovies(val movies: Flow<PagingData<Movie>>) : MovieListViewResult
    data class UpdateNowPlayingMovies(val movies: Flow<PagingData<Movie>>) : MovieListViewResult
} 