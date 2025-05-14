package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.data.repo.SavedArtworkRepoImpl
import com.eldirohmanur.photogram.domain.ArtworkMapperDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDetailDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class SaveArtworkUseCaseTest {
    private val savedArtworksUseCase by lazy {
        SaveArtworkUseCase(repo, domainMapper)
    }
    private lateinit var repo: SavedArtworkRepoImpl
    private lateinit var artworkDomain1: ArtworkDomain
    private lateinit var artworkDetail1: ArtworkDetailDomain
    private lateinit var artworkUi1: ArtworkUiModel
    private lateinit var domainMapper: ArtworkMapperDomain

    // Test dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repo = mock()
        domainMapper = mock()

        artworkDomain1 = ArtworkDomain()
        artworkDetail1 = ArtworkDetailDomain()
        artworkUi1 = ArtworkUiModel()

        whenever(domainMapper.toArtworkDomain(artworkDetail1, true)).thenReturn(artworkDomain1)
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke by artwork ui`() = runTest {
        whenever(repo.saveArtwork(artworkDomain1)).thenReturn(Unit)
        savedArtworksUseCase(artworkDomain1)

        verify(repo).saveArtwork(artworkDomain1)
    }

    @Test
    fun `invoke by artwork detail`() = runTest {
        whenever(repo.saveArtwork(artworkDomain1)).thenReturn(Unit)
        savedArtworksUseCase(artworkDetail1)

        verify(repo).saveArtwork(artworkDomain1)
    }

    @Test
    fun `invoke by artwork domain`() = runTest {
        whenever(repo.saveArtwork(artworkDomain1)).thenReturn(Unit)
        savedArtworksUseCase(artworkDomain1)

        verify(repo).saveArtwork(artworkDomain1)
    }
}