package com.eldirohmanur.photogram.data.source.remote.model

import com.google.gson.annotations.SerializedName


data class PaginationResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("offset") val offset: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("current_page") val currentPage: Int
)
