package com.eldirohmanur.photogram.domain.usecase

import com.eldirohmanur.photogram.domain.repo.SavedArtworkRepo
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
class DeleteSavedArtworkUseCaseTest {
    private val removeSavedArtworkUseCase by lazy {
        DeleteSavedArtworkUseCase(repo)
    }
    private lateinit var repo: SavedArtworkRepo

    // Test dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repo = mock()
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `test invoke`() = runTest {
        whenever(repo.deleteArtwork(1)).thenReturn(Unit)
        removeSavedArtworkUseCase(1)

        verify(repo).deleteArtwork(1)
    }
}