package test

import com.pajato.argus.core.data.Directors
import com.pajato.argus.core.data.ErrorKey
import com.pajato.argus.core.data.Movie
import com.pajato.argus.core.data.Name
import com.pajato.argus.core.data.TVShow
import com.pajato.argus.core.data.VideoError
import com.pajato.argus.core.data.VideoType
import com.pajato.argus.core.video.VideoInteractor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class VideoInteractorTest {
    private val registrar = TestRegistrar()
    private val interactor = VideoInteractor(registrar)

    @Test
    fun `registering new item bumps repo count by one`() {
        val expected = registrar.videoStore.size + 1
        interactor.register("New video", VideoType.tvShow)
        assertEquals(expected, registrar.videoStore.size)
    }

    @Test
    fun `registering a video twice generates an error`() {
        val name = "New Name"
        val type = VideoType.tvShow
        val validVideo = interactor.register(name, type)
        assert(validVideo !is VideoError)
        val invalidVideo = interactor.register(name, type)
        assert(invalidVideo is VideoError)
        if (invalidVideo !is VideoError)
            fail<kotlin.String>("New name was incorrectly accepted!")
        else {
            assertEquals(ErrorKey.AlreadyExists, invalidVideo.key)
            assertEquals(VideoType.error, invalidVideo.type)
        }
    }

    @Test
    fun `find by name correctly finds and does not find a video`() {
        val name = "Video To Find By Name"
        val type = VideoType.movie
        val okVideo = interactor.register(name, type)
        assertEquals(okVideo, interactor.findByName(name))
        assertEquals(VideoType.movie, okVideo.type)
        val errorVideo = interactor.findByName("No Such Name")
        assertNotEquals(okVideo, errorVideo)
        assertEquals(VideoType.error, errorVideo.type)
    }

    @Test
    fun `find by id correctly finds and does not find a video`() {
        val name = "Video To Find By Id"
        val type = VideoType.tvShow
        val okVideo = interactor.register(name, type) as TVShow
        val id = okVideo.id
        assertEquals(okVideo, interactor.findById(id))
        assertEquals(VideoType.tvShow, okVideo.type)
        val errorVideo = interactor.findById(-1)
        assertNotEquals(okVideo, errorVideo)
        assertEquals(VideoType.error, errorVideo.type)
    }

    @Test
    fun `update a video to change the name and add a directors list`() {
        val video = interactor.register("Faux Name", VideoType.movie) as Movie
        val newName = "The Reel Thing"
        val nameAttribute = Name(newName)
        val directorsList = listOf("Alfred H.", "Steven S.")
        val directorsAttribute = Directors(directorsList)
        val attributes = mutableSetOf(nameAttribute, directorsAttribute)
        interactor.update(video.id, attributes)
        assertEquals(newName, (video.data[nameAttribute.key] as Name).name)
        assertEquals(directorsList, (video.data[directorsAttribute.key] as Directors).directors)
    }
}
