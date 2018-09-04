package test

import com.pajato.argus.core.data.Cast
import com.pajato.argus.core.data.Directors
import com.pajato.argus.core.data.Episode
import com.pajato.argus.core.data.ErrorKey
import com.pajato.argus.core.data.Movie
import com.pajato.argus.core.data.Name
import com.pajato.argus.core.data.Provider
import com.pajato.argus.core.data.Release
import com.pajato.argus.core.data.Series
import com.pajato.argus.core.data.SeriesInstance
import com.pajato.argus.core.data.TVShow
import com.pajato.argus.core.data.VideoError
import com.pajato.argus.core.data.VideoType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class DataTest {
    @Test
    fun `Exercise the Attribute properties`() {
        val nameAttribute = Name("testName")
        assertEquals("testName", nameAttribute.name)
        assertEquals("Name", nameAttribute.key)

        val timeStamp = Date().time
        val releaseAttribute = Release(timeStamp)
        assertEquals(timeStamp, releaseAttribute.timeStamp)
        assertEquals("Release", releaseAttribute.key)

        val firstStar = "Star One"
        val secondStar = "Star Two"
        val castAttribute = Cast(listOf(firstStar, secondStar))
        assertEquals(2, castAttribute.performers.size)
        assert(castAttribute.performers.contains(firstStar))
        assert(castAttribute.performers.contains(secondStar))
        assertEquals("Cast", castAttribute.key)

        val firstDirector = "Director One"
        val secondDirector = "Director Two"
        val directorsAttribute = Directors(listOf(firstDirector, secondDirector))
        assertEquals(2, directorsAttribute.directors.size)
        assert(directorsAttribute.directors.contains(firstDirector))
        assert(directorsAttribute.directors.contains(secondDirector))
        assertEquals("Directors", directorsAttribute.key)

        val providerName = "Amazon"
        val providerAttribute = Provider(providerName)
        assertEquals(providerName, providerAttribute.name)
        assertEquals("Provider", providerAttribute.key)

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
    fun `creating video error does the right thing`() {
        val key = ErrorKey.Ok
        val error = VideoError(key)
        assertEquals(key, error.key)
        assertEquals(0, error.data.size)
    }

    @Test
    fun `TV show video has a correct id, type and no attributes`() {
        val video = TVShow(0)
        assertEquals(0, video.id)
        assertEquals(VideoType.tvShow, video.type)
        assertEquals(0, video.data.size)
    }

    @Test
    fun `Movie video has a correct id, type and no attributes`() {
        val video = Movie(0)
        assertEquals(0, video.id)
        assertEquals(VideoType.movie, video.type)
        assertEquals(0, video.data.size)
    }
}
