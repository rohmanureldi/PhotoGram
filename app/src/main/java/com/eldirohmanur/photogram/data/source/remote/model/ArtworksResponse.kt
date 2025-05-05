package com.eldirohmanur.photogram.data.source.remote.model


import com.google.gson.annotations.SerializedName

data class ArtworksResponse(
    @SerializedName("data") val data: List<ArtworkDataResponse>,
    @SerializedName("pagination") val pagination: PaginationResponse
)