package com.amonga.snifflicks.module.movie.fragment

import com.amonga.snifflicks.R
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.amonga.snifflicks.core.compose.fragment.ComposeBaseFragment
import com.amonga.snifflicks.core.compose.interfaces.ISideEffect
import com.amonga.snifflicks.core.compose.viewmodel.BaseViewModel
import com.amonga.snifflicks.core.theme.*
import com.amonga.snifflicks.module.movie.event.MovieListEvent
import com.amonga.snifflicks.module.movie.viewmodel.MovieListViewModel
import com.amonga.snifflicks.module.movie.viewstate.MovieListViewState
import com.amonga.snifflicks.module.network.model.Movie
import com.valentinilk.shimmer.shimmer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow

@AndroidEntryPoint
class MovieListFragment : ComposeBaseFragment<MovieListViewState>() {

    private val vm: MovieListViewModel by viewModels()

    override fun getVmInstance(): BaseViewModel<*, *, MovieListViewState> = vm

    @Composable
    override fun FragmentContent(initialState: State<MovieListViewState>) {
        LaunchedEffect(Unit) {
            vm.processEvent(MovieListEvent.LoadPopularMovies)
            vm.processEvent(MovieListEvent.LoadUpcomingMovies)
            vm.processEvent(MovieListEvent.LoadNowPlayingMovies)
        }

        val lazyPagingItems = initialState.value.popularMoviesPagingData?.collectAsLazyPagingItems()

        Scaffold(
            topBar = { MovieAppBar() },
            containerColor = BackgroundColor
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(dimensionResource(id = R.dimen.spacing_medium)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.movie_grid_spacing)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.movie_grid_spacing)),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item(span = { GridItemSpan(2) }) {
                    // Now Playing Section
                    MovieSection(
                        title = stringResource(id = R.string.now_playing),
                        moviesPagingData = initialState.value.nowPlayingMoviesPagingData,
                        onMovieClick = { vm.processEvent(MovieListEvent.OnMovieClick(it)) }
                    )
                }

                item(span = { GridItemSpan(2) }) {
                    // Upcoming Section
                    MovieSection(
                        title = stringResource(id = R.string.upcoming),
                        moviesPagingData = initialState.value.upcomingMoviesPagingData,
                        onMovieClick = { vm.processEvent(MovieListEvent.OnMovieClick(it)) }
                    )
                }

                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = stringResource(id = R.string.popular),
                        style = MaterialTheme.typography.titleLarge,
                        color = OnBackgroundColor,
                        modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.spacing_medium))
                    )
                }

                popularMoviesGrid(
                    lazyPagingItems = lazyPagingItems,
                    onMovieClick = { vm.processEvent(MovieListEvent.OnMovieClick(it)) }
                )
            }
        }
    }

    @Composable
    private fun MovieSection(
        title: String,
        moviesPagingData: Flow<PagingData<Movie>>?,
        onMovieClick: (Int) -> Unit
    ) {
        Column(
            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.spacing_medium))
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = OnBackgroundColor,
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.spacing_medium))
            )

            moviesPagingData?.collectAsLazyPagingItems()?.let { pagingItems ->
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.movie_grid_spacing)),
                    contentPadding = PaddingValues(end = dimensionResource(id = R.dimen.spacing_medium))
                ) {
                    items(pagingItems.itemCount) { index ->
                        pagingItems[index]?.let { movie ->
                            MovieCard(
                                movie = movie,
                                onMovieClick = { onMovieClick(movie.id) },
                                modifier = Modifier.width(dimensionResource(id = R.dimen.movie_poster_width))
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MovieCard(
        movie: Movie,
        onMovieClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier.clickable(onClick = onMovieClick),
            colors = CardDefaults.cardColors(containerColor = SurfaceColor)
        ) {
            Column {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.movie_poster_height)),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.spacing_medium))
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurfaceColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_tiny)))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = movie.releaseDate.split("-")[0],
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceColor.copy(alpha = 0.7f)
                        )

                        Text(
                            text = stringResource(
                                id = R.string.rating_format,
                                movie.voteAverage
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = RatingStarColor
                        )
                    }
                }
            }
        }
    }

    private fun LazyGridScope.popularMoviesGrid(
        onMovieClick: (Int) -> Unit,
        lazyPagingItems: LazyPagingItems<Movie>?
    ) {
        lazyPagingItems ?: return

        when (lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                loadingGrid()
            }

            is LoadState.Error -> {
                item(span = { GridItemSpan(2) }) {
                    ErrorState(
                        message = (lazyPagingItems.loadState.refresh as LoadState.Error).error.message
                            ?: stringResource(id = R.string.error_generic),
                        onRetry = { lazyPagingItems.refresh() }
                    )
                }
            }

            else -> {
                items(lazyPagingItems.itemCount) { index ->
                    lazyPagingItems[index]?.let { movie ->
                        MovieCard(
                            movie = movie,
                            onMovieClick = { onMovieClick(movie.id) }
                        )
                    }
                }

                when (lazyPagingItems.loadState.append) {
                    is LoadState.Loading -> {
                        items(2) {
                            ShimmerMovieItem()
                        }
                    }

                    is LoadState.Error -> {
                        item(span = { GridItemSpan(2) }) {
                            ErrorItem(
                                message = (lazyPagingItems.loadState.append as LoadState.Error).error.message
                                    ?: stringResource(id = R.string.error_generic),
                                onRetry = { lazyPagingItems.retry() }
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    @Composable
    private fun ShimmerMovieItem(
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .shimmer(),
            colors = CardDefaults.cardColors(containerColor = SurfaceColor)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f)
                        .background(ShimmerBackground)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.spacing_medium))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(id = R.dimen.text_size_medium))
                            .background(ShimmerBackground)
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .width(dimensionResource(id = R.dimen.movie_rating_chip_height))
                                .height(dimensionResource(id = R.dimen.text_size_small))
                                .background(ShimmerBackground)
                        )
                        Box(
                            modifier = Modifier
                                .width(dimensionResource(id = R.dimen.movie_rating_chip_height))
                                .height(dimensionResource(id = R.dimen.text_size_small))
                                .background(ShimmerBackground)
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MovieAppBar() {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_small))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = stringResource(id = R.string.app_icon),
                        tint = PrimaryColor,
                        modifier = Modifier
                            .background(Color.Black)
                            .size(dimensionResource(id = R.dimen.icon_size_large))
                    )
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        color = OnBackgroundColor
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SurfaceColor,
                titleContentColor = OnSurfaceColor
            )
        )
    }

    private fun LazyGridScope.loadingGrid() {
        items(6) {
            ShimmerMovieItem()
        }
    }

    @Composable
    private fun ErrorState(message: String, onRetry: () -> Unit) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.spacing_medium)),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.spacing_large)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_error),
                        contentDescription = stringResource(id = R.string.error_icon),
                        tint = ErrorColor,
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large))
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurfaceColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                    ) {
                        Text(stringResource(id = R.string.retry))
                    }
                }
            }
        }
    }

    @Composable
    private fun ErrorItem(message: String, onRetry: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.spacing_medium)),
            colors = CardDefaults.cardColors(containerColor = SurfaceColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.spacing_medium)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceColor,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text(stringResource(id = R.string.retry))
                }
            }
        }
    }

    override fun onSideEffectReceived(sideEffect: ISideEffect): Boolean {
        return false
    }

    companion object {
        const val SELECTED_GENRE_LIST = "SELECTED_GENRE_LIST"
        fun newInstance(selectedGenreIds: Set<Int>): Fragment {
            return MovieListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(SELECTED_GENRE_LIST, selectedGenreIds.toTypedArray())
                }
            }
        }
    }
} 