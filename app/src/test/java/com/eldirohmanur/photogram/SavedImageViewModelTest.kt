package com.eldirohmanur.photogram

import app.cash.turbine.test
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.usecase.DeleteSavedArtworkUseCase
import com.eldirohmanur.photogram.domain.usecase.GetSavedArtworksUseCase
import com.eldirohmanur.photogram.presentation.mapper.toArtworkUI
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel
import com.eldirohmanur.photogram.presentation.saved.SavedImagesViewModel
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
class SavedImagesViewModelTest {

    // Mocks
    private lateinit var getSavedArtworksUseCase: GetSavedArtworksUseCase
    private lateinit var removeSavedArtworkUseCase: DeleteSavedArtworkUseCase
    private lateinit var viewModel: SavedImagesViewModel

    // Test dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Set the main dispatcher to our test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Initialize mocks
        getSavedArtworksUseCase = mock()
        removeSavedArtworkUseCase = mock()
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load saved artworks and update state`() = runTest {
        // Given
        val artworks = listOf(
            ArtworkDomain(id = 1, title = "Artwork 1", "", "", "", "", "", false, ""),
            ArtworkDomain(id = 2, title = "Artwork 2", "", "", "", "", "", false, ""),
        )
        val expectedUiArtworks = artworks.map { it.toArtworkUI() }

        whenever(getSavedArtworksUseCase()).thenReturn(flowOf(artworks))

        // When
        viewModel = SavedImagesViewModel(getSavedArtworksUseCase, removeSavedArtworkUseCase)
        testDispatcher.scheduler.advanceUntilIdle() // Process all pending coroutines

        // Then
        viewModel.state.test {
            val currentState = awaitItem()
            assertEquals(false, currentState.isLoading)
            assertEquals(expectedUiArtworks, currentState.savedArtworks)
        }
    }

    @Test
    fun `init should handle error when loading saved artworks`() = runTest {
        // Given
        whenever(getSavedArtworksUseCase()).thenThrow(RuntimeException("Network error"))

        // When
        viewModel = SavedImagesViewModel(getSavedArtworksUseCase, removeSavedArtworkUseCase)
        testDispatcher.scheduler.advanceUntilIdle() // Process all pending coroutines

        // Then
        viewModel.state.test {
            val currentState = awaitItem()
            assertEquals(false, currentState.isLoading)
            assertEquals(emptyList<ArtworkUiModel>(), currentState.savedArtworks)
        }
    }

    @Test
    fun `removeFromSaved should call remove use case with correct id`() = runTest {
        // Given
        val artworkId = 1
        whenever(getSavedArtworksUseCase()).thenReturn(flowOf(emptyList()))
        whenever(removeSavedArtworkUseCase(artworkId)).thenReturn(Unit)

        viewModel = SavedImagesViewModel(getSavedArtworksUseCase, removeSavedArtworkUseCase)

        // When
        viewModel.removeFromSaved(artworkId)
        testDispatcher.scheduler.advanceUntilIdle() // Process all pending coroutines

        // Then
        verify(removeSavedArtworkUseCase(artworkId))
    }

    @Test
    fun `removeFromSaved should handle error gracefully`() = runTest {
        // Given
        val artworkId = 1
        whenever(getSavedArtworksUseCase()).thenReturn(flowOf(emptyList()))
        whenever(removeSavedArtworkUseCase.invoke(artworkId)).thenThrow(RuntimeException("Delete error"))
        viewModel = SavedImagesViewModel(getSavedArtworksUseCase, removeSavedArtworkUseCase)

        // When
        viewModel.removeFromSaved(artworkId)
        testDispatcher.scheduler.advanceUntilIdle() // Process all pending coroutines

        // Then - Verify the app doesn't crash and state remains unchanged
        viewModel.state.test {
            val currentState = awaitItem()
            assertEquals(false, currentState.isLoading)
            assertEquals(emptyList<ArtworkUiModel>(), currentState.savedArtworks)
        }
    }
}