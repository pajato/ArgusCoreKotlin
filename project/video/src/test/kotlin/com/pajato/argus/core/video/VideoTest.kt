package com.pajato.argus.core.video

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*

fun getPersister(): Persister {
    val dir: File? = File("build/tmp/Argus/").apply {
        if (mkdirs()) println("Directory ${this.absolutePath} has been created.")
    }
    val repo = File.createTempFile("testVideoRepo", ".txt", dir)
    return Persister(repo)
}

class VideoTest {
    private val persister = getPersister()

    @Test
    fun `Exercise the Cast Attribute property`() {
        val firstStar = "Star One"
        val secondStar = "Star Two"
        val castAttribute = Cast(mutableListOf(firstStar, secondStar))
        assertEquals(2, castAttribute.performers.size)
        assert(castAttribute.performers.contains(firstStar))
        assert(castAttribute.performers.contains(secondStar))
        assertEquals(AttributeType.Cast, castAttribute.attrType)
        assert(castAttribute.isEqual(Cast(castAttribute.performers)))
        assert(!castAttribute.isEqual(Cast(mutableListOf("some other star"))))
    }

    @Test
    fun `Exercise the Release Attribute property`() {
        val timeStamp = Date().time
        val releaseAttribute = Release(timeStamp)
        assertEquals(timeStamp, releaseAttribute.timeStamp)
        assertEquals(AttributeType.Release, releaseAttribute.attrType)
        assert(releaseAttribute.isEqual(Release(releaseAttribute.timeStamp)))
        assert(!releaseAttribute.isEqual(Release( Date().time + 10L)))
    }

    @Test
    fun `Exercise the Directors Attribute property`() {
        val firstDirector = "Director One"
        val secondDirector = "Director Two"
        val directorsAttribute = Directors(mutableListOf(firstDirector, secondDirector))
        assertEquals(2, directorsAttribute.directors.size)
        assert(directorsAttribute.directors.contains(firstDirector))
        assert(directorsAttribute.directors.contains(secondDirector))
        assertEquals(AttributeType.Directors, directorsAttribute.attrType)
        assert(directorsAttribute.isEqual(Directors(directorsAttribute.directors)))
        assert(!directorsAttribute.isEqual(Directors(mutableListOf("some other director"))))
    }

    @Test
    fun `Test the Name Attribute property`() {
        val nameAttribute = Name("testName")
        assertEquals("testName", nameAttribute.name)
        assertEquals(AttributeType.Name, nameAttribute.attrType)
        assert(nameAttribute.isEqual(Name(nameAttribute.name)))
        assert(!nameAttribute.isEqual(Name("some other name")))
    }

    @Test
    fun `Exercise the Provider Attribute property`() {
        val providerName = "Amazon"
        val providerAttribute = Provider(providerName)
        assertEquals(providerName, providerAttribute.name)
        assertEquals(AttributeType.Provider, providerAttribute.attrType)
        assert(providerAttribute.isEqual(Provider(providerAttribute.name)))
        assert(!providerAttribute.isEqual(Provider("some other provider")))
    }

    @Test
    fun `Exercise the Type Attribute property`() {
        val typeAttribute = Type(VideoType.Movie)
        assertThat(typeAttribute.type).isEqualTo(VideoType.Movie)
        assertThat(typeAttribute.attrType).isEqualTo(AttributeType.Type)
        assert(typeAttribute.isEqual(Type(typeAttribute.type)))
        assert(!typeAttribute.isEqual(Type(VideoType.TvShow)))

        assert(typeAttribute.updateByReplace)
        typeAttribute.updateAdd(typeAttribute)
        typeAttribute.isEqual(AttributeFactory.create(AttributeType.Name.name, "fred")!!)

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
        assertEquals(0, video.videoId)
        assertEquals(0, video.videoData.size)
    }

    @Test
    fun `Exercise the default Archive Event class`() {
        val event = ArchiveEvent()
        assertEquals("Archive", event.key)
        assertEquals("", event.videoId)
        assertEquals("", event.state)
    }

    @Test
    fun `Exercise the Archive Event with an invalid id`() {
        val event = ArchiveEvent("abc")
        assertEquals("Archive", event.key)
        assertEquals("abc", event.videoId)
        assertEquals("", event.state)
        event.load(mutableMapOf())
    }

    @Test
    fun `Exercise the default Register Event class`() {
        val name = "Some Name"
        val event = RegisterEvent("0", name)
        assertEquals("Register", event.key)
        assertEquals("0", event.videoId)
        assertEquals(name, event.videoName)
    }

    @Test
    fun `Exercise the Register Event with an invalid id`() {
        val name = "Some Name"
        val videoId = "AlphaBetaGamma"
        val event = RegisterEvent(videoId, name)
        assertEquals("Register", event.key)
        assertEquals(videoId, event.videoId)
        assertEquals(name, event.videoName)
        event.load(mutableMapOf())
    }

    @Test
    fun `Exercise the default Update (append) Event class`() {
        val id = "0"
        val attrType = AttributeType.Cast
        val name = "Keely Hawes"
        val event = UpdateEvent(id, AttributeType.Cast.name, name, UpdateType.Add.name)
        assertEquals("Update", event.key)
        assertEquals(UpdateType.Add.name, event.updateType)
        assertEquals(id, event.videoId)
        assertEquals(attrType.name, event.attributeName)
        assertEquals(name, event.attributeValue)
    }

    @Test
    fun `Exercise the Update (append) Event with an invalid id`() {
        val id = "xyz"
        val attrType = AttributeType.Cast
        val name = "Keely Hawes"
        val event = UpdateEvent(id, AttributeType.Cast.name, name, UpdateType.Add.name)
        assertEquals("Update", event.key)
        assertEquals(UpdateType.Add.name, event.updateType)
        assertEquals(id, event.videoId)
        assertEquals(attrType.name, event.attributeName)
        assertEquals(name, event.attributeValue)
        event.load(mutableMapOf())
    }

    @Test
    fun `Exercise the Update (append) Event with an invalid attribute`() {
        val id = "0"
        val name = "Keely Hawes"
        val attributeName = "InvalidAttributeName"
        val event = UpdateEvent(id, attributeName, name, UpdateType.Add.name)
        event.load(mutableMapOf(0L to CoreVideo(0)))
        assertEquals("Update", event.key)
        assertEquals(UpdateType.Add.name, event.updateType)
        assertEquals(id, event.videoId)
        assertEquals(attributeName, event.attributeName)
        assertEquals(name, event.attributeValue)
    }

    @Test
    fun `Exercise the Update (remove) Event class`() {
        val id = "0"
        val attrType = AttributeType.Cast
        val name = "Keely Hawes"
        val event = UpdateEvent(id, AttributeType.Cast.name, name, UpdateType.Remove.name)
        assertEquals("Update", event.key)
        assertEquals(UpdateType.Remove.name, event.updateType)
        assertEquals(id, event.videoId)
        assertEquals(attrType.name, event.attributeName)
        assertEquals(name, event.attributeValue)
    }

    @Test
    fun `Exercise the Update (removeAll) Event class`() {
        val id = "0"
        val attrType = AttributeType.Cast
        val name = "Keely Hawes"
        val event = UpdateEvent(id, AttributeType.Cast.name, name, UpdateType.RemoveAll.name)
        assertEquals("Update", event.key)
        assertEquals(UpdateType.RemoveAll.name, event.updateType)
        assertEquals(id, event.videoId)
        assertEquals(attrType.name, event.attributeName)
        assertEquals(name, event.attributeValue)
    }

    @Test
    fun `when arg is numeric test that getLong() returns a correct result`() {
        val argAsLong = 100254612L
        val argAsString = argAsLong.toString()
        assertEquals(argAsLong, getLong(argAsString))
    }

    @Test
    fun `when arg is non-numeric test that getLong() returns a 0L`() {
        val argAsString = "ABC123"
        assertEquals(0L, getLong(argAsString))
    }

    @Test
    fun `when first and second are identical test that hasSameEntries() returns true`() {
        val first = mutableListOf("A", "C", "D", "Z")
        val second = mutableListOf("C", "A", "Z", "D")
        assert(hasSameEntries(first, second))
        assert(hasSameEntries(second, first))
    }

    @Test
    fun `when first and second are not identical test that hasSameEntries() returns false`() {
        val first = mutableListOf("A", "C", "D", "Z")
        val second = mutableListOf("C", "A", "B", "D")
        assert(!hasSameEntries(first, second))
        assert(!hasSameEntries(second, first))
    }

    @Test
    fun `do an update remove and removeAll on a core video object`() {
        val video = CoreVideo(0)
        video.videoData[AttributeType.Provider] = Provider("HBO")
        video.updateWithStore(mutableSetOf(Provider("")), UpdateType.Remove.name, persister)
        video.updateWithStore(mutableSetOf(Provider("")), UpdateType.RemoveAll.name, persister)
        video.updateWithStore(mutableSetOf(Cast(mutableListOf())), UpdateType.Remove.name, persister)
    }
}
