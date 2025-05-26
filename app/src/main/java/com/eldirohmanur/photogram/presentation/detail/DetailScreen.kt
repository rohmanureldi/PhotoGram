@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.eldirohmanur.photogram.presentation.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel
import com.eldirohmanur.photogram.utils.landscapist.CustomFailedPlugin
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ArtworkDetailScreen(
    artworkId: Int,
    artworkUrl: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    viewModel: DetailViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadArtwork(artworkId)
    }

    val state by viewModel.state.collectAsState()
    val artwork = state.artwork
    val scrollState = rememberScrollState()

    Scaffold(
        floatingActionButton = {
            artwork?.let {
                SaveArtworkButton(it.isSaved) {
                    if (it.isSaved) {
                        viewModel.removeFromSaved(artwork.id)
                    } else {
                        viewModel.saveArtwork()
                    }
                }
            }
        }
    ) { paddingValues ->
        paddingValues
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            ) {
            with(sharedTransitionScope) {
                GlideImage(
                    imageModel = { artworkUrl }, // loading a network image using an URL.
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center,
                    ),
                    component = rememberImageComponent {
                        +CustomFailedPlugin
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .sharedElement(
                            sharedContentState = sharedTransitionScope.rememberSharedContentState(
                                key = artworkUrl
                            ),
                            animatedVisibilityScope = animatedContentScope
                        )
                )
            }

            AnimatedContent(state, label = "content",) {
                when {
                    it.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier)
                        }
                    }

                    it.error != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = state.error!!,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .padding(16.dp)
                            )
                        }
                    }

                    it.artwork != null -> {
                        ArtworkDetailContent(
                            modifier = Modifier,
                            artwork = it.artwork
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SaveArtworkButton(
    isSaved: Boolean,
    onToggleSave: () -> Unit
) {
    FloatingActionButton(
        onClick = onToggleSave
    ) {
        Icon(
            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isSaved) "Remove from saved" else "Save artwork"
        )
    }
}

@Composable
fun ArtworkDetailContent(
    modifier: Modifier,
    artwork: ArtworkUiModel
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = artwork.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Artist: ${artwork.artist}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Date: ${artwork.date}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Description",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = AnnotatedString.Companion.fromHtml(artwork.description.ifEmpty { "No description available." }),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
        )
    }
}