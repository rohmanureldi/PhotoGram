package com.eldirohmanur.photogram.data.source.remote.model

import com.google.gson.annotations.SerializedName


data class ArtworkDataResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("artist_title") val artistName: String?,
    @SerializedName("date_display") val dateDisplay: String?,
    @SerializedName("medium_display") val mediumDisplay: String?,
    @SerializedName("image_id") val imageId: String?,
    @SerializedName("thumbnail") val thumbnail: ThumbnailResponse?,
    @SerializedName("description") val description: String?,
    @SerializedName("provenance_text") val provenance: String?,
    @SerializedName("publication_history") val publication: String?,
    @SerializedName("exhibition_history") val exhibition: String?,
    @SerializedName("credit_line") val credit: String? = ""
)