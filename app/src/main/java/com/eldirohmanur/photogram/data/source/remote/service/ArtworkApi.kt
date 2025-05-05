package com.eldirohmanur.photogram.data.source.remote.service

import com.eldirohmanur.photogram.data.source.remote.model.ArtworkDetailResponse
import com.eldirohmanur.photogram.data.source.remote.model.ArtworksResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArtInstituteApi {
    @GET("artworks")
    suspend fun getArtworks(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 20
    ): ArtworksResponse

    @GET("artworks/{id}")
    suspend fun getArtworkDetail(
        @Path("id") id: Int
    ): ArtworkDetailResponse

    @GET("artworks/search")
    suspend fun searchArtworks(
        @Query("q") query: String,
        @Query("fields") field: String = "id,title,image_id",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ArtworksResponse

    companion object {
        const val BASE_URL = "https://api.artic.edu/api/v1/"
        const val IMAGE_URL = "https://www.artic.edu/iiif/2/"
    }
}