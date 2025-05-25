package com.amonga.snifflicks.module.genre.event

import com.amonga.snifflicks.core.compose.interfaces.IEvent

sealed interface GenreSelectionEvent : IEvent {
    data object LoadGenres : GenreSelectionEvent
    data class ToggleGenre(val genreId: Int) : GenreSelectionEvent
    data object ExploreAll : GenreSelectionEvent
    data object ContinueWithSelection : GenreSelectionEvent
} 