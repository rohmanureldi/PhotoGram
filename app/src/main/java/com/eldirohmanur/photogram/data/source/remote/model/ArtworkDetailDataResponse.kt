package com.eldirohmanur.photogram.data.source.remote.model

import com.google.gson.annotations.SerializedName


data class ArtworkDetailDataResponse(
    @SerializedName("id") val id: Int? = 0,
    @SerializedName("title") val title: String? = "",
    @SerializedName("artist_title") val artistName: String? = "",
    @SerializedName("date_display") val dateDisplay: String? = "",
    @SerializedName("medium_display") val mediumDisplay: String? = "",
    @SerializedName("dimensions") val dimensions: String? = "",
    @SerializedName("image_id") val imageId: String? = "",
    @SerializedName("description") val description: String? = "",
    @SerializedName("provenance_text") val provenanceText: String? = "",
    @SerializedName("exhibition_history") val exhibitionHistory: String? = "",
    @SerializedName("publication_history") val publicationHistory: String? = "",
    @SerializedName("place_of_origin") val placeOfOrigin: String? = "",
    @SerializedName("credit_line") val credit: String? = "",
)