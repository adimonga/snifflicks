package com.amonga.snifflicks.module.genre

import com.amonga.snifflicks.R
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.amonga.snifflicks.core.compose.fragment.ComposeBaseFragment
import com.amonga.snifflicks.core.compose.interfaces.ISideEffect
import com.amonga.snifflicks.core.compose.viewmodel.BaseViewModel
import com.amonga.snifflicks.core.theme.*
import com.amonga.snifflicks.module.genre.event.GenreSelectionEvent
import com.amonga.snifflicks.module.genre.viewmodel.GenreSelectionViewModel
import com.amonga.snifflicks.module.genre.viewstate.GenreSelectionViewState
import com.amonga.snifflicks.module.network.model.Genre
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenreSelectionFragment : ComposeBaseFragment<GenreSelectionViewState>() {

    private val vm: GenreSelectionViewModel by viewModels()

    override fun getVmInstance(): BaseViewModel<*, *, GenreSelectionViewState> = vm

    @Composable
    override fun FragmentContent(initialState: State<GenreSelectionViewState>) {
        LaunchedEffect(Unit) {
            vm.processEvent(GenreSelectionEvent.LoadGenres)
        }

        val state = initialState.value

        Scaffold(
            topBar = { GenreSelectionTopBar() },
            containerColor = BackgroundColor
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    state.isLoading -> {
                        LoadingState()
                    }

                    state.error != null -> {
                        ErrorState(state.error) {
                            vm.processEvent(GenreSelectionEvent.LoadGenres)
                        }
                    }

                    else -> {
                        GenreSelectionContent(
                            genres = state.genres,
                            selectedGenreIds = state.selectedGenreIds,
                            onGenreClick = { vm.processEvent(GenreSelectionEvent.ToggleGenre(it)) },
                            onExploreAllClick = { vm.processEvent(GenreSelectionEvent.ExploreAll) },
                            onContinueClick = { vm.processEvent(GenreSelectionEvent.ContinueWithSelection) }
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun GenreSelectionTopBar() {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = stringResource(id = R.string.genre_selection_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurfaceColor
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SurfaceColor,
                titleContentColor = OnSurfaceColor
            )
        )
    }

    @Composable
    private fun LoadingState() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryColor)
        }
    }

    @Composable
    private fun ErrorState(error: String, onRetry: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.spacing_large)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = OnBackgroundColor,
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

    @Composable
    private fun GenreSelectionContent(
        genres: List<Genre>,
        selectedGenreIds: Set<Int>,
        onGenreClick: (Int) -> Unit,
        onExploreAllClick: () -> Unit,
        onContinueClick: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.spacing_medium))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_medium)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_medium)),
                modifier = Modifier.weight(1f)
            ) {
                items(genres) { genre ->
                    GenreChip(
                        genre = genre,
                        isSelected = selectedGenreIds.contains(genre.id),
                        onClick = { onGenreClick(genre.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(
                    onClick = onExploreAllClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = PrimaryColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.explore_all))
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))

                Button(
                    onClick = onContinueClick,
                    enabled = selectedGenreIds.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                        disabledContainerColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.continue_with_selection))
                }

            }
        }
    }

    @Composable
    private fun GenreChip(
        genre: Genre,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) PrimaryColor else SurfaceColor,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isSelected) PrimaryColor else OnSurfaceColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(onClick = onClick)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.spacing_medium))
            ) {
                Text(
                    text = genre.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) OnPrimaryColor else OnSurfaceColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    override fun onSideEffectReceived(sideEffect: ISideEffect): Boolean {
        return false
    }
} 