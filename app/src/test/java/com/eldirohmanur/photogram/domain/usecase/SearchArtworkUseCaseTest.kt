package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.data.repo.ArtworkRepoImpl
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
class SearchArtworkUseCaseTest {
    private lateinit var artworkDomain1: ArtworkDomain
    private lateinit var repo: ArtworkRepoImpl

    // Test dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repo = mock()
        artworkDomain1 = ArtworkDomain()
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `search artwork`() = runTest {
        whenever(repo.searchArtworks("abc", 1, 20)).thenReturn(
            Result.success(
                listOf(
                    artworkDomain1
                )
            )
        )

        val caller = SearchArtworksUseCase(repo).invoke("abc")
        assertEquals(true, caller.isSuccess)
        assertEquals(listOf(artworkDomain1), caller.getOrNull())
        verify(repo).searchArtworks("abc", 1, 20)
    }
}