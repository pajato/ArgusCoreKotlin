package com.pajato.argus.core.video

import com.pajato.argus.core.video.AttributeFactory.createAttribute
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class AttributesTest {

    @Test
    fun `exercise some Cast code`() {
        val cast = Cast(mutableListOf())
        Assertions.assertFalse(cast.updateByReplace)
        cast.isEqual(AttributeFactory.createAttribute(AttributeType.Name, "fred")!!)
        cast.isEqual(AttributeFactory.createAttribute(AttributeType.Cast, "fred")!!)
        cast.update(AttributeFactory.createAttribute(AttributeType.Cast, "fred")!!, UpdateType.Add)
        assertTrue(cast.performers.size == 1)
        cast.update(AttributeFactory.createAttribute(AttributeType.Cast, "fred")!!, UpdateType.Remove)
        assertTrue(cast.performers.size == 0)
        cast.update(AttributeFactory.createAttribute(AttributeType.Name, "fred")!!, UpdateType.Add)
        cast.update(AttributeFactory.createAttribute(AttributeType.Cast, "fred")!!, UpdateType.RemoveAll)
        cast.update(AttributeFactory.createAttribute(AttributeType.Cast, "fred")!!, UpdateType.CoverageDefault)
    }

    @Test
    fun `exercise some Directors code`() {
        val directors = Directors(mutableListOf())
        Assertions.assertFalse(directors.updateByReplace)
        directors.isEqual(AttributeFactory.createAttribute(AttributeType.Name, "fred")!!)
        directors.isEqual(AttributeFactory.createAttribute(AttributeType.Directors, "fred")!!)
        directors.update(AttributeFactory.createAttribute(AttributeType.Directors, "fred")!!, UpdateType.Add)
        assertTrue(directors.directors.size == 1)
        directors.update(AttributeFactory.createAttribute(AttributeType.Directors, "fred")!!, UpdateType.Remove)
        assertTrue(directors.directors.size == 0)
        directors.update(AttributeFactory.createAttribute(AttributeType.Name, "fred")!!, UpdateType.Add)
        directors.update(AttributeFactory.createAttribute(AttributeType.Directors, "fred")!!, UpdateType.RemoveAll)
        directors.update(createAttribute(AttributeType.Directors, "fred")!!, UpdateType.CoverageDefault)
    }

    @Test
    fun `exercise some Name code`() {
        val name = Name("fred")
        assertTrue(name.updateByReplace)
        name.update(name, UpdateType.Add)
        name.isEqual(AttributeFactory.createAttribute(AttributeType.Provider, "fred")!!)
    }

    @Test
    fun `exercise some Provider code`() {
        val provider = Provider("fred")
        assertTrue(provider.updateByReplace)
        provider.update(provider, UpdateType.Add)
        provider.isEqual(AttributeFactory.createAttribute(AttributeType.Name, "fred")!!)
    }

    @Test
    fun `exercise some Release code`() {
        val release = Release(0L)
        assertTrue(release.updateByReplace)
        release.update(release, UpdateType.Add)
        release.isEqual(AttributeFactory.createAttribute(AttributeType.Name, "fred")!!)
    }

    @Test
    fun `exercise some Type code`() {
        val type = Type(VideoType.Movie)
        assertTrue(type.updateByReplace)
        type.update(type, UpdateType.Add)
        type.isEqual(AttributeFactory.createAttribute(AttributeType.Name, "fred")!!)
    }

    @Test
    fun `exercise some CoverageDefault code`() {
        val coverageDefault = CoverageDefault()
        Assertions.assertFalse(coverageDefault.updateByReplace)
        coverageDefault.update(coverageDefault, UpdateType.CoverageDefault)
        coverageDefault.isEqual(coverageDefault)
        assertEquals(0, coverageDefault.values.size)
    }

    @Test
    fun `test the Cast creator`() {
        val name = "some star"
        val cast = AttributeFactory.createAttribute(AttributeType.Cast, name)
        assertTrue(cast is Cast && cast.performers.contains(name))
    }

    @Test
    fun `test the Directors creator`() {
        val name = "the director"
        val directors = AttributeFactory.createAttribute(AttributeType.Directors, name)
        assertTrue(directors is Directors && directors.directors.contains(name))
    }

    @Test
    fun `test the Name creator`() {
        val videoName = "the video"
        val name = AttributeFactory.createAttribute(AttributeType.Name, videoName)
        assertTrue(name is Name && name.name == videoName)
    }

    @Test
    fun `test the Provider creator`() {
        val videoProvider = "Netflix"
        val provider = AttributeFactory.createAttribute(AttributeType.Provider, videoProvider)
        assertTrue(provider is Provider && provider.name == videoProvider)
    }

    @Test
    fun `test the Release creator`() {
        val videoRelease = "1456223331"
        val release = AttributeFactory.createAttribute(AttributeType.Release, videoRelease)
        assertTrue(release is Release && release.timeStamp == videoRelease.toLong())
    }

    @Test
    fun `test the Series creator`() {
        val episodeAsString = "1 1 "
        val series = AttributeFactory.createAttribute(AttributeType.Series, episodeAsString) ?:
                fail("Could not create a Series attribute!")
        assertTrue(series is Series)
        assertEquals(1, (series as Series).episodes.size)
        AttributeFactory.createAttribute(AttributeType.Type, "InvalidType")
    }

    @Test
    fun `test the Type creator`() {
        val name = "Movie"
        val type = AttributeFactory.createAttribute(AttributeType.Type, name)
        assertTrue(type is Type && type.type.name == name)
        AttributeFactory.createAttribute(AttributeType.Type, "InvalidType")
    }

    @Test
    fun `test the Series attribute`() {
        val name = "The White Walker"
        val episodeData = mutableMapOf<AttributeType, Attribute>(AttributeType.Name to Name(name))
        val series = Series(mutableListOf(Series.Episode(1, 1, episodeData)))
        fun testBasicSeries() {
            assertEquals(1, series.episodes.size)
            assertEquals(1, series.episodes[0].seriesNumber)
            assertEquals(1, series.episodes[0].episodeNumber)
            assertEquals(name, (series.episodes[0].episodeData[AttributeType.Name] as Name).name)
            assertEquals(AttributeType.Series, series.attrType)
            assertEquals(false, series.updateByReplace)
            assertEquals(true, series.isEqual(series))
            assertEquals(1, series.values.size)
        }
        fun testSeriesUpdateAdd() {
            series.update(Name(name), UpdateType.Add)
            assertEquals(1, series.values.size)
            series.update(Series(mutableListOf(Series.Episode(1, 2, episodeData))), UpdateType.Add)
            assertEquals(2, series.values.size)
            assertEquals(2, series.episodes.size)
        }
        fun testSeriesUpdateRemove() {
            series.update(Series(mutableListOf(Series.Episode(1, 2, episodeData))), UpdateType.Remove)
            assertEquals(1, series.values.size)
            assertEquals(1, series.episodes.size)
        }
        fun testSeriesUpdateRemoveAll() {
            series.update(Series(mutableListOf()), UpdateType.RemoveAll)
            assertEquals(0, series.values.size)
        }
        fun testSeriesCoverageCompleteness() {
            series.update(Series(mutableListOf()), UpdateType.CoverageDefault)
            assertEquals(0, series.values.size)
            series.update(Series(mutableListOf(Series.Episode(1, 2, episodeData))), UpdateType.Add)
            series.update(Series(mutableListOf(Series.Episode(2, 3, episodeData))), UpdateType.Add)
            assertEquals(2, series.values.size)
            assertEquals(2, series.episodes.size)
        }
        fun testSeriesFiltering() {
            val testData = mutableMapOf<AttributeType, Attribute>(AttributeType.Series to Series(mutableListOf()))
            series.update(Series(mutableListOf(Series.Episode(1, 3, testData))), UpdateType.Add)
            assertEquals(2, series.values.size)
        }

        testBasicSeries()
        testSeriesUpdateAdd()
        testSeriesUpdateRemove()
        testSeriesUpdateRemoveAll()
        testSeriesCoverageCompleteness()
        testSeriesFiltering()
    }

    @Test fun `when arg is not a valid episode regex, test that toEpisode() returns an invalid Episode`() {
        val episodeAsString = "1 1 "
        val attribute = AttributeFactory.createAttribute(AttributeType.Series, episodeAsString) ?:
        fail("Could not create a Series attribute!")
        assertTrue(attribute is Series)
        val series = attribute as Series
        assertEquals(1, series.episodes.size)
        assertEquals(0, series.episodes[0].seriesNumber)
        assertEquals(0, series.episodes[0].episodeNumber)
    }

    @Test fun `when arg is a valid episode regex, test that toEpisode() returns a valid Episode`() {
        val name = "Ned's Demise"
        val episodeAsString = "1 7 Name $name"
        val attribute = AttributeFactory.createAttribute(AttributeType.Series, episodeAsString) ?:
        fail("Could not create a Series attribute!")
        assertTrue(attribute is Series)
        val series = attribute as Series
        assertEquals(1, series.episodes.size)
        assertEquals(1, series.episodes[0].seriesNumber)
        assertEquals(7, series.episodes[0].episodeNumber)
        val actualName = (series.episodes[0].episodeData[AttributeType.Name] as Name).name
        assertEquals(name, actualName)
    }

    @Test fun `exercise a null attribute passes to the toEpisode() method`() {
        val name = "Unknown"
        val episodeAsString = "1 7 Type $name"
        val attribute = AttributeFactory.createAttribute(AttributeType.Series, episodeAsString) ?:
        fail("Could not create a Series attribute!")
        assertTrue(attribute is Series)
        val series = attribute as Series
        assertEquals(1, series.episodes.size)
        assertEquals(0, series.episodes[0].seriesNumber)
        assertEquals(0, series.episodes[0].episodeNumber)
        assertEquals(0, series.episodes[0].episodeData.size)
    }
}
