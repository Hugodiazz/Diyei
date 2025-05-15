package com.hdev.diyei.Model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumArt: Int? = null,
    val duration: String,
    val uri: Uri
)
