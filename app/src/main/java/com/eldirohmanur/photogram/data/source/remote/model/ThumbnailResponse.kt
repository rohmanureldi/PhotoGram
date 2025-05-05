package com.eldirohmanur.photogram.data.source.remote.model

import com.google.gson.annotations.SerializedName

data class ThumbnailResponse(
    @SerializedName("lqip") val lqip: String?,
    @SerializedName("width") val width: Int?,
    @SerializedName("height") val height: Int?,
    @SerializedName("alt_text") val altText: String?
)
