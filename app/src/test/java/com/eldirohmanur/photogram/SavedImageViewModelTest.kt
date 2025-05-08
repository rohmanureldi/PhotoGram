package com.eldirohmanur.photogram

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.usecase.DeleteSavedArtworkUseCase
import com.eldirohmanur.photogram.domain.usecase.GetSavedArtworksUseCase
import com.eldirohmanur.photogram.presentation.mapper.toArtworkUI
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel
import com.eldirohmanur.photogram.presentation.saved.SavedImagesViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class SavedImageViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getSavedArtworksUseCase: GetSavedArtworksUseCase
    private lateinit var removeSavedArtworkUseCase: DeleteSavedArtworkUseCase
    private lateinit var viewModel: SavedImagesViewModel

    // Test data
    private val mockArtworkDomain1 = mockk<ArtworkDomain>(relaxed = true)
    private val mockArtworkDomain2 = mockk<ArtworkDomain>(relaxed = true)
    private val mockArtworkUI1 = mockk<ArtworkUiModel>(relaxed = true)
    private val mockArtworkUI2 = mockk<ArtworkUiModel>(relaxed = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getSavedArtworksUseCase = mockk()
        removeSavedArtworkUseCase = mockk()

        // Mock the toArtworkUI extension function for domain objects
        coEvery { mockArtworkDomain1.toArtworkUI() } returns mockArtworkUI1
        coEvery { mockArtworkDomain2.toArtworkUI() } returns mockArtworkUI2
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load saved artworks`() = runTest {
        // Given
        val artworkList = listOf(mockArtworkDomain1, mockArtworkDomain2)
        coEvery { getSavedArtworksUseCase() } returns flowOf(artworkList)

        // When
        viewModel = SavedImagesViewModel(getSavedArtworksUseCase, removeSavedArtworkUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val emission = awaitItem()
            assertEquals(listOf(mockArtworkUI1, mockArtworkUI2), emission.savedArtworks)
            assertFalse(emission.isLoading)
        }
    }

    @Test
    fun `init should handle error when loading saved artworks`() = runTest {
        // Given
        coEvery { getSavedArtworksUseCase() } throws IOException("Network error")

        // When
        viewModel = SavedImagesViewModel(getSavedArtworksUseCase, removeSavedArtworkUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val emission = awaitItem()
            assertEquals(emptyList<ArtworkUiModel>(), emission.savedArtworks)
            assertFalse(emission.isLoading)
        }
    }

    @Test
    fun `removeFromSaved should call removeSavedArtworkUseCase`() = runTest {
        // Given
        val artworkId = 123
        val artworkList = listOf(mockArtworkDomain1, mockArtworkDomain2)
        coEvery { getSavedArtworksUseCase() } returns flowOf(artworkList)
        coEvery { removeSavedArtworkUseCase(artworkId) } returns Unit

        // When
        viewModel = SavedImagesViewModel(getSavedArtworksUseCase, removeSavedArtworkUseCase)
        viewModel.removeFromSaved(artworkId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { removeSavedArtworkUseCase(artworkId) }
    }

    @Test
    fun `removeFromSaved should handle error`() = runTest {
        // Given
        val artworkId = 123
        val artworkList = listOf(mockArtworkDomain1, mockArtworkDomain2)
        coEvery { getSavedArtworksUseCase() } returns flowOf(artworkList)
        coEvery { removeSavedArtworkUseCase(artworkId) } throws IOException("Network error")

        // When
        viewModel = SavedImagesViewModel(getSavedArtworksUseCase, removeSavedArtworkUseCase)
        viewModel.removeFromSaved(artworkId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { removeSavedArtworkUseCase(artworkId) }
        // No state change expected since the Flow will handle updates automatically
    }

    @Test
    fun `state should reflect loading state during initialization`() = runTest {
        // Given
        coEvery { getSavedArtworksUseCase() } returns flowOf(emptyList())

        // When
        viewModel = SavedImagesViewModel(getSavedArtworksUseCase, removeSavedArtworkUseCase)

        // Then - test the initial loading state
        viewModel.state.test {
            val initialState = awaitItem()
            assertEquals(true, initialState.isLoading)

            // Advance time to complete loading
            testDispatcher.scheduler.advanceUntilIdle()

            val loadedState = awaitItem()
            assertEquals(false, loadedState.isLoading)
        }
    }
}
