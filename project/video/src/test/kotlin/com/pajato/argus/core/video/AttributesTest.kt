package com.pajato.argus.core.video

import com.pajato.argus.core.video.AttributeFactory.createAttribute
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AttributesTest {

    @Test
    fun `exercise some Cast code`() {
        val cast = Cast(mutableListOf())
        Assertions.assertFalse(cast.updateByReplace)
        cast.isEqual(AttributeFactory.createAttribute(AttributeType.Name, "fred")!!)
        cast.isEqual(AttributeFactory.createAttribute(AttributeType.Cast, "fred")!!)
        cast.update(AttributeFactory.createAttribute(AttributeType.Cast, "fred")!!, UpdateType.Add)
        Assertions.assertTrue(cast.performers.size == 1)
        cast.update(AttributeFactory.createAttribute(AttributeType.Cast, "fred")!!, UpdateType.Remove)
        Assertions.assertTrue(cast.performers.size == 0)
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
        Assertions.assertTrue(directors.directors.size == 1)
        directors.update(AttributeFactory.createAttribute(AttributeType.Directors, "fred")!!, UpdateType.Remove)
        Assertions.assertTrue(directors.directors.size == 0)
        directors.update(AttributeFactory.createAttribute(AttributeType.Name, "fred")!!, UpdateType.Add)
        directors.update(AttributeFactory.createAttribute(AttributeType.Directors, "fred")!!, UpdateType.RemoveAll)
        directors.update(createAttribute(AttributeType.Directors, "fred")!!, UpdateType.CoverageDefault)
    }

    @Test
    fun `exercise some Name code`() {
        val name = Name("fred")
        Assertions.assertTrue(name.updateByReplace)
        name.update(name, UpdateType.Add)
        name.isEqual(AttributeFactory.createAttribute(AttributeType.Provider, "fred")!!)
    }

    @Test
    fun `exercise some Provider code`() {
        val provider = Provider("fred")
        Assertions.assertTrue(provider.updateByReplace)
        provider.update(provider, UpdateType.Add)
        provider.isEqual(AttributeFactory.createAttribute(AttributeType.Name, "fred")!!)
    }

    @Test
    fun `exercise some Release code`() {
        val release = Release(0L)
        Assertions.assertTrue(release.updateByReplace)
        release.update(release, UpdateType.Add)
        release.isEqual(AttributeFactory.createAttribute(AttributeType.Name, "fred")!!)
    }

    @Test
    fun `exercise some Type code`() {
        val type = Type(VideoType.Movie)
        Assertions.assertTrue(type.updateByReplace)
        type.update(type, UpdateType.Add)
        type.isEqual(AttributeFactory.createAttribute(AttributeType.Name, "fred")!!)
    }

    @Test
    fun `exercise some CoverageDefault code`() {
        val coverageDefault = CoverageDefault()
        Assertions.assertFalse(coverageDefault.updateByReplace)
        coverageDefault.update(coverageDefault, UpdateType.CoverageDefault)
        coverageDefault.isEqual(coverageDefault)
    }

    @Test
    fun `test the Cast creator`() {
        val name = "some star"
        val cast = AttributeFactory.createAttribute(AttributeType.Cast, name)
        Assertions.assertTrue(cast is Cast && cast.performers.contains(name))
    }

    @Test
    fun `test the Directors creator`() {
        val name = "the director"
        val directors = AttributeFactory.createAttribute(AttributeType.Directors, name)
        Assertions.assertTrue(directors is Directors && directors.directors.contains(name))
    }

    @Test
    fun `test the Name creator`() {
        val videoName = "the video"
        val name = AttributeFactory.createAttribute(AttributeType.Name, videoName)
        Assertions.assertTrue(name is Name && name.name == videoName)
    }

    @Test
    fun `test the Provider creator`() {
        val videoProvider = "Netflix"
        val provider = AttributeFactory.createAttribute(AttributeType.Provider, videoProvider)
        Assertions.assertTrue(provider is Provider && provider.name == videoProvider)
    }

    @Test
    fun `test the Release creator`() {
        val videoRelease = "1456223331"
        val release = AttributeFactory.createAttribute(AttributeType.Release, videoRelease)
        Assertions.assertTrue(release is Release && release.timeStamp == videoRelease.toLong())
    }

    @Test
    fun `test the Type creator`() {
        val name = "Movie"
        val type = AttributeFactory.createAttribute(AttributeType.Type, name)
        Assertions.assertTrue(type is Type && type.type.name == name)
        AttributeFactory.createAttribute(AttributeType.Type, "InvalidType")
    }

}
