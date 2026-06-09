package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.extensions.ExtensionManager
import com.example.extensions.VideoLink

import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@Composable
fun PlayerScreen(navController: NavController, url: String, providerName: String) {
    var videoLinks by remember { mutableStateOf<List<VideoLink>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(url, providerName) {
        val provider = ExtensionManager.loadedProviders.value.find { it.name == providerName }
        if (provider != null) {
            try {
                videoLinks = provider.extractVideoLinks(url)
            } catch (e: Exception) {
                error = e.message
            }
        } else {
            error = "Provider not found"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (error != null) {
            Text(error!!, color = Color.Red)
        } else if (videoLinks == null) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            val videoUrl = videoLinks!!.firstOrNull()?.url
            if (videoUrl != null) {
                val exoPlayer = remember(videoUrl) {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(videoUrl))
                        prepare()
                        playWhenReady = true
                    }
                }

                DisposableEffect(videoUrl) {
                    onDispose {
                        exoPlayer.release()
                    }
                }

                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            keepScreenOn = true
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("No playable links found", color = Color.White)
            }
        }
        
        // Back Button Overlay
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = androidx.compose.foundation.shape.CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
    }
}
