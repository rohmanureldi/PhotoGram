@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.eldirohmanur.photogram.presentation.saved


import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel
import com.eldirohmanur.photogram.ui.components.CpnArtworkGrid
import com.eldirohmanur.photogram.utils.landscapist.CustomFailedPlugin
import com.eldirohmanur.photogram.utils.landscapist.CustomThumbnailPlugin
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun SavedImagesScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    viewModel: SavedImagesViewModel = hiltViewModel<SavedImagesViewModel>(),
    navigateToDetail: (Int, String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.savedArtworks.isEmpty()) {
            Text(
                text = "No saved artworks yet",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        } else {
            CpnArtworkGrid(
                artworks = state.savedArtworks,
                isLoadMore = false,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onArtworkClick = navigateToDetail,
                onDeleteClick = viewModel::removeFromSaved
            )
//            SavedArtworkGrid(
//                artworks = state.savedArtworks,
//                onArtworkClick = navigateToDetail,
//                onDeleteClick = viewModel::removeFromSaved
//            )
        }
    }
}

@Composable
fun SavedArtworkGrid(
    artworks: List<ArtworkUiModel>,
    onArtworkClick: (Int, String) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items = artworks, key = { it.id }) { artwork ->
            SavedArtworkItem(
                modifier = Modifier.animateItem(),
                artwork = artwork,
                onArtworkClick = onArtworkClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
fun SavedArtworkItem(
    modifier: Modifier,
    artwork: ArtworkUiModel,
    onArtworkClick: (Int, String) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onArtworkClick(artwork.id, artwork.imageUrl) }
    ) {
        Column {
            Box {

                GlideImage(
                    imageModel = { artwork.imageUrl }, // loading a network image using an URL.
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        contentDescription = artwork.title
                    ),
                    component = rememberImageComponent {
                        +CustomThumbnailPlugin(artwork.thumbnailUrl)
                        +CustomFailedPlugin
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                IconButton(
                    onClick = { onDeleteClick(artwork.id) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove from saved",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = artwork.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = artwork.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = artwork.date,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}