package com.hdev.diyei.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hdev.diyei.Model.Song
import com.hdev.diyei.viewModel.MainViewModel
import com.hdev.diyei.viewModel.PlayerState

@Composable
fun SongScreen(
    viewModel: MainViewModel,
    navController: NavHostController
) {
    val song = viewModel.currentSong.collectAsState().value
    val playerState = viewModel.playerState.collectAsState().value

    if (song == null) {
        return
    }else {
        Box(modifier = Modifier.fillMaxSize()) {
            if (song.albumArt != null) {
                Image(
                    bitmap = song.albumArt.asImageBitmap(),
                    contentDescription = "Carátula del álbum",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            renderEffect = BlurEffect(
                                radiusX = 20f,
                                radiusY = 20f
                            )
                        },
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(800.dp) // Ajusta la altura del desvanecimiento según necesites
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surface),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cerrar")
                    }
                    Box(
                        modifier = Modifier.weight(1f), // El Text ocupa el espacio central
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Reproduciendo",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.width(48.dp))
                }
                // Carátula Circular
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(300.dp)
                            .background(Color.Gray.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            song.albumArt != null -> {
                                Image(
                                    bitmap = song.albumArt.asImageBitmap(),
                                    contentDescription = "Carátula del álbum",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            else -> {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Placeholder",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
                // Información de la Canción

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = song.title,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = song.artist,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = song.album,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Barra de Progreso y Tiempo
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = viewModel.getCurrentPositionString(), // Replace with current time
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = song.duration, // Replace with remaining time
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        val totalDurationMillis = song.durationL
                        val currentPositionMillis by viewModel.currentPosition.collectAsState()
                        val sliderPosition = if (totalDurationMillis > 0) {
                            currentPositionMillis.toFloat() / totalDurationMillis.toFloat()
                        } else {
                            0f
                        } // Replace with actual progress
                        Slider(
                            value = sliderPosition.coerceIn(0f, 1f),
                            onValueChange = { newValue ->
                                val newPosition = (newValue * totalDurationMillis).toLong()
                                viewModel.seekTo(newPosition)
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = Color.Blue,
                                activeTrackColor = Color.Blue,
                                inactiveTrackColor = Color.DarkGray
                            )
                        )
                    }

                    // Controles de Reproducción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { /* Handle shuffle */ }) {
                            Icon(Icons.Filled.Shuffle, contentDescription = "Shuffle")
                        }
                        IconButton(onClick = { /* Handle previous */ }) {
                            Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous")
                        }
                        Button(
                            onClick = { viewModel.togglePlayPause() }, // Call the toggle function
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Blue,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.size(60.dp)
                        ) {
                            Icon(
                                imageVector = if (playerState == PlayerState.Playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Play/Pause",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        IconButton(onClick = { viewModel.playNext() }) {
                            Icon(Icons.Filled.SkipNext, contentDescription = "Next")
                        }
                        IconButton(onClick = { /* Handle queue */ }) {
                            Icon(Icons.Filled.List, contentDescription = "Queue")
                        }
                }
            }
        }
    }
}