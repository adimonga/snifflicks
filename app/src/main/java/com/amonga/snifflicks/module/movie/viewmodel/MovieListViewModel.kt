package com.amonga.snifflicks.module.movie.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.amonga.snifflicks.core.compose.viewmodel.BaseViewModel
import com.amonga.snifflicks.module.movie.event.MovieListEvent
import com.amonga.snifflicks.module.movie.fragment.MovieListFragment
import com.amonga.snifflicks.module.movie.viewresult.MovieListViewResult
import com.amonga.snifflicks.module.movie.viewstate.MovieListViewState
import com.amonga.snifflicks.module.network.usecase.MovieListUseCase
import com.amonga.snifflicks.module.paging.MoviePagingSource
import com.amonga.snifflicks.module.paging.MovieType
import com.amonga.snifflicks.module.sideeffect.MainActivitySideEffect.OpenMovieDetailsFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val movieListUseCase: MovieListUseCase
) : BaseViewModel<MovieListEvent, MovieListViewResult, MovieListViewState>(MovieListViewState.INITIAL) {

    private val genreArray = savedState.get(MovieListFragment.SELECTED_GENRE_LIST) as? Array<Int>

    private val pagingConfig = PagingConfig(
        pageSize = 20,
        prefetchDistance = 2,
        enablePlaceholders = false
    )

    private fun createMoviePager(movieType: MovieType) = Pager(
        config = pagingConfig,
        pagingSourceFactory = { MoviePagingSource(movieListUseCase, movieType, genreArray) }
    ).flow.cachedIn(viewModelScope)

    override suspend fun HandleEventScope.handleEvent(event: MovieListEvent) {
        when (event) {
            MovieListEvent.LoadPopularMovies -> {
                MovieListViewResult.UpdatePopularMovies(createMoviePager(MovieType.POPULAR))
                    .reduceToState()
            }

            MovieListEvent.LoadUpcomingMovies -> {
                MovieListViewResult.UpdateUpcomingMovies(createMoviePager(MovieType.UPCOMING))
                    .reduceToState()
            }

            MovieListEvent.LoadNowPlayingMovies -> {
                MovieListViewResult.UpdateNowPlayingMovies(createMoviePager(MovieType.NOW_PLAYING))
                    .reduceToState()
            }

            is MovieListEvent.OnMovieClick -> {
                OpenMovieDetailsFragment(event.movieId).emit()
                // Handle movie click
            }
        }
    }

    override fun MovieListViewResult.reduce(oldState: MovieListViewState): MovieListViewState {
        return when (this) {
            is MovieListViewResult.UpdatePopularMovies -> {
                oldState.copy(popularMoviesPagingData = movies)
            }

            is MovieListViewResult.UpdateUpcomingMovies -> {
                oldState.copy(upcomingMoviesPagingData = movies)
            }

            is MovieListViewResult.UpdateNowPlayingMovies -> {
                oldState.copy(nowPlayingMoviesPagingData = movies)
            }
        }
    }
} 