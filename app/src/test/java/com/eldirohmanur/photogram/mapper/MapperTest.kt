package com.eldirohmanur.photogram.mapper

import com.eldirohmanur.photogram.data.mapper.ArtworkMapperData
import com.eldirohmanur.photogram.data.source.local.entity.SavedArtworkEntity
import com.eldirohmanur.photogram.data.source.remote.model.ArtworkDataResponse
import com.eldirohmanur.photogram.data.source.remote.model.ArtworkDetailDataResponse
import com.eldirohmanur.photogram.domain.ArtworkMapperDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDetailDomain
import com.eldirohmanur.photogram.domain.model.ArtworkDomain
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapperTest {
    private val testDispatcher = StandardTestDispatcher()
    private val domain = ArtworkDomain()
    private val dataResponse = ArtworkDataResponse(
        id = 0,
        title = "",
        artistName = "",
        dateDisplay = "",
        mediumDisplay = "",
        imageId = "",
        thumbnail = null,
        description = ""
    )
    private val detailDataResponse = ArtworkDetailDataResponse(
        id = 0,
        title = "",
    )
    private val entity = SavedArtworkEntity(
        id = 0,
        title = "",
        artistName = "",
        dateDisplay = "",
        mediumDisplay = "",
        imageId = "",
        description = "",
        savedAt = 0
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun uiMapperTest() = runTest {
        val mapped = ArtworkMapperUi.toArtworkUI(domain)
        val expected = ArtworkUiModel(
            imageUrl = "https://www.artic.edu/iiif/2/${domain.imageId}/full/843,/0/default.jpg"
        )
        assertEquals(
            expected, mapped
        )
    }

    @Test
    fun dataMapperTest() = runTest {
        val mapped = ArtworkMapperData.toArtwork(dataResponse)
        val expected = domain
        assertEquals(expected, mapped)

        val mappedSaved = ArtworkMapperData.toArtwork(dataResponse, true)
        val expectedSaved = domain.copy(isSaved = true)
        assertEquals(expectedSaved, mappedSaved)
        assertTrue(mappedSaved.isSaved)

        val mapped1 = ArtworkMapperData.toArtwork(entity = entity)
        val expected1 = domain.copy(isSaved = true)
        assertEquals(expected1, mapped1)
        assertTrue(mapped1.isSaved)

        val mapped2 = ArtworkMapperData.toArtworkDetail(response = detailDataResponse)
        val expected2 = ArtworkDetailDomain()
        assertEquals(expected2, mapped2)

        val mapped3 = ArtworkMapperData.toArtworkEntity(domain).copy(savedAt = 0)
        val expected3 = entity
        assertEquals(expected3, mapped3)
    }

    @Test
    fun domainMapperTest() = runTest {
        val mapped = ArtworkMapperDomain.toArtworkDomain(
            ArtworkUiModel(), false
        )
        val expected = domain
        assertEquals(expected, mapped)
        assertFalse(mapped.isSaved)

        val mapped1 = ArtworkMapperDomain.toArtworkDomain(
            ArtworkUiModel(), true
        )
        val expected1 = domain.copy(isSaved = true)
        assertEquals(expected1, mapped1)
        assertTrue(mapped1.isSaved)

        val mapped2 = ArtworkMapperDomain.toArtworkDomain(
            ArtworkDetailDomain(), false
        )
        val expected2 = domain
        assertEquals(expected2, mapped2)
        assertFalse(mapped2.isSaved)

        val mapped2Saved = ArtworkMapperDomain.toArtworkDomain(
            ArtworkDetailDomain(), true
        )
        val expected2Saved = domain.copy(isSaved = true)
        assertEquals(expected2Saved, mapped2Saved)
        assertTrue(mapped2Saved.isSaved)

    }
}