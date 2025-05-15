package com.hdev.diyei.viewModel

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ExoPlayerManager(context: Context) {
    // Estado actual de la reproducción
    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val playerState: StateFlow<PlayerState> = _playerState

    private val exoPlayer = ExoPlayer.Builder(context).build()

    fun playSong(uri: String) {
        val mediaItem = MediaItem.fromUri(Uri.parse(uri))
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
        _playerState.value = PlayerState.Playing
    }

    fun pause() {
        exoPlayer.pause()
        _playerState.value = PlayerState.Paused
    }

    fun release() {
        exoPlayer.release()
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            _playerState.value = PlayerState.Paused
        } else {
            exoPlayer.play()
            _playerState.value = PlayerState.Playing
        }
    }
}

// Estados del reproductor
sealed class PlayerState {
    object Idle : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    data class Error(val message: String) : PlayerState()
}
