package com.pajato.argus.core.video

import com.pajato.argus.core.data.CoreVideo
import com.pajato.argus.core.data.Directors
import com.pajato.argus.core.data.Name
import com.pajato.argus.core.data.VideoError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import test.TestRegistrar

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VideoInteractorTest {
    private val registrar = TestRegistrar()
    private val interactor = VideoInteractor(registrar)

    @BeforeEach
    fun init() {
        registrar.reset()
    }

    @Test
    fun `find all should return the correct size`() {
        assertThat(interactor.findAll().size).isEqualTo(0)
        interactor.register("Video 1")
        assertThat(interactor.findAll().size).isEqualTo(1)
        interactor.register("Video 2")
        assertThat(interactor.findAll().size).isEqualTo(2)
    }

    @Test
    fun `registering new item bumps repo count by one`() {
        val expected = registrar.videoStore.size + 1
        interactor.register("New video")
        assertEquals(expected, registrar.videoStore.size)
    }

    @Test
    fun `registering a video twice generates an error`() {
        val name = "New Name"
        val validVideo = interactor.register(name)
        assertEquals(CoreVideo::class, validVideo::class)
        val invalidVideo = interactor.register(name)
        assertEquals(VideoError::class, invalidVideo::class)
    }

    @Test
    fun `find by name correctly finds and does not find a video`() {
        val name = "Video To Find By Name"
        val okVideo = interactor.register(name)
        assertEquals(okVideo, interactor.findByName(name))
        val errorVideo = interactor.findByName("No Such Name")
        assertNotEquals(okVideo, errorVideo)
    }

    @Test
    fun `find by id correctly finds and does not find a video`() {
        val name = "Video To Find By Id"
        val okVideo = interactor.register(name)
        if (okVideo is CoreVideo) {
            val id = okVideo.id
            assertEquals(okVideo, interactor.findById(id))
        } else fail("Expected a CoreVideo but found a VideoError object!")
        val errorVideo = interactor.findById(-1)
        assertNotEquals(okVideo, errorVideo)
    }

    @Test
    fun `update a video to change the name and add a directors list`() {
        val video = interactor.register("Faux Name")
        when (video) {
            is CoreVideo -> {
                val newName = "The Reel Thing"
                val nameAttribute = Name(newName)
                val directorsList = listOf("Alfred H.", "Steven S.")
                val directorsAttribute = Directors(directorsList)
                val attributes = mutableSetOf(nameAttribute, directorsAttribute)
                interactor.update(video.id, attributes)
                assertEquals(newName, (video.data[nameAttribute.key] as Name).name)
                assertEquals(directorsList, (video.data[directorsAttribute.key] as Directors).directors)
            }
            is VideoError -> fail("Expected CoreVideo object but found a VideoError with key ${video.key}")
        }
    }
}
