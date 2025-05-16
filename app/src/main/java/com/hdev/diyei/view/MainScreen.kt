package com.hdev.diyei.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hdev.diyei.Model.Song
import com.hdev.diyei.viewModel.MainViewModel
import com.hdev.diyei.viewModel.PlayerState


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavHostController
){
    // Lista de canciones
    val songs = viewModel.songs
    val currentSong = viewModel.currentSong.collectAsState().value
    val playerState = viewModel.playerState.collectAsState().value
    Column(modifier = modifier.fillMaxSize()){
        Text(
            text = "Mi música",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn(modifier= modifier.weight(1f)) {
            items(songs){
                SongItem(
                    song = it,
                    onItemClick = { song ->
                       viewModel.play(song.uri)
                    }
                )
            }
        }
        if (currentSong != null) {
            SongItemController(
                song = currentSong,
                onButtonClick = {
                    viewModel.togglePlayPause()
                },
                playerState = playerState,
                navController = navController
            )
        }
    }
}



@Composable
fun SongItem(
    song: Song,
    onItemClick: (Song) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick(song) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen de la carátula (opcional)
        if (song.albumArt != null) {

                Image(
                    bitmap = song.albumArt.asImageBitmap(),
                    contentDescription = "Carátula del álbum",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
            Spacer(modifier = Modifier.width(8.dp))


        } else {
            // Placeholder si no hay imagen
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Gray.copy(alpha = 0.2f))
                    .clip(RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Placeholder",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Información de la canción (título y artista)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Duración de la canción
        Text(
            text = song.duration,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun SongItemController(
    song: Song,
    playerState: PlayerState,
    onButtonClick: () -> Unit,
    navController: NavHostController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(MaterialTheme.colorScheme.onSurface, shape = RoundedCornerShape(18.dp))
            .clickable { navController.navigate("SongDetails") },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen de la carátula (opcional)
        if (song.albumArt != null) {
            Image(
                bitmap = song.albumArt.asImageBitmap(),
                contentDescription = "Carátula del álbum",
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            // Placeholder si no hay imagen
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Gray.copy(alpha = 0.2f))
                    .clip(RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Placeholder",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        // Información de la canción (título y artista)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.surface
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            )
        }
        IconButton(onClick = { onButtonClick() }) {
            Icon(
                imageVector = if (playerState == PlayerState.Playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = "Reproducir",
                tint = MaterialTheme.colorScheme.surface, modifier = Modifier.size(48.dp)
            )
        }
    }
}
