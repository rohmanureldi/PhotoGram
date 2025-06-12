@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.eldirohmanur.photogram.presentation.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    val state by viewModel.state.collectAsStateWithLifecycle()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val imageHeightDp = 300.dp
    val imageHeightPx = with(density) { imageHeightDp.roundToPx() }
    var titleArtistHeight by remember {
        mutableStateOf(
            IntSize(0, 0)
        )
    }

    val floatingHeaderVisible by remember(titleArtistHeight) {
        derivedStateOf {
            (scrollState.value > titleArtistHeight.height + imageHeightPx) && screenState != DetailScreenContentState.Loading
        }
    }
    val animateWeight by animateFloatAsState(
        if (screenState == DetailScreenContentState.Success) 0f else 1f,
        label = "containerWeight"
    )

    Box {
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
                        .height(imageHeightDp)
                        .sharedElement(
                            sharedContentState = sharedTransitionScope.rememberSharedContentState(
                                key = artworkUrl
                            ),
                            animatedVisibilityScope = animatedContentScope
                        )
                )
            }

            AnimatedContent(
                modifier = Modifier.let { mModifier ->
                    animateWeight.let {
                        if (it > 0) mModifier.weight(
                            animateWeight
                        ) else mModifier
                    }
                },
                targetState = screenState,
                label = "content",
                transitionSpec = { fadeIn().togetherWith(fadeOut()) }
            ) {
                when (it) {
                    DetailScreenContentState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier)
                        }
                    }

                    is DetailScreenContentState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = it.msg,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .padding(16.dp)
                            )
                        }
                    }

                    is DetailScreenContentState.Success -> {
                        val artwork = state.artwork
                        ArtworkDetailContent(
                            modifier = Modifier,
                            artwork = artwork,
                            onToggleSave = viewModel::toggleSave,
                            onTopContentSizeChanged = {
                                titleArtistHeight = it
                            }
                        )
                    }
                }
            }
        }

        FloatingHeader(
            title = state.artwork.title,
            artist = state.artwork.artist,
            date = state.artwork.date,
            imageUrl = state.artwork.imageUrl,
            isSaved = state.artwork.isSaved,
            isVisible = floatingHeaderVisible,
            onToggleSave = viewModel::toggleSave
        )
    }
}

@Composable
private fun FloatingHeader(
    title: String,
    artist: String,
    date: String,
    imageUrl: String,
    isSaved: Boolean,
    isVisible: Boolean,
    onToggleSave: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(),
        exit = slideOutVertically()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primaryContainer),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlideImage(
                    imageModel = { imageUrl }, // loading a network image using an URL.
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center,
                    ),
                    component = rememberImageComponent {
                        +CustomFailedPlugin
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)),
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee()
                        )

                        Text(
                            text = "By ${artist.ifBlank { "Unknown" }} ${
                                date.let { "- $it" }.takeIf { date.isNotBlank() }.orEmpty()
                            }",
                            style = MaterialTheme.typography.labelSmall,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee()
                        )
                    }

                    SaveArtworkButton(
                        isSaved = isSaved,
                        onToggleSave = onToggleSave,
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
private fun SaveArtworkButton(
    modifier: Modifier = Modifier,
    isSaved: Boolean,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    onToggleSave: () -> Unit
) {
    Surface(
        shape = CircleShape,
        modifier = modifier.animateContentSize(),
        onClick = onToggleSave,
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = if (isSaved) "Saved" else "Save",
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun ArtworkDetailContent(
    modifier: Modifier,
    artwork: ArtworkUiModel,
    onToggleSave: () -> Unit,
    onTopContentSizeChanged: (IntSize) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged(onTopContentSizeChanged)
                .animateContentSize()
        ) {
            Text(
                text = "Credit: ${artwork.credit}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.LightGray,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )

            SaveArtworkButton(
                modifier = Modifier,
                isSaved = artwork.isSaved,
                onToggleSave = onToggleSave
            )
        }
        Column(
            modifier = Modifier
        ) {
            Spacer(modifier = Modifier.height(8.dp))

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
        }

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

        if (artwork.provenance.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Provenance Text",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = artwork.provenance,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
            )
        }

        if (artwork.publicationHistory.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Publication History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = artwork.publicationHistory,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
            )
        }

        if (artwork.exhibitionHistory.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Exhibition History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = artwork.exhibitionHistory,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
            )
        }


    }
}