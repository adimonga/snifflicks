package com.amonga.snifflicks.module.genre.viewmodel

import com.amonga.snifflicks.core.compose.viewmodel.BaseViewModel
import com.amonga.snifflicks.module.genre.event.GenreSelectionEvent
import com.amonga.snifflicks.module.genre.viewresult.GenreSelectionViewResult
import com.amonga.snifflicks.module.genre.viewstate.GenreSelectionViewState
import com.amonga.snifflicks.module.network.service.MovieService
import com.amonga.snifflicks.module.sideeffect.MainActivitySideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GenreSelectionViewModel @Inject constructor(
    private val movieService: MovieService
) : BaseViewModel<GenreSelectionEvent, GenreSelectionViewResult, GenreSelectionViewState>(
    GenreSelectionViewState.INITIAL
) {

    override suspend fun HandleEventScope.handleEvent(event: GenreSelectionEvent) {
        when (event) {
            GenreSelectionEvent.LoadGenres -> {
                GenreSelectionViewResult.Loading.reduceToState()
                try {
                    val response = movieService.getMovieGenres()
                    GenreSelectionViewResult.Success(response.genres).reduceToState()
                } catch (e: Exception) {
                    GenreSelectionViewResult.Error(e.message ?: "Failed to load genres")
                        .reduceToState()
                }
            }

            is GenreSelectionEvent.ToggleGenre -> {
                GenreSelectionViewResult.UpdateSelectedGenres(event.genreId).reduceToState()
            }

            GenreSelectionEvent.ExploreAll -> {
                MainActivitySideEffect.OpenMovieListFragment(emptySet()).emit()
            }

            GenreSelectionEvent.ContinueWithSelection -> {
                if (states.value.selectedGenreIds.isNotEmpty()) {
                    MainActivitySideEffect.OpenMovieListFragment(states.value.selectedGenreIds)
                        .emit()
                }
            }
        }
    }

    override fun GenreSelectionViewResult.reduce(oldState: GenreSelectionViewState): GenreSelectionViewState {
        return when (this) {
            is GenreSelectionViewResult.Loading -> {
                oldState.copy(isLoading = true, error = null)
            }

            is GenreSelectionViewResult.Success -> {
                oldState.copy(isLoading = false, error = null, genres = genres)
            }

            is GenreSelectionViewResult.Error -> {
                oldState.copy(isLoading = false, error = message)
            }

            is GenreSelectionViewResult.UpdateSelectedGenres -> {
                val newSelectedGenres = oldState.selectedGenreIds.toMutableSet()
                if (newSelectedGenres.contains(genreId)) {
                    newSelectedGenres.remove(genreId)
                } else {
                    newSelectedGenres.add(genreId)
                }
                oldState.copy(selectedGenreIds = newSelectedGenres)
            }
        }
    }
} 