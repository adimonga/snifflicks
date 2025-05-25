package com.amonga.snifflicks.module.genre.viewstate

import com.amonga.snifflicks.core.compose.interfaces.IViewState
import com.amonga.snifflicks.module.network.model.Genre

data class GenreSelectionViewState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val genres: List<Genre> = emptyList(),
    val selectedGenreIds: Set<Int> = emptySet()
) : IViewState {
    companion object {
        val INITIAL = GenreSelectionViewState()
    }
} 