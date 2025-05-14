package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.domain.ArtworkMapperDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDetailDomain
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
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class FetchArtworkDetailUseCaseTest {
    private val fetchArtworkDetailUseCase by lazy {
        FetchArtworkDetailUseCase(repo, mapper)
    }
    private lateinit var artworkDomain1: ArtworkDomain
    private lateinit var artworkDetail1: ArtworkDetailDomain
    private lateinit var repo: ArtworkRepo
    private lateinit var mapper: ArtworkMapperDomain

    // Test dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repo = mock()
        mapper = mock()
        artworkDomain1 = ArtworkDomain(id = 1, title = "Artwork 1")
        artworkDetail1 = ArtworkDetailDomain(id = 1, title = "Artwork 1")

        whenever(mapper.toArtworkDomain(artworkDetail1)).thenReturn(artworkDomain1)
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `test invoke`() = runTest {
        whenever(repo.getArtworkDetail(1)).thenReturn(Result.success(artworkDetail1))
        val response = fetchArtworkDetailUseCase(1)

        assertEquals(artworkDomain1, response)
        verify(repo).getArtworkDetail(1)
    }
}