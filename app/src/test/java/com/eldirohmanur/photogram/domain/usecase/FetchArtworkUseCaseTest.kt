package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.repo.ArtworkRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class FetchArtworkUseCaseTest {
    private lateinit var repo: ArtworkRepo
    private val fetchArtworkUseCase by lazy {
        FetchArtworksUseCase(repo)
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
    fun `test invoke`() = runTest {
        whenever(repo.getArtworks(1, 20)).thenReturn(Result.success(listOf(artworkDomain1)))
        val response = fetchArtworkUseCase(1)

        assertEquals(listOf(artworkDomain1), response)
        verify(repo).getArtworks(1, 20)
    }

    @Test
    fun `invoke() should return null when repository returns failure`() = runTest {
        // Given
        val page = 1
        val limit = 20
        whenever(repo.getArtworks(page, limit)).thenReturn(Result.failure(Exception("Error")))

        // When
        val result = fetchArtworkUseCase.invoke(page, limit)

        // Then
        assertNull(result)
        verify(repo).getArtworks(page, limit)
    }
}