package com.amonga.snifflicks.module.movie.event

import com.amonga.snifflicks.core.compose.interfaces.IEvent

sealed interface MovieDetailsEvent : IEvent {
    data class LoadMovieDetails(val movieId: Int) : MovieDetailsEvent
    data object RetryLoadingMovieDetails : MovieDetailsEvent
} 