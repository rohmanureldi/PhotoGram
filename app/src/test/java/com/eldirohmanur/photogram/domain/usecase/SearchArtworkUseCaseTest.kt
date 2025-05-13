package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.data.source.remote.model.ArtworkDetailDataResponse
import com.eldirohmanur.photogram.domain.model.ArtworkDetailDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.toArtworkDetail
import com.eldirohmanur.photogram.domain.toArtworkDomain
import com.eldirohmanur.photogram.presentation.mapper.toArtworkUI
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class SearchArtworkUseCaseTest {
    private lateinit var searchArtworksUseCase: SearchArtworksUseCase
    private lateinit var artworkDomain1: ArtworkDomain
    private lateinit var artworkDetail1: ArtworkDetailDomain
    private lateinit var artworkUi1: ArtworkUiModel

    // Test dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        searchArtworksUseCase = mock()

        val artworkDetailResponse = ArtworkDetailDataResponse(id = 1, title = "Artwork 1")
        artworkDomain1 = artworkDetailResponse.toArtworkDetail().toArtworkDomain()
        artworkDetail1 = artworkDetailResponse.toArtworkDetail()
        artworkUi1 = artworkDomain1.toArtworkUI()
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke by artwork ui`() = runTest {
        whenever(searchArtworksUseCase.invoke("abc")).thenReturn(Result.success(listOf(artworkDomain1)))
        val caller = searchArtworksUseCase("abc")
        assertEquals(true, caller.isSuccess)
        assertEquals(listOf(artworkDomain1), caller.getOrNull())
        verify(searchArtworksUseCase).invoke("abc")
    }
}