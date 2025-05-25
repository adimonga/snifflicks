package com.amonga.snifflicks.module.sideeffect

import com.amonga.snifflicks.core.compose.interfaces.ISideEffect

sealed interface MainActivitySideEffect: ISideEffect {

    object OpenLanguageSelectionFragment: MainActivitySideEffect
    class OpenMovieListFragment(val selectedGenreIds: Set<Int>) : MainActivitySideEffect
    class OpenMovieDetailsFragment(val movieId: Int) : MainActivitySideEffect
}