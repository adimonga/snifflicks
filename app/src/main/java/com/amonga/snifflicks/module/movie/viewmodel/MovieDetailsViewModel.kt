package com.amonga.snifflicks.module.movie.viewmodel

import com.amonga.snifflicks.R
import com.amonga.snifflicks.core.compose.viewmodel.BaseViewModel
import com.amonga.snifflicks.core.util.ResourceUtils
import com.amonga.snifflicks.module.movie.event.MovieDetailsEvent
import com.amonga.snifflicks.module.movie.viewresult.MovieDetailsViewResult
import com.amonga.snifflicks.module.movie.viewstate.MovieDetailsViewState
import com.amonga.snifflicks.module.network.service.MovieService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val movieService: MovieService
) : BaseViewModel<MovieDetailsEvent, MovieDetailsViewResult, MovieDetailsViewState>(MovieDetailsViewState.INITIAL) {

    private var currentMovieId: Int? = null

    override suspend fun HandleEventScope.handleEvent(event: MovieDetailsEvent) {
        when (event) {
            is MovieDetailsEvent.LoadMovieDetails -> {
                currentMovieId = event.movieId
                loadMovieDetailsAndVideos(event.movieId)
            }
            MovieDetailsEvent.RetryLoadingMovieDetails -> {
                currentMovieId?.let { loadMovieDetailsAndVideos(it) }
            }
        }
    }

    private suspend fun HandleEventScope.loadMovieDetailsAndVideos(movieId: Int) {
        MovieDetailsViewResult.Loading.reduceToState()

        try {
            coroutineScope {
                val detailsDeferred = async { movieService.getMovieDetails(movieId) }
                val videosDeferred = async { movieService.getMovieVideos(movieId) }

                val details = detailsDeferred.await()
                val videos = videosDeferred.await()

                MovieDetailsViewResult.Success(
                    movieDetails = details,
                    videos = videos.results.filter { it.site == "YouTube" }
                ).reduceToState()
            }
        } catch (e: Exception) {
            MovieDetailsViewResult.Error(
                e.message ?: ResourceUtils.getString(R.string.failed_to_load_movie_details)
            ).reduceToState()
        }
    }

    override fun MovieDetailsViewResult.reduce(oldState: MovieDetailsViewState): MovieDetailsViewState {
        return when (this) {
            is MovieDetailsViewResult.Loading -> {
                oldState.copy(
                    isLoading = true,
                    error = null
                )
            }
            is MovieDetailsViewResult.Success -> {
                oldState.copy(
                    isLoading = false,
                    error = null,
                    movieDetails = movieDetails,
                    videos = videos
                )
            }
            is MovieDetailsViewResult.Error -> {
                oldState.copy(
                    isLoading = false,
                    error = message,
                    movieDetails = null,
                    videos = emptyList()
                )
            }
        }
    }
} 