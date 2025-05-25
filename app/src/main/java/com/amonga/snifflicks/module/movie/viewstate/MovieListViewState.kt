package com.amonga.snifflicks.module.movie.viewstate

import androidx.paging.PagingData
import com.amonga.snifflicks.core.compose.interfaces.IViewState
import com.amonga.snifflicks.module.network.model.Movie
import kotlinx.coroutines.flow.Flow

data class MovieListViewState(
    val popularMoviesPagingData: Flow<PagingData<Movie>>? = null,
    val upcomingMoviesPagingData: Flow<PagingData<Movie>>? = null,
    val nowPlayingMoviesPagingData: Flow<PagingData<Movie>>? = null
) : IViewState {
    companion object {
        val INITIAL = MovieListViewState()
    }
} 