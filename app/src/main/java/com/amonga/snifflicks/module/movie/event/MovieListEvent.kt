package com.amonga.snifflicks.module.movie.event

import com.amonga.snifflicks.core.compose.interfaces.IEvent

sealed interface MovieListEvent : IEvent {
    data object LoadPopularMovies : MovieListEvent
    data object LoadUpcomingMovies : MovieListEvent
    data object LoadNowPlayingMovies : MovieListEvent
    data class OnMovieClick(val movieId: Int) : MovieListEvent
} 