package com.amonga.snifflicks.module.network.usecase

import com.amonga.snifflicks.core.usecase.BaseSuspendingUseCase
import com.amonga.snifflicks.core.usecase.EmptyStateException
import com.amonga.snifflicks.core.usecase.ResultState
import com.amonga.snifflicks.module.network.model.MovieDetails
import com.amonga.snifflicks.module.network.service.MovieService
import javax.inject.Inject

class MovieDetailsUseCase @Inject constructor(
    private val movieDetailsService: MovieService
) : BaseSuspendingUseCase<MovieDetailsUseCase.Input, MovieDetails>() {

    data class Input(val movieId: Int)

    override fun onException(throwable: Throwable, parameters: Input): ResultState<MovieDetails>? {
        return null
    }

    override suspend fun execute(input: Input): MovieDetails {
        return movieDetailsService.getMovieDetails(movieId = input.movieId)
            .takeIf { it.id > 0 } ?: throw EmptyStateException()
    }
} 