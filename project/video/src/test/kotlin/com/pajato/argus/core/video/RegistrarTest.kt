package com.pajato.argus.core.video

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegistrarTest {
    private val dir: File? = File("build/tmp/Argus/").apply {
        if (mkdirs()) println("Directory ${this.absolutePath} has been created.")
    }
    private val repo = File.createTempFile("testVideoRepo", ".txt", dir)
    private val registrar = Registrar(repo)
    private val interactor = VideoInteractor(registrar)

    @BeforeEach
    fun init() {
        registrar.reset()
        println("Examining persistence store (${repo.name})")
        repo.forEachLine {
            println(it)
        }
        println("Examined persistence store.\n")
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
    fun `find all with filter should return the correct size`() {
        fun setup() {
            println("Repo path is ${repo.absolutePath ?: "/nowhere!"}")
            interactor.register("Video 1")
            interactor.register("Video 2")
            interactor.register("Video 3")
            interactor.register("Video 4")
            interactor.register("Video 5")
            interactor.register("Video 6")
            interactor.register("Video 7")
            interactor.register("Video 8")
            Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
        }

        setup()
        val data = mutableSetOf<Attribute>(Name("Video 3"))
        val results = interactor.findAll(data)
        Assertions.assertEquals(1, results.size)
    }

    @Test
    fun `exercise find all with filter for code coverage`() {
        val now = Date().time
        val video = interactor.register("Video 1")
        Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
        Assertions.assertTrue(video is CoreVideo)

        var data = mutableSetOf<Attribute>(Name("Video 1"))
        Assertions.assertEquals(1, interactor.findAll(data).size)

        val id = (video as? CoreVideo)?.videoId ?: -1L
        Assertions.assertTrue(id >= now)

        data = mutableSetOf()
        Assertions.assertEquals(1, interactor.findAll(data).size)

        data = mutableSetOf(Provider("HBO"))
        Assertions.assertEquals(0, interactor.findAll(data).size)

        interactor.update(id, data, UpdateType.Add)
        Assertions.assertEquals(1, interactor.findAll(data).size)

        data = mutableSetOf(Provider("StarTwo"))
        Assertions.assertEquals(0, interactor.findAll(data).size)
    }

    @Test
    fun `exercise findById() for code coverage`() {
        val video = interactor.register("Video 1")
        Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
        Assertions.assertTrue(video is CoreVideo)

        val id = (video as? CoreVideo)?.videoId ?: -1L
        Assertions.assertTrue(interactor.findById(id) is CoreVideo)
        Assertions.assertEquals(video, interactor.findById(id))
        Assertions.assertTrue(interactor.findById(23) is VideoError)
    }

    @Test
    fun `registering new item bumps repo count by one`() {
        val expected = registrar.videoList.size + 1
        interactor.register("New video")
        assertEquals(expected, registrar.videoList.size)
    }

    @Test
    fun `archive a registered video for code coverage`() {
        val expected = registrar.videoList.size + 1
        interactor.register("New video")
        assertEquals(expected, registrar.videoList.size)
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
            val id = okVideo.videoId
            assertEquals(okVideo, interactor.findById(id))
        } else fail("Expected a CoreVideo but found a VideoError object!")
        val errorVideo = interactor.findById(-1)
        assertNotEquals(okVideo, errorVideo)
    }

    @Test
    fun `update a video in various ways to exercise the update() method`() {
        val video = interactor.register("Faux Name")
        when (video) {
            is CoreVideo -> {
                val newName = "The Reel Thing"
                val castAttribute = Cast(mutableListOf("Star1", "Star2"))
                val directorsList = mutableListOf("Alfred H.", "Steven S.")
                val directorsAttribute = Directors(directorsList)
                val nameAttribute = Name(newName)
                val providerAttribute = Provider("HBO")
                val releaseAttribute = Release(Date().time)
                //val seriesAttribute = Series(mutableListOf())
                val typeAttribute = Type(VideoType.Movie)
                val attributes = mutableSetOf(
                        castAttribute,
                        directorsAttribute,
                        nameAttribute,
                        providerAttribute,
                        releaseAttribute,
                        //seriesAttribute,
                        typeAttribute)
                interactor.update(video.videoId, attributes, UpdateType.Add)
                assertEquals(newName, (video.videoData[nameAttribute.attrType] as Name).name)
                assertEquals(directorsList, (video.videoData[directorsAttribute.attrType] as Directors).directors)
                interactor.update(video.videoId, mutableSetOf(Cast(mutableListOf("Star2"))), UpdateType.Remove)
                assertTrue(video.videoData[AttributeType.Cast] != null)
                interactor.update(video.videoId, mutableSetOf(Provider("HBO")), UpdateType.RemoveAll)
                assertFalse(video.videoData.contains(AttributeType.Provider))
                interactor.update(video.videoId, mutableSetOf(), UpdateType.CoverageDefault)
                assertTrue(interactor.update(-23, mutableSetOf(), UpdateType.Add) is VideoError)
                assertTrue(interactor.update(video.videoId, mutableSetOf(), UpdateType.CoverageDefault) is CoreVideo)
            }
            is VideoError -> fail("Expected CoreVideo object but found a VideoError with key ${video.key}")
        }
    }
}
