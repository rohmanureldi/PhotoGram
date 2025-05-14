package com.eldirohmanur.photogram.data.repo

import com.eldirohmanur.photogram.data.mapper.ArtworkMapperData
import com.eldirohmanur.photogram.data.source.local.entity.SavedArtworkEntity
import com.eldirohmanur.photogram.data.source.remote.model.ArtworkDataResponse
import com.eldirohmanur.photogram.data.source.remote.model.ArtworkDetailDataResponse
import com.eldirohmanur.photogram.data.source.remote.model.ArtworkDetailResponse
import com.eldirohmanur.photogram.data.source.remote.model.ArtworksResponse
import com.eldirohmanur.photogram.data.source.remote.model.PaginationResponse
import com.eldirohmanur.photogram.data.source.remote.service.ArtInstituteApi
import com.eldirohmanur.photogram.domain.model.ArtworkDetailDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.utils.Dispatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class ArtworkRepoImplTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dispatcher: Dispatch
    private lateinit var artworkDomain1: ArtworkDomain
    private lateinit var artworkDomain2: ArtworkDomain
    private lateinit var artworkEntity1: SavedArtworkEntity
    private lateinit var artworkResponse: ArtworksResponse

    private lateinit var artworkDetailResponse: ArtworkDetailResponse
    private lateinit var artworkDetailDataResponse: ArtworkDetailDataResponse
    private lateinit var artworkDetailDomain: ArtworkDetailDomain

    private lateinit var artworkDataResponse1: ArtworkDataResponse
    private lateinit var artworkDataResponse2: ArtworkDataResponse
    private lateinit var paginationResponse: PaginationResponse

    private lateinit var mapper: ArtworkMapperData
    private lateinit var api: ArtInstituteApi
    private lateinit var savedArtworkRepo: SavedArtworkRepoImpl
    private val repo: ArtworkRepoImpl by lazy {
        ArtworkRepoImpl(
            api = api,
            savedArtworkRepository = savedArtworkRepo,
            dispatcher = dispatcher,
            mapper = mapper
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        dispatcher = mock()
        mapper = mock()
        api = mock()
        savedArtworkRepo = mock()

        artworkDomain1 = ArtworkDomain(id = 1, title = "Artwork 1")
        artworkDomain2 = ArtworkDomain(id = 2, title = "Artwork 1")
        artworkEntity1 = SavedArtworkEntity(
            id = 1,
            title = "Artwork 1",
            artistName = "",
            dateDisplay = "",
            mediumDisplay = "",
            imageId = "",
            description = "",
            savedAt = 0
        )
        artworkDataResponse1 = ArtworkDataResponse(
            1,
            "", "", "", "", "", null, ""
        )
        artworkDataResponse2 = ArtworkDataResponse(
            2,
            "", "", "", "", "", null, ""
        )
        paginationResponse = PaginationResponse(1, 1, 1, 1, 1)
        artworkResponse = ArtworksResponse(
            data = listOf(
                artworkDataResponse1, artworkDataResponse2
            ),
            pagination = paginationResponse
        )
        artworkDetailDataResponse = ArtworkDetailDataResponse(1)
        artworkDetailResponse = ArtworkDetailResponse(artworkDetailDataResponse)
        artworkDetailDomain = ArtworkDetailDomain(1)

        whenever(dispatcher.default).thenReturn(testDispatcher)
        whenever(dispatcher.io).thenReturn(testDispatcher)
        whenever(dispatcher.main).thenReturn(testDispatcher)
        whenever(mapper.toArtworkEntity(artworkDomain1)).thenReturn(artworkEntity1)
        whenever(mapper.toArtwork(artworkEntity1)).thenReturn(artworkDomain1)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `getArtworks() should return mapped and saved status artworks`() = runTest {
        // Given
        val dtoList = artworkResponse
        val domainList = listOf(artworkDomain1, artworkDomain2)

        whenever(api.getArtworks(1, 20)).thenReturn(artworkResponse)
        whenever(mapper.toArtwork(dtoList.data[0])).thenReturn(domainList[0])
        whenever(mapper.toArtwork(dtoList.data[1])).thenReturn(domainList[1])
        whenever(savedArtworkRepo.isArtworkSaved(1)).thenReturn(true)
        whenever(savedArtworkRepo.isArtworkSaved(2)).thenReturn(false)

        // When
        val result = repo.getArtworks(1, 20)

        // Then
        assertTrue(result.isSuccess)
        val artworks = result.getOrNull()!!
        assertEquals(2, artworks.size)
        assertTrue(artworks[0].isSaved)
        assertFalse(artworks[1].isSaved)
    }

    @Test
    fun `getArtworkDetail() should return mapped detail with saved status`() = runTest {
        val id = 1
        val dto = artworkDetailDataResponse
        val domain = artworkDetailDomain

        whenever(api.getArtworkDetail(id)).thenReturn(artworkDetailResponse)
        whenever(mapper.toArtworkDetail(dto)).thenReturn(domain)
        whenever(savedArtworkRepo.isArtworkSaved(id)).thenReturn(true)

        val result = repo.getArtworkDetail(id)

        assertTrue(result.isSuccess)
        val artwork = result.getOrNull()!!
        assertEquals(id, artwork.id)
        assertTrue(artwork.isSaved)
    }

    @Test
    fun `searchArtworks() should return mapped artworks with saved status`() = runTest {
        val query = "mona"
        val dtoList = artworkResponse
        val mapped1 = artworkDomain1
        val mapped2 = artworkDomain2

        whenever(api.searchArtworks(query)).thenReturn(artworkResponse)
        whenever(savedArtworkRepo.isArtworkSaved(1)).thenReturn(true)
        whenever(savedArtworkRepo.isArtworkSaved(2)).thenReturn(false)
        whenever(mapper.toArtwork(dtoList.data[0], true)).thenReturn(mapped1)
        whenever(mapper.toArtwork(dtoList.data[1], false)).thenReturn(mapped2)

        val result = repo.searchArtworks(query, 1, 20)

        assertTrue(result.isSuccess)
        val artworks = result.getOrNull()!!
        assertEquals(listOf(mapped1, mapped2), artworks)
    }

    @Test
    fun `getArtworks() should return failure on exception`() = runTest {
        whenever(api.getArtworks(1, 20)).thenThrow(RuntimeException("API Error"))

        val result = repo.getArtworks(1, 20)

        assertTrue(result.isFailure)
        assertEquals("API Error", result.exceptionOrNull()?.message)
    }
}