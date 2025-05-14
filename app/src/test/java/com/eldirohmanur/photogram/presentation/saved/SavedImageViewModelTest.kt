package com.eldirohmanur.photogram.presentation.saved

import app.cash.turbine.test
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.usecase.DeleteSavedArtworkUseCase
import com.eldirohmanur.photogram.domain.usecase.GetSavedArtworksUseCase
import com.eldirohmanur.photogram.presentation.mapper.ArtworkMapperUi
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel
import com.eldirohmanur.photogram.utils.Dispatch
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
class SavedImageViewModelTest {

    // Mocks
    private lateinit var getSavedArtworksUseCase: GetSavedArtworksUseCase
    private lateinit var removeSavedArtworkUseCase: DeleteSavedArtworkUseCase
    private lateinit var artworkDomains: ArtworkDomain
    private lateinit var artworkUis: ArtworkUiModel
    private lateinit var uiMapper: ArtworkMapperUi
    private lateinit var dispatch: Dispatch
    private val viewModel: SavedImagesViewModel by lazy {
        SavedImagesViewModel(
            getSavedArtworksUseCase = getSavedArtworksUseCase,
            removeSavedArtworkUseCase = removeSavedArtworkUseCase,
            mapper = uiMapper,
            dispatch = dispatch
        )
    }

    // Test dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Set the main dispatcher to our test dispatcher
        Dispatchers.setMain(testDispatcher)

        dispatch = mock()
        uiMapper = mock()
        getSavedArtworksUseCase = mock()
        removeSavedArtworkUseCase = mock()
        artworkDomains = ArtworkDomain(
            id = 1,
            title = "Artwork 1",
            artistName = "",
            dateDisplay = "",
            mediumDisplay = "",
            imageId = "",
            description = "",
            isSaved = false,
            thumbnailUrl = ""
        )

        artworkUis = ArtworkUiModel(
            id = 1,
            title = "Artwork 1",
            artist = "",
            date = "",
            imageUrl = "",
            thumbnailUrl = "",
            description = "",
            imageId = "",
            isSaved = false
        )


        whenever(dispatch.default).thenReturn(testDispatcher)
        whenever(dispatch.io).thenReturn(testDispatcher)
        whenever(dispatch.main).thenReturn(testDispatcher)
        whenever(uiMapper.toArtworkUI(artworkDomains)).thenReturn(artworkUis)

    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load saved artworks and update state`() = runTest {
        // Given
        val expectedUiArtworks = listOf(artworkUis)
        whenever(getSavedArtworksUseCase()).thenReturn(flowOf(listOf(artworkDomains)))

        // When
        viewModel // warm initiate
        testDispatcher.scheduler.advanceUntilIdle() // Process all pending coroutines
        // Then
        viewModel.state.test {
            val currentState = awaitItem()
            assertEquals(false, currentState.isLoading)
            assertEquals(expectedUiArtworks, currentState.savedArtworks)
        }
        verify(getSavedArtworksUseCase).invoke()
//        verify(uiMapper).toArtworkUI(artworkDomains)
    }

    @Test
    fun `init should handle error when loading saved artworks`() = runTest {
        // Given
        whenever(getSavedArtworksUseCase()).thenThrow(RuntimeException("Network error"))

        // When
        viewModel // wam initiate
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


        // When
        viewModel.removeFromSaved(artworkId)
        testDispatcher.scheduler.advanceUntilIdle() // Process all pending coroutines

        // Then
        verify(removeSavedArtworkUseCase).invoke(artworkId)
    }

    @Test
    fun `removeFromSaved should handle error gracefully`() = runTest {
        // Given
        val artworkId = 1
        whenever(getSavedArtworksUseCase()).thenReturn(flowOf(emptyList()))
        whenever(removeSavedArtworkUseCase.invoke(artworkId)).thenThrow(RuntimeException("Delete error"))

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