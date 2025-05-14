package com.eldirohmanur.photogram.presentation.detail

import app.cash.turbine.test
import com.eldirohmanur.photogram.domain.ArtworkMapperDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.usecase.DeleteSavedArtworkUseCase
import com.eldirohmanur.photogram.domain.usecase.FetchArtworkDetailUseCase
import com.eldirohmanur.photogram.domain.usecase.GetSavedArtworksUseCase
import com.eldirohmanur.photogram.domain.usecase.SaveArtworkUseCase
import com.eldirohmanur.photogram.presentation.mapper.ArtworkMapperUi
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

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    // Mocks
    private lateinit var getSavedArtworksUseCase: GetSavedArtworksUseCase
    private lateinit var removeSavedArtworkUseCase: DeleteSavedArtworkUseCase
    private lateinit var getArtworkByIdUseCase: GetSavedArtworksUseCase
    private lateinit var fetchArtworkDetailUseCase: FetchArtworkDetailUseCase
    private lateinit var saveArtworkUseCase: SaveArtworkUseCase
    private val viewModel: DetailViewModel by lazy {
        DetailViewModel(
            getArtworkByIdUseCase = getArtworkByIdUseCase,
            fetchArtworkDetailUseCase = fetchArtworkDetailUseCase,
            saveArtworkUseCase = saveArtworkUseCase,
            removeSavedArtworkUseCase = removeSavedArtworkUseCase,
            mapper = uiMapper,
            domainMapper = domainMapper
        )
    }
    private lateinit var artworkDomain1: ArtworkDomain
    private lateinit var artworkUiModel1: ArtworkUiModel
    private lateinit var uiMapper: ArtworkMapperUi
    private lateinit var domainMapper: ArtworkMapperDomain

    // Test dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Set the main dispatcher to our test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Initialize mocks
        getSavedArtworksUseCase = mock()
        removeSavedArtworkUseCase = mock()
        getArtworkByIdUseCase = mock()
        fetchArtworkDetailUseCase = mock()
        saveArtworkUseCase = mock()
        uiMapper = mock()
        domainMapper = mock()

        artworkDomain1 = ArtworkDomain(id = 1, title = "Artwork 1")
        artworkUiModel1 = ArtworkUiModel()

        whenever(uiMapper.toArtworkUI(artworkDomain1)).thenReturn(artworkUiModel1)
        whenever(domainMapper.toArtworkDomain(artworkUiModel1, true)).thenReturn(artworkDomain1)
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `loadArtwork by id from local db`() = runTest {
        whenever(getArtworkByIdUseCase.invoke(1)).thenReturn(artworkDomain1)

        viewModel.loadArtwork(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(artworkUiModel1, state.artwork)
            verify(getArtworkByIdUseCase).invoke(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadArtwork by id from network fetch`() = runTest {
        whenever(getArtworkByIdUseCase.invoke(1)).thenReturn(null)
        whenever(fetchArtworkDetailUseCase.invoke(1)).thenReturn(artworkDomain1)

        viewModel.loadArtwork(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(artworkUiModel1, state.artwork)
            verify(getArtworkByIdUseCase).invoke(1)
            verify(fetchArtworkDetailUseCase).invoke(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadArtwork loading state`() = runTest {
        whenever(getArtworkByIdUseCase.invoke(1)).thenReturn(artworkDomain1)

        viewModel.loadArtwork(1)

        viewModel.state.test {
            awaitItem() // initial state
            val state = awaitItem()
            assertEquals(true, state.isLoading)
            verify(getArtworkByIdUseCase).invoke(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadArtwork error state`() = runTest {
        whenever(getArtworkByIdUseCase.invoke(1)).thenThrow(RuntimeException("Network error"))

        viewModel.loadArtwork(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(false, state.isLoading)
            assertEquals(true, !state.error.isNullOrEmpty())
            verify(getArtworkByIdUseCase).invoke(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `save artwork`() = runTest {
        whenever(getArtworkByIdUseCase.invoke(1)).thenReturn(artworkDomain1)
        whenever(saveArtworkUseCase.invoke(artworkDomain1)).thenReturn(Unit)

        viewModel.loadArtwork(1)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.saveArtwork()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(true, state.artwork?.isSaved)
            verify(getArtworkByIdUseCase).invoke(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `save artwork error`() = runTest {
        whenever(getArtworkByIdUseCase.invoke(1)).thenReturn(artworkDomain1)
        whenever(saveArtworkUseCase.invoke(artworkDomain1)).thenThrow(RuntimeException("Network error"))

        viewModel.loadArtwork(1)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.saveArtwork()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(true, !state.error.isNullOrEmpty())
            verify(getArtworkByIdUseCase).invoke(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `remove artwork`() = runTest {
        whenever(getArtworkByIdUseCase.invoke(1)).thenReturn(artworkDomain1)
        whenever(removeSavedArtworkUseCase.invoke(1)).thenReturn(Unit)

        viewModel.loadArtwork(1)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.removeFromSaved(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(false, state.artwork?.isSaved)
            verify(removeSavedArtworkUseCase).invoke(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `remove artwork error`() = runTest {
        whenever(getArtworkByIdUseCase.invoke(1)).thenReturn(artworkDomain1)
        whenever(removeSavedArtworkUseCase.invoke(1)).thenThrow(RuntimeException("Network error"))

        viewModel.loadArtwork(1)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.removeFromSaved(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(true, !state.error.isNullOrEmpty())
            verify(removeSavedArtworkUseCase).invoke(1)
            cancelAndIgnoreRemainingEvents()
        }
    }
}