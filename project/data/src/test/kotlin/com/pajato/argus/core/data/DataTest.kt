package com.pajato.argus.core.data

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class DataTest {

    @Test
    fun `Exercise the Cast Attribute property`() {
        val firstStar = "Star One"
        val secondStar = "Star Two"
        val castAttribute = Cast(listOf(firstStar, secondStar))
        assertEquals(2, castAttribute.performers.size)
        assert(castAttribute.performers.contains(firstStar))
        assert(castAttribute.performers.contains(secondStar))
        assertEquals("Cast", castAttribute.key)
    }

    @Test
    fun `Exercise the Date Attribute property`() {
        val timeStamp = Date().time
        val releaseAttribute = Release(timeStamp)
        assertEquals(timeStamp, releaseAttribute.timeStamp)
        assertEquals("Release", releaseAttribute.key)
    }

    @Test
    fun `Exercise the Directors Attribute property`() {
        val firstDirector = "Director One"
        val secondDirector = "Director Two"
        val directorsAttribute = Directors(listOf(firstDirector, secondDirector))
        assertEquals(2, directorsAttribute.directors.size)
        assert(directorsAttribute.directors.contains(firstDirector))
        assert(directorsAttribute.directors.contains(secondDirector))
        assertEquals("Directors", directorsAttribute.key)
    }

    @Test
    fun `Exercise the Name Attribute property`() {
        val nameAttribute = Name("testName")
        assertEquals("testName", nameAttribute.name)
        assertEquals("Name", nameAttribute.key)
    }

    @Test
    fun `Exercise the Provider Attribute property`() {
        val providerName = "Amazon"
        val providerAttribute = Provider(providerName)
        assertEquals(providerName, providerAttribute.name)
        assertEquals("Provider", providerAttribute.key)
    }

    @Test
    fun `Exercise the Series Attribute property`() {
        val series1episode1 = Episode(mutableListOf())
        val series1episode2 = Episode(mutableListOf())
        val series2episode1 = Episode(mutableListOf())
        val series2episode2 = Episode(mutableListOf())
        val series2episode3 = Episode(mutableListOf())
        val series1 = SeriesInstance(mutableListOf(series1episode1, series1episode2))
        val series2 = SeriesInstance(mutableListOf(series2episode1, series2episode2, series2episode3))
        val seriesAttribute = Series(mutableListOf(series1, series2))
        assertEquals(2, seriesAttribute.series.size)
        assertEquals(2, seriesAttribute.series[0].episodes.size)
        assertEquals(0, seriesAttribute.series[0].episodes[0].attributes.size)
        assertEquals(0, seriesAttribute.series[0].episodes[1].attributes.size)
        assertEquals(3, seriesAttribute.series[1].episodes.size)
        assertEquals(0, seriesAttribute.series[1].episodes[0].attributes.size)
        assertEquals(0, seriesAttribute.series[1].episodes[1].attributes.size)
        assertEquals(0, seriesAttribute.series[1].episodes[2].attributes.size)
        assertEquals("Series", seriesAttribute.key)
    }

    @Test
    fun `Exercise the Type Attribute property`() {
        val typeAttribute = Type(VideoType.Movie)
        assertThat(typeAttribute.type).isEqualTo(VideoType.Movie)
        assertThat(typeAttribute.key).isEqualTo("Type")
    }

    @Test
    fun `creating video error does the right thing`() {
        val key = ErrorKey.Ok
        val error = VideoError(key)
        assertEquals(key, error.key)
        assertEquals("", error.message)
    }

    @Test
    fun `Core video has a correct id and no attributes`() {
        val video = CoreVideo(0)
        assertEquals(0, video.id)
        assertEquals(0, video.data.size)
    }
}
