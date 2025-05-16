package com.hdev.diyei.viewModel

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hdev.diyei.Model.Song
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val player = ExoPlayerManager(app)
    val playerState = player.playerState

    private val _songs = mutableStateListOf<Song>()
    val songs: List<Song> get() = _songs

    private val nextSong = MutableStateFlow<Song?>(null)
    // Cancion actual
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private var currentIndex = 0

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    init {
        loadSongs()
        updatePosition()
        player.setOnSongEndedListener {
            playNext()
        }
    }

    private fun loadSongs() {
        val context = getApplication<Application>().applicationContext
        _songs.addAll(loadSongsFromDevice(context))
    }

    fun play(uri: Uri) {
        val index = _songs.indexOfFirst { it.uri == uri }
        if (index != -1) {
            currentIndex = index
            _currentSong.value = _songs[index]
            player.playSong(uri.toString())
        }
    }

    fun playNext() {
        if (_songs.isEmpty()) return
        currentIndex = (currentIndex + 1) % _songs.size
        val nextSong = _songs[currentIndex]
        _currentSong.value = nextSong
        player.playSong(nextSong.uri.toString())
    }



    fun togglePlayPause() {
        player.togglePlayPause()
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }



    fun updatePosition() {
        viewModelScope.launch {
            while (true) {
                val pos = player.getCurrentPosition()
                _currentPosition.value = pos
                delay(50L) // cada 500ms
            }
        }
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }

    fun getCurrentPosition(): Long = _currentPosition.value
    fun getCurrentPositionString(): String = formatDuration(_currentPosition.value)

    fun loadSongsFromDevice(context: Context): List<Song> {
        val songs = mutableListOf<Song>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"



        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val title = cursor.getString(titleCol)
                val artist = cursor.getString(artistCol)
                val album = cursor.getString(albumCol)
                val duration = cursor.getLong(durationCol)
                val formattedDuration = formatDuration(duration)
                val contentUri = ContentUris.withAppendedId(collection, id)
                val albumArt = getEmbeddedAlbumArt(context, contentUri)
                print(albumArt.toString())
                songs.add(Song(id, title, artist, album, albumArt,formattedDuration,duration,contentUri))
            }
        }

        return songs
    }

    fun formatDuration(durationInMillis: Long): String {
        val totalSeconds = durationInMillis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
    fun getEmbeddedAlbumArt(context: Context, uri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            // Versión que funciona con Content URI
            retriever.setDataSource(context, uri)

            // Alternativa 1: Buscar artwork en diferentes metadatos
            retriever.embeddedPicture?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size)
            } ?: run {
                // Alternativa 2: Intentar extraer de otros campos
                val artworkBytes = retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_IMAGE_PRIMARY
                )?.toByteArray()
                artworkBytes?.let {
                    BitmapFactory.decodeByteArray(it, 0, it.size)
                }
            }
        } catch (e: Exception) {
            Log.e("AlbumArt", "Error al obtener artwork: ${e.message}")
            null
        } finally {
            retriever.release()
        }
    }
}
