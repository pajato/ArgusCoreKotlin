package com.pajato.argus.core.video

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AttributesTest {
    private val persister = getPersister()

    @Test
    fun `test some Cast code`() {
        val nameValue = "fred"
        val cast = Cast(mutableListOf())
        Assertions.assertFalse(cast.updateByReplace)
        cast.isEqual(AttributeFactory.create(AttributeType.Name.name, nameValue)!!)
        cast.isEqual(Cast.createFrom(nameValue))
        assertEquals(0, cast.performers.size)
        assertEquals(0, cast.values.size)
        cast.updateAdd(Cast.createFrom(nameValue))
        assertEquals(1, cast.performers.size)
        assertEquals(1, cast.values.size)
        cast.updateRemove(Cast.createFrom(nameValue))
        assertEquals(0, cast.performers.size)
        assertEquals(0, cast.values.size)
        cast.updateAdd(AttributeFactory.create(AttributeType.Name.name, nameValue)!!)
        cast.updateRemove(AttributeFactory.create(AttributeType.Name.name, nameValue)!!)
    }

    @Test
    fun `test some Directors code`() {
        val nameValue = "fred"
        val directors = Directors(mutableListOf())
        Assertions.assertFalse(directors.updateByReplace)
        directors.isEqual(AttributeFactory.create(AttributeType.Name.name, nameValue)!!)
        directors.isEqual(Directors.createFrom(nameValue))
        directors.updateAdd(Directors.createFrom(nameValue))
        assertEquals(1, directors.directors.size)
        assertEquals(directors.directors, directors.values)
        directors.updateRemove(Directors.createFrom(nameValue))
        assertEquals(0, directors.directors.size)
        assertEquals(directors.directors, directors.values)
        directors.updateAdd(AttributeFactory.create(AttributeType.Name.name, nameValue)!!)
        directors.updateRemove(AttributeFactory.create(AttributeType.Name.name, nameValue)!!)
    }

    @Test
    fun `test some Name code`() {
        val nameValue = "fred"
        val name = Name(nameValue)
        assertTrue(name.updateByReplace)
        name.updateAdd(name)
        name.updateRemove(name)
        assertEquals(true, name.isEqual(AttributeFactory.create(AttributeType.Name.name, nameValue)!!))
        assertEquals(false, name.isEqual(AttributeFactory.create(AttributeType.Provider.name, nameValue)!!))
        assertEquals(1, name.values.size)
        assertEquals(nameValue, name.values[0])
    }

    @Test
    fun `test some Provider code`() {
        val nameValue = "fred"
        val provider = Provider(nameValue)
        assertTrue(provider.updateByReplace)
        provider.updateAdd(provider)
        provider.updateRemove(provider)
        provider.isEqual(AttributeFactory.create(AttributeType.Name.name, "fred")!!)
        assertEquals(true, provider.isEqual(AttributeFactory.create(AttributeType.Provider.name, nameValue)!!))
        assertEquals(false, provider.isEqual(AttributeFactory.create(AttributeType.Name.name, nameValue)!!))
        assertEquals(1, provider.values.size)
        assertEquals(nameValue, provider.values[0])
    }

    @Test
    fun `test some Release code`() {
        val value = 0L
        val release = Release(value)
        val attrName = AttributeType.Release.name
        assertTrue(release.updateByReplace)
        release.updateAdd(release)
        release.updateRemove(release)
        assertEquals(true, release.isEqual(AttributeFactory.create(attrName, value.toString())!!))
        assertEquals(false, release.isEqual(AttributeFactory.create(Name.key, value.toString())!!))
        assertEquals(1, release.values.size)
        assertEquals(value.toString(), release.values[0])
        release.isEqual(AttributeFactory.create(AttributeType.Name.name, "fred")!!)
        val invalidRelease = Release().create("abcd")
        assertEquals(0L, invalidRelease.timeStamp)
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
            assertEquals(0, series.values.size)
        }

        fun testSeriesUpdateAdd() {
            series.updateAdd(Name(name))
            series.updateAdd(Series(mutableListOf(Series.Episode(1, 2, episodeData))))
            assertEquals(2, series.episodes.size)
            series.persist(UpdateType.Add.name, 0L, persister)
            assertEquals(2, persister.eventStore.readLines().size)
        }

        fun testSeriesUpdateRemove() {
            series.updateRemove(Series(mutableListOf(Series.Episode(1, 2, episodeData))))
            assertEquals(1, series.episodes.size)
            series.updateRemove(Name("fred"))
            series.updateRemove(Series(mutableListOf(Series.Episode(6, 6, episodeData))))
        }

        fun testSeriesFiltering() {
            val testData = mutableMapOf<AttributeType, Attribute>(AttributeType.Series to Series(mutableListOf()))
            series.updateAdd(Series(mutableListOf(Series.Episode(1, 3, testData))))
            assertEquals(2, series.episodes.size)
        }

        testBasicSeries()
        testSeriesUpdateAdd()
        testSeriesUpdateRemove()
        testSeriesFiltering()
    }

    @Test
    fun `test some Type code`() {
        val movieType = VideoType.Movie
        val type = Type(movieType)
        assertTrue(type.updateByReplace)
        type.updateAdd(type)
        type.updateRemove(type)
        type.isEqual(AttributeFactory.create(AttributeType.Name.name, "fred")!!)
        assertEquals(1, type.values.size)
        assertEquals(movieType.name, type.values[0])
    }

    @Test
    fun `test the Cast creator`() {
        val name = "some star"
        val cast = Cast.createFrom(name)
        assertTrue(cast.performers.contains(name))
    }

    @Test
    fun `test the Directors creator`() {
        val name = "the director"
        val directors = Directors.createFrom(name)
        assertTrue(directors.directors.contains(name))
    }

    @Test
    fun `test the Name creator`() {
        val videoName = "the video"
        val name = AttributeFactory.create(AttributeType.Name.name, videoName)
        assertTrue(name is Name && name.name == videoName)
    }

    @Test
    fun `test the Provider creator`() {
        val videoProvider = "Netflix"
        val provider = AttributeFactory.create(AttributeType.Provider.name, videoProvider)
        assertTrue(provider is Provider && provider.name == videoProvider)
    }

    @Test
    fun `test the Release creator`() {
        val videoRelease = "1456223331"
        val release = AttributeFactory.create(AttributeType.Release.name, videoRelease)
        assertTrue(release is Release && release.timeStamp == videoRelease.toLong())
    }

    @Test
    fun `test the Series creator`() {
        var episodeAsString = "1 1 "
        var series = Series().create(episodeAsString)
        assertEquals(0, series.episodes.size)
        AttributeFactory.create(AttributeType.Type.name, "Error")
        episodeAsString = "1 1 InvalidAttributeName xxx"
        series = Series().create(episodeAsString)
        assertEquals(0, series.episodes.size)
    }

    @Test
    fun `test the Type creator`() {
        val name = "Movie"
        var type = AttributeFactory.create(AttributeType.Type.name, name)
        assertTrue(type is Type && type.type.name == name)
        type = AttributeFactory.create(AttributeType.Type.name, "InvalidType")
        assertTrue(type is Type && type.type.name == "Error")
    }

    @Test
    fun `test an invalid attribute creator`() {
        val name = "InvalidAttributeName"
        val invalidAttribute = AttributeFactory.create(name, "")
        assertTrue(invalidAttribute == null)
    }

    @Test
    fun `when arg is not a valid episode regex, test that toEpisode() returns no Episode`() {
        val episodeAsString = "1 1 "
        val series = Series.createFrom(episodeAsString)
        assertEquals(0, series.episodes.size)
    }

    @Test
    fun `when arg is a valid episode regex, test that toEpisode() returns a valid Episode`() {
        val name = "Ned's Demise"
        val episodeAsString = "1 7 Name $name"
        val series = Series.createFrom(episodeAsString)
        assertEquals(1, series.episodes.size)
        assertEquals(1, series.episodes[0].seriesNumber)
        assertEquals(7, series.episodes[0].episodeNumber)
        val actualName = (series.episodes[0].episodeData[AttributeType.Name] as Name).name
        assertEquals(name, actualName)
    }
}
