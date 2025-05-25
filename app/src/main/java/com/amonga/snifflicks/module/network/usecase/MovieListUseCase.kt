package com.amonga.snifflicks.module.network.usecase

import com.amonga.snifflicks.core.usecase.BaseSuspendingUseCase
import com.amonga.snifflicks.core.usecase.EmptyStateException
import com.amonga.snifflicks.core.usecase.ResultState
import com.amonga.snifflicks.module.network.model.MovieResponse
import com.amonga.snifflicks.module.network.service.MovieService
import javax.inject.Inject

class MovieListUseCase @Inject constructor(
    private val movieService: MovieService
) : BaseSuspendingUseCase<MovieListUseCase.Input, MovieResponse>() {

    data class Input(
        val pageNo: Int,
        val minDate: String? = null,
        val maxDate: String? = null,
        val genreIds: String?
    )

    override fun onException(throwable: Throwable, parameters: Input): ResultState<MovieResponse>? {
        return null
    }

    override suspend fun execute(input: Input): MovieResponse {
        val params = mutableMapOf(
            "page" to input.pageNo.toString(),
            "include_adult" to "false",
            "include_video" to "false",
            "language" to "en-US",
            "sort_by" to "popularity.desc"
        )

        if (input.minDate != null && input.maxDate != null) {
            params["with_release_type"] = "3|2"
            params["primary_release_date.gte"] = input.minDate
            params["primary_release_date.lte"] = input.maxDate
        }

        if(input.genreIds != null){
            params["with_genres"] = input.genreIds
        }

        return movieService.discoverMovies(params).takeIf { it.results.isNotEmpty() }
            ?: throw EmptyStateException()
    }
}