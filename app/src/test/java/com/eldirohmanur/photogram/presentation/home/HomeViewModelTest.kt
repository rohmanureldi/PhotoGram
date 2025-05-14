package com.eldirohmanur.photogram.presentation.home

import app.cash.turbine.test
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.domain.usecase.FetchArtworksUseCase
import com.eldirohmanur.photogram.domain.usecase.SearchArtworksUseCase
import com.eldirohmanur.photogram.presentation.mapper.ArtworkMapperUi
import com.eldirohmanur.photogram.presentation.model.ArtworkUiModel
import com.eldirohmanur.photogram.utils.Dispatch
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
import org.mockito.kotlin.calls
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val viewModel: HomeViewModel by lazy {
        HomeViewModel(
            getArtworksUseCase = getArtworksUseCase,
            searchArtworksUseCase = searchArtworksUseCase,
            mapper = uiMapper,
            dispatch = dispatch
        )
    }
    private lateinit var getArtworksUseCase: FetchArtworksUseCase
    private lateinit var searchArtworksUseCase: SearchArtworksUseCase
    private lateinit var uiMapper: ArtworkMapperUi
    private lateinit var dispatch: Dispatch

    @Before
    fun setup() {
        // Set the main dispatcher to our test dispatcher
        Dispatchers.setMain(testDispatcher)

        dispatch = mock()
        uiMapper = mock()
        whenever(dispatch.default).thenReturn(testDispatcher)
        whenever(dispatch.io).thenReturn(testDispatcher)
        whenever(dispatch.main).thenReturn(testDispatcher)

        // Initialize mocks
        getArtworksUseCase = mock()
        searchArtworksUseCase = mock()
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }


    @Test
    fun `init should fetch artworks`() = runTest {
        // Given
        val currentPage = 1
        val artworks = listOf(
            ArtworkDomain(id = 1, title = "Artwork 1", "", "", "", "", "", false, ""),
            ArtworkDomain(id = 2, title = "Artwork 2", "", "", "", "", "", false, ""),
        )
        val expected = artworks.map { uiMapper.toArtworkUI(it) }

        whenever(getArtworksUseCase(currentPage)).thenReturn(artworks)

        viewModel.state.test {
            awaitItem()
            val initialState = awaitItem()

            assertEquals(true, initialState.isLoading)
            assertEquals(false, initialState.isLoadMore)
            assertEquals(listOf<ArtworkUiModel>(), listOf<ArtworkUiModel>())

            val currentState = awaitItem()
            assertEquals(false, currentState.isLoading)
            assertEquals(false, currentState.isLoadMore)
            assertEquals(expected, currentState.artworks)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `on query change should fetch loadArtwork if query is empty`() = runTest {
        viewModel.onSearchQueryChange("")
        testDispatcher.scheduler.advanceUntilIdle()

        verify(getArtworksUseCase).invoke(1)

    }

    @Test
    fun `on query change should search artwork if query is NOT empty`() = runTest {
        val query = "abc"
        viewModel.onSearchQueryChange(query)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertEquals(query, awaitItem().searchQuery)
        }

        verify(searchArtworksUseCase).invoke(query, 1)
    }

    @Test
    fun `clear search should fetch default artworks`() = runTest {
        val query = "abc"
        viewModel.onSearchQueryChange(query)

        viewModel.state.test {
            awaitItem()
            viewModel.clearSearch()
            val currentState = awaitItem()
            assertEquals("", currentState.searchQuery)
        }
        testDispatcher.scheduler.advanceUntilIdle()
        verify(getArtworksUseCase).invoke(1)
    }

    @Test
    fun `isLoading initial state`() = runTest {
        viewModel.state.test {
            awaitItem() //initial state
            assertEquals(true, awaitItem().isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isLoadMore state on loadArtwork()`() = runTest {
        val loadSecondPage = 2
        viewModel.state.test {
            awaitItem() //initial state

            viewModel.loadArtworks() // first load
            awaitItem() // first load loading
            awaitItem()  // first load response

            viewModel.loadArtworks()  // second load
            val currentState = awaitItem()  // second load loading
            assertEquals(false, currentState.isLoading)
            assertEquals(true, currentState.isLoadMore)
            cancelAndIgnoreRemainingEvents()
        }
        verify(getArtworksUseCase).invoke(loadSecondPage)
    }

    @Test
    fun `isLoadMore state on searchArtwork()`() = runTest {
        val loadFirstPage = 1
        val loadSecondPage = 2
        val query = "abc"

        val artworks = listOf(
            ArtworkDomain(id = 1, title = "Artwork 1", "", "", "", "", "", false, ""),
            ArtworkDomain(id = 2, title = "Artwork 2", "", "", "", "", "", false, ""),
        )

        whenever(searchArtworksUseCase(query)).thenReturn(Result.success(artworks))

        viewModel.onSearchQueryChange(query) // first load

        viewModel.state.test {
            awaitItem()
            awaitItem()
            awaitItem()
            awaitItem()
            awaitItem()
            viewModel.loadArtworks()
            val state = awaitItem()
            assertEquals(true, state.isLoadMore)
            cancelAndIgnoreRemainingEvents()
        }

        verify(searchArtworksUseCase).invoke(query, loadFirstPage)
        verify(searchArtworksUseCase).invoke(query, loadSecondPage)

    }

    @Test
    fun `searchArtwork should do nothing if query is empty`() = runTest {
        viewModel.searchArtworks()
        verifyNoInteractions(searchArtworksUseCase)
    }

    @Test
    fun `searchArtwork should do nothing if isLoading is true`() = runTest {
        val query = "abc"

        val artworks = listOf(
            ArtworkDomain(id = 1, title = "Artwork 1", "", "", "", "", "", false, ""),
            ArtworkDomain(id = 2, title = "Artwork 2", "", "", "", "", "", false, ""),
        )

        whenever(searchArtworksUseCase(query)).thenReturn(Result.success(artworks))

        viewModel.onSearchQueryChange(query) // first load

        viewModel.state.test {
            awaitItem()
            val state = awaitItem()
            viewModel.onSearchQueryChange(query)
            assertEquals(true, state.isLoading)
            verify(searchArtworksUseCase, calls(1))
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `searchArtwork should do nothing if isLoadMore is true`() = runTest {
        val query = "abc"

        val artworks = listOf(
            ArtworkDomain(id = 1, title = "Artwork 1", "", "", "", "", "", false, ""),
            ArtworkDomain(id = 2, title = "Artwork 2", "", "", "", "", "", false, ""),
        )

        whenever(searchArtworksUseCase(query)).thenReturn(Result.success(artworks))

        viewModel.onSearchQueryChange(query) // first load

        viewModel.state.test {
            awaitItem()
            awaitItem()
            awaitItem()
            awaitItem()
            awaitItem()
            viewModel.loadArtworks()
            val state = awaitItem()
            assertEquals(true, state.isLoadMore)


            viewModel.loadArtworks()
            awaitItem()
            val lastState = awaitItem()
            assertEquals(true, lastState.isLoadMore)

            verify(searchArtworksUseCase, times(2)).invoke(query, 2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init should handle error when loading artworks`() = runTest {
        // Given
        val errorMsg = "Network error"
        whenever(getArtworksUseCase(1)).thenThrow(RuntimeException(errorMsg))

        // When
        viewModel
        testDispatcher.scheduler.advanceUntilIdle() // Process all pending coroutines

        // Then
        viewModel.state.test {
            val currentState = awaitItem()
            assertEquals(false, currentState.isLoading)
            assertEquals(false, currentState.isLoadMore)
            assertEquals(emptyList<ArtworkUiModel>(), emptyList<ArtworkUiModel>())
            assertEquals(true, !currentState.error.isNullOrEmpty())
        }
    }

    @Test
    fun `searchArtwork should handle error`() = runTest {
        // Given
        val errorMsg = "Network error"
        whenever(searchArtworksUseCase("abc")).thenThrow(RuntimeException(errorMsg))

        // When
        viewModel.onSearchQueryChange("abc")
        testDispatcher.scheduler.advanceUntilIdle() // Process all pending coroutines

        // Then
        viewModel.state.test {
            val currentState = awaitItem()
            assertEquals(false, currentState.isLoading)
            assertEquals(false, currentState.isLoadMore)
            assertEquals(emptyList<ArtworkUiModel>(), emptyList<ArtworkUiModel>())
            assertEquals(true, !currentState.error.isNullOrEmpty())
            verify(searchArtworksUseCase).invoke("abc")
        }
    }
}