package com.hdev.diyei.viewModel

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
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

    //Funcion que devuelve el tiempo actual
    fun getCurrentPosition(): Long {
        return exoPlayer.currentPosition
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

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun setOnSongEndedListener(onEnded: () -> Unit) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    onEnded()
                }
            }
        })
    }
}

// Estados del reproductor
sealed class PlayerState {
    object Idle : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    data class Error(val message: String) : PlayerState()
}
