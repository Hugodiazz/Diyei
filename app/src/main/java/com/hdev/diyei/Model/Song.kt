package com.hdev.diyei.Model

data class Song(
    val title: String,
    val artist: String,
    val albumArt: Int? = null,
    val duration: String
)
