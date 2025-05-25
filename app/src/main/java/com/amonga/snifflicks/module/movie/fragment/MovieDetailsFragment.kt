package com.amonga.snifflicks.module.movie.fragment

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import com.amonga.snifflicks.core.compose.fragment.ComposeBaseFragment
import com.amonga.snifflicks.core.compose.interfaces.ISideEffect
import com.amonga.snifflicks.core.compose.viewmodel.BaseViewModel
import com.amonga.snifflicks.module.movie.dialog.YoutubePlayerDialog
import com.amonga.snifflicks.module.movie.event.MovieDetailsEvent
import com.amonga.snifflicks.module.movie.viewmodel.MovieDetailsViewModel
import com.amonga.snifflicks.module.movie.viewstate.MovieDetailsViewState
import com.amonga.snifflicks.module.network.model.MovieDetails
import com.amonga.snifflicks.module.network.model.MovieVideo
import com.valentinilk.shimmer.shimmer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : ComposeBaseFragment<MovieDetailsViewState>() {

    private val vm: MovieDetailsViewModel by viewModels()

    override fun getVmInstance(): BaseViewModel<*, *, MovieDetailsViewState> = vm

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun FragmentContent(initialState: State<MovieDetailsViewState>) {
        LaunchedEffect(Unit) {
            vm.processEvent(
                MovieDetailsEvent.LoadMovieDetails(
                    arguments?.getInt(SELECTED_MOVIE_ID)
                        ?: throw Exception("SELECTED_MOVIE_ID is required for this fragment")
                )
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Movie Details", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { parentFragmentManager.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1A1A1A)
                    )
                )
            },
            containerColor = Color.Black
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    initialState.value.isLoading -> {
                        LoadingState()
                    }

                    initialState.value.error != null -> {
                        ErrorState(
                            message = initialState.value.error!!,
                            onRetry = { vm.processEvent(MovieDetailsEvent.RetryLoadingMovieDetails) }
                        )
                    }

                    initialState.value.movieDetails != null -> {
                        MovieDetailsContent(
                            movieDetails = initialState.value.movieDetails!!,
                            videos = initialState.value.videos
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun MovieDetailsContent(movieDetails: MovieDetails, videos: List<MovieVideo>) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Backdrop
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w780${movieDetails.backdropPath}",
                contentDescription = movieDetails.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title and Release Year
                Text(
                    text = "${movieDetails.title} (${movieDetails.releaseDate.split("-")[0]})",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Tagline
                movieDetails.tagline?.let { tagline ->
                    Text(
                        text = tagline,
                        fontSize = 16.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Rating and Runtime
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MovieInfoChip(
                        label = "★ ${String.format("%.1f", movieDetails.voteAverage)}",
                        color = Color.Yellow
                    )
                    movieDetails.runtime?.let { runtime ->
                        MovieInfoChip(
                            label = "${runtime}min",
                            color = Color.Green
                        )
                    }
                }

                // Genres
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    movieDetails.genres.forEach { genre ->
                        MovieInfoChip(
                            label = genre.name,
                            color = Color(0xFF018ef5)
                        )
                    }
                }

                // Videos Section
                if (videos.isNotEmpty()) {
                    Text(
                        text = "Videos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        this.items(items = videos.filter { it.site == "YouTube" }) { video ->
                            VideoThumbnail(video = video)
                        }
                    }
                }

                // Overview
                Text(
                    text = "Overview",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                movieDetails.overview?.let { overview ->
                    Text(
                        text = overview,
                        fontSize = 16.sp,
                        color = Color.LightGray,
                        lineHeight = 24.sp
                    )
                }

                // Production Companies
                if (movieDetails.productionCompanies.isNotEmpty()) {
                    Text(
                        text = "Production Companies",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        movieDetails.productionCompanies.forEach { company ->
                            if (company.logoPath != null) {
                                AsyncImage(
                                    model = "https://image.tmdb.org/t/p/w200${company.logoPath}",
                                    contentDescription = company.name,
                                    modifier = Modifier
                                        .height(40.dp)
                                        .background(Color(0xFF1A1A1A))
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MovieInfoChip(
        label: String,
        color: Color
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = color.copy(alpha = 0.1f),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text(
                text = label,
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }

    @Composable
    private fun VideoThumbnail(video: MovieVideo) {
        var showYoutubePlayer by remember { mutableStateOf(false) }

        if (showYoutubePlayer) {
            YoutubePlayerDialog(
                videoId = video.key,
                videoTitle = video.name,
                onDismiss = { showYoutubePlayer = false }
            )
        }

        Card(
            modifier = Modifier
                .width(300.dp)
                .height(200.dp)
                .clickable { showYoutubePlayer = true },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            Box {
                AsyncImage(
                    model = video.getYoutubeThumbnailUrl(),
                    contentDescription = video.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Play button overlay
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                        .background(Color.Black.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Video title overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = video.name,
                        color = Color.White,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    @Composable
    private fun LoadingState() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Backdrop shimmer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmer()
                    .background(Color.Gray.copy(alpha = 0.3f))
            )

            // Title shimmer
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(24.dp)
                    .padding(top = 16.dp)
                    .shimmer()
                    .background(Color.Gray.copy(alpha = 0.3f))
            )

            // Info chips shimmer
            Row(
                modifier = Modifier
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shimmer()
                            .background(Color.Gray.copy(alpha = 0.3f))
                    )
                }
            }

            // Overview shimmer
            repeat(4) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .padding(vertical = 4.dp)
                        .shimmer()
                        .background(Color.Gray.copy(alpha = 0.3f))
                )
            }
        }
    }

    @Composable
    private fun ErrorState(message: String, onRetry: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Retry")
            }
        }
    }

    override fun onSideEffectReceived(sideEffect: ISideEffect): Boolean {
        return false
    }

    companion object {
        const val SELECTED_MOVIE_ID = "SELECTED_MOVIE_ID"
        fun newInstance(movieId: Int): Fragment {
            return MovieDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(SELECTED_MOVIE_ID, movieId)
                }
            }
        }
    }
} 