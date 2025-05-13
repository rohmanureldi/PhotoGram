package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.domain.model.ArtworkDomain
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
class FetchArtworkUseCaseTest {
    private lateinit var fetchArtworkUseCase: FetchArtworksUseCase
    private lateinit var artworkDomain1: ArtworkDomain

    // Test dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fetchArtworkUseCase = mock()
        artworkDomain1 = ArtworkDomain(id = 1, title = "Artwork 1", "", "", "", "", "", false, "")
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `test invoke`() = runTest {
        whenever(fetchArtworkUseCase.invoke(1)).thenReturn(listOf(artworkDomain1))
        val response = fetchArtworkUseCase(1)

        assertEquals(listOf(artworkDomain1), response)
        verify(fetchArtworkUseCase).invoke(1)
    }
}