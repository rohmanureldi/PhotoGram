package com.eldirohmanur.photogram.domain.usecase

import app.cash.turbine.test
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class GetSavedArtworkUseCaseTest {
    private lateinit var getSavedArtworksUseCase: GetSavedArtworksUseCase
    private lateinit var artworkDomain1: ArtworkDomain

    // Test dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getSavedArtworksUseCase = mock()
        artworkDomain1 = ArtworkDomain(id = 1, title = "Artwork 1", "", "", "", "", "", false, "")
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `test fetch without id`() = runTest {
        whenever(getSavedArtworksUseCase.invoke()).thenReturn(flowOf(listOf(artworkDomain1)))
        val caller = getSavedArtworksUseCase()

        caller.test {
            val response = awaitItem()
            assertEquals(listOf(artworkDomain1), response)
            cancelAndIgnoreRemainingEvents()
        }
        verify(getSavedArtworksUseCase).invoke()
    }

    @Test
    fun `fetch with id`() = runTest {
        whenever(getSavedArtworksUseCase.invoke(1)).thenReturn(artworkDomain1)
        val response = getSavedArtworksUseCase(1)

        assertEquals(artworkDomain1, response)
        verify(getSavedArtworksUseCase).invoke(1)
    }
}