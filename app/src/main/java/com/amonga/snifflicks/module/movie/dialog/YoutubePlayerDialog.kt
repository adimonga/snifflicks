package com.amonga.snifflicks.module.movie.dialog

import com.amonga.snifflicks.R
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.amonga.snifflicks.core.theme.DialogBackgroundColor
import com.amonga.snifflicks.core.theme.DialogCloseButtonBackground
import com.amonga.snifflicks.core.theme.DialogTextColor
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YoutubePlayerDialog(
    videoId: String,
    videoTitle: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var youTubePlayer by remember { mutableStateOf<YouTubePlayerView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            youTubePlayer?.release()
        }
    }

    Dialog(
        onDismissRequest = {
            youTubePlayer?.release()
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DialogBackgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DialogBackgroundColor)
            ) {
                // YouTube Player
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.7777778f)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            YouTubePlayerView(ctx).apply {
                                layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )

                                (context as? AppCompatActivity)?.lifecycle?.addObserver(this)

                                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                    override fun onReady(player: YouTubePlayer) {
                                        player.loadVideo(videoId, 0f)
                                    }
                                })
                            }.also { youTubePlayer = it }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Video title
                Text(
                    text = videoTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = DialogTextColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.dialog_content_padding))
                )
            }

            // Floating close button centered at top
            Surface(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.dialog_close_button_size))
                    .align(Alignment.TopCenter)
                    .offset(y = dimensionResource(id = R.dimen.dialog_close_button_offset))
                    .clip(CircleShape)
                    .zIndex(1f),
                color = DialogCloseButtonBackground
            ) {
                IconButton(
                    onClick = {
                        youTubePlayer?.release()
                        onDismiss()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.dialog_close_button),
                        tint = DialogTextColor,
                        modifier = Modifier.size(dimensionResource(id = R.dimen.dialog_close_icon_size))
                    )
                }
            }
        }
    }
} 