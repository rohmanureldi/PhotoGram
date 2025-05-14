package com.eldirohmanur.photogram.data.repo

import com.eldirohmanur.photogram.data.mapper.ArtworkMapperData
import com.eldirohmanur.photogram.data.source.local.SavedArtworkDao
import com.eldirohmanur.photogram.data.source.local.entity.SavedArtworkEntity
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
import com.eldirohmanur.photogram.utils.Dispatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class SavedArtworkRepoImplTest {
    private lateinit var dao: SavedArtworkDao
    private lateinit var customDispatcher: Dispatch
    private lateinit var artworkDomain1: ArtworkDomain
    private lateinit var artworkEntity1: SavedArtworkEntity
    private lateinit var mapper: ArtworkMapperData
    private val repo: SavedArtworkRepoImpl by lazy {
        SavedArtworkRepoImpl(
            dao,
            customDispatcher,
            mapper
        )
    }

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        dao = mock()
        customDispatcher = mock()
        mapper = mock()
        artworkDomain1 = ArtworkDomain(id = 1, title = "Artwork 1")
        artworkEntity1 = SavedArtworkEntity(
            id = 1,
            title = "Artwork 1",
            artistName = "",
            dateDisplay = "",
            mediumDisplay = "",
            imageId = "",
            description = "",
            savedAt = 0
        )

        whenever(customDispatcher.default).thenReturn(testDispatcher)
        whenever(customDispatcher.io).thenReturn(testDispatcher)
        whenever(customDispatcher.main).thenReturn(testDispatcher)
        whenever(mapper.toArtworkEntity(artworkDomain1)).thenReturn(artworkEntity1)
        whenever(mapper.toArtwork(artworkEntity1)).thenReturn(artworkDomain1)

    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `getSavedArtworks() should return mapped artworks`() = runTest {
        // Given
        val entities = listOf(artworkEntity1)
        val expected = entities.map { mapper.toArtwork(it) }

        whenever(dao.getAllSavedArtworks()).thenReturn(flowOf(entities))

        // When
        val result = repo.getSavedArtworks().first()
        assertEquals(expected, result)

        // Then
        verify(dao).getAllSavedArtworks()
    }

    @Test
    fun `getSavedArtworksById() should return mapped artworks`() = runTest {
        // Given
        whenever(dao.getSavedArtworkById(1)).thenReturn(artworkEntity1)

        // When
        val result = repo.getSavedArtworkById(1)
        assertEquals(artworkDomain1, result)

        // Then
        verify(dao).getSavedArtworkById(1)
        verify(mapper).toArtwork(artworkEntity1)
    }

    @Test
    fun `saveArtwork test`() = runTest {
        // Given
        whenever(dao.saveArtwork(artworkEntity1)).thenReturn(Unit)

        // When
        repo.saveArtwork(artworkDomain1)

        // Then
        verify(dao).saveArtwork(artworkEntity1)
    }
}