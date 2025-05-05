package com.eldirohmanur.photogram.data.source.remote.model

import com.google.gson.annotations.SerializedName

data class ArtworkDetailResponse(
    @SerializedName("data") val data: ArtworkDetailDataResponse
)