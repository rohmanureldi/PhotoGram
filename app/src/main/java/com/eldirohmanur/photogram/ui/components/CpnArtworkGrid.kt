package com.eldirohmanur.photogram.ui.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel
import com.eldirohmanur.photogram.utils.OnBottomReached
import com.eldirohmanur.photogram.utils.landscapist.CustomFailedPlugin
import com.eldirohmanur.photogram.utils.landscapist.CustomThumbnailPlugin
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.collections.immutable.ImmutableList


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CpnArtworkGrid(
    artworks: ImmutableList<ArtworkUiModel>,
    isLoadMore: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onArtworkClick: (Int, String) -> Unit,
    onLoadMore: (() -> Unit)? = null,
    onDeleteClick: ((Int) -> Unit)? = null
) {
    val gridState = rememberLazyGridState()

    gridState.OnBottomReached(onLoadMore)

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        state = gridState
    ) {
        items(artworks) { artwork ->
            ArtworkItem(
                artwork = artwork,
                onArtworkClick = onArtworkClick,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onDeleteClick = onDeleteClick
            )

        }
        if (isLoadMore) {
            item(
                span = {
                    GridItemSpan(3)
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ArtworkItem(
    artwork: ArtworkUiModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onArtworkClick: (Int, String) -> Unit,
    onDeleteClick: ((Int) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .clickable { onArtworkClick(artwork.id, artwork.imageUrl) }
    ) {
        Box {
            with(sharedTransitionScope) {
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
                        .sharedElement(
                            sharedContentState = sharedTransitionScope.rememberSharedContentState(
                                key = artwork.imageUrl
                            ),
                            animatedVisibilityScope = animatedContentScope
                        )
                )
            }

            if (onDeleteClick != null) {
                IconButton(
                    onClick = { onDeleteClick.invoke(artwork.id) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove from saved",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

    }
}