package com.hdev.diyei.Model

import android.graphics.Bitmap
import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumArt: Bitmap? = null,
    val duration: String,
    val durationL: Long,
    val uri: Uri
)
