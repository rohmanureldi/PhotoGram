package com.eldirohmanur.photogram.domain.usecase

import app.cash.turbine.test
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.repo.SavedArtworkRepo
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
    private lateinit var repo: SavedArtworkRepo
    private val useCase by lazy {
        GetSavedArtworksUseCase(repo)
    }
    private lateinit var artworkDomain1: ArtworkDomain

    // Test dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repo = mock()
        artworkDomain1 = ArtworkDomain(id = 1, title = "Artwork 1")
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `test fetch without id`() = runTest {
        whenever(repo.getSavedArtworks()).thenReturn(flowOf(listOf(artworkDomain1)))
        val caller = useCase()

        caller.test {
            val response = awaitItem()
            assertEquals(listOf(artworkDomain1), response)
            cancelAndIgnoreRemainingEvents()
        }
        verify(repo).getSavedArtworks()
    }

    @Test
    fun `fetch with id`() = runTest {
        whenever(repo.getSavedArtworkById(1)).thenReturn(artworkDomain1)
        val response = useCase(1)

        assertEquals(artworkDomain1, response)
        verify(repo).getSavedArtworkById(1)
    }
}