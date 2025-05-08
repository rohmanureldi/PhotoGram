package com.eldirohmanur.photogram.utils.landscapist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.plugins.ImagePlugin


data class CustomThumbnailPlugin(val model: Any) : ImagePlugin.LoadingStatePlugin {

    @Composable
    override fun compose(
        modifier: Modifier,
        imageOptions: ImageOptions,
        executor: @Composable (IntSize) -> Unit
    ): ImagePlugin = apply {
        Box(modifier = Modifier.fillMaxSize()) {
            GlideImage(
                modifier = Modifier.fillMaxSize(),
                imageModel = { model },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            )
        }
    }
}