package com.amonga.snifflicks.module.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.amonga.snifflicks.core.usecase.ResultState
import com.amonga.snifflicks.module.network.model.Movie
import com.amonga.snifflicks.module.network.usecase.MovieListUseCase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class MoviePagingSource @Inject constructor(
    private val movieListUseCase: MovieListUseCase,
    private val movieType: MovieType,
    private val genreArray: Array<Int>?
) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1
            val dateRange = when (movieType) {
                MovieType.POPULAR -> null
                MovieType.UPCOMING -> getUpcomingDateRange()
                MovieType.NOW_PLAYING -> getNowPlayingDateRange()
            }

            when (val result = movieListUseCase(
                MovieListUseCase.Input(
                    pageNo = page,
                    minDate = dateRange?.first,
                    maxDate = dateRange?.second,
                    genreIds = genreArray?.joinToString(",")
                )
            )) {
                is ResultState.Success -> {
                    LoadResult.Page(
                        data = result.data.results,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (result.data.results.isEmpty()) null else page + 1
                    )
                }
                is ResultState.Error -> {
                    LoadResult.Error(result.throwable)
                }
                ResultState.Empty -> {
                    LoadResult.Page(
                        data = emptyList(),
                        prevKey = null,
                        nextKey = null
                    )
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun getUpcomingDateRange(): Pair<String, String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = LocalDate.now().plusDays(1)
        val twoMonthsLater = today.plusMonths(2)
        return Pair(today.format(formatter), twoMonthsLater.format(formatter))
    }

    private fun getNowPlayingDateRange(): Pair<String, String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = LocalDate.now()
        val twoWeeksAgo = today.minusWeeks(2)
        return Pair(twoWeeksAgo.format(formatter), today.format(formatter))
    }
}

enum class MovieType {
    POPULAR,
    UPCOMING,
    NOW_PLAYING
} 