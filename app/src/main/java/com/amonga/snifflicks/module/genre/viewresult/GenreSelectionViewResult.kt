package com.amonga.snifflicks.module.genre.viewresult

import com.amonga.snifflicks.core.compose.interfaces.IViewResult
import com.amonga.snifflicks.module.network.model.Genre

sealed interface GenreSelectionViewResult : IViewResult {
    data object Loading : GenreSelectionViewResult
    data class Success(val genres: List<Genre>) : GenreSelectionViewResult
    data class Error(val message: String) : GenreSelectionViewResult
    data class UpdateSelectedGenres(val genreId: Int) : GenreSelectionViewResult
} 