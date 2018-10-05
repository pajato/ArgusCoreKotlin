package com.pajato.argus.core.video

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.io.File

internal class PersisterTest {
    private val dir: File? = File("build/tmp/Argus/").apply {
        if (mkdirs()) println("Directory ${this.absolutePath} has been created.")
    }
    private val repo = File.createTempFile("testVideoRepo", ".txt", dir)
    private val uut = Persister(repo)

    @Test
    fun `load a non-archived video`() {
        fun setup() {
            val events = "Register 0 MI5\nArchive 0 false\n"
            repo.appendText(events)
            Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
            Assertions.assertEquals(events.length.toLong(), repo.length())
        }

        setup()
        val videoList = uut.load()
        Assertions.assertFalse(videoList[0].archived)
        uut.archive(videoList[0])
    }

    @Test
    fun `load an archived video`() {
        fun setup() {
            val events = "Register 0 MI5\nArchive 0 true\n"
            repo.appendText(events)
            Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
            Assertions.assertEquals(events.length.toLong(), repo.length())
        }

        setup()
        val videoList = uut.load()
        Assertions.assertTrue(videoList[0].archived)
        uut.archive(videoList[0])
    }

    @Test
    fun `load a non-archived video for code coverage`() {
        fun setup() {
            val events = "Register 0 MI5\nArchive 1 false\n"
            repo.appendText(events)
            Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
            Assertions.assertEquals(events.length.toLong(), repo.length())
        }

        setup()
        val videoList = uut.load()
        Assertions.assertFalse(videoList[0].archived)
        uut.archive(videoList[0])
    }

    @Test
    fun `load an archived video for code coverage`() {
        fun setup() {
            val events = "Register 0 MI5\nArchive 1 true\n"
            repo.appendText(events)
            Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
            Assertions.assertEquals(events.length.toLong(), repo.length())
        }

        setup()
        val videoList = uut.load()
        Assertions.assertFalse(videoList[0].archived)
        uut.archive(videoList[0])
    }

    @Test
    fun testRegisterEvent() {
        val id = 0L
        val name = "Persister Test Video"
        fun setup() {
            val event = "Register $id $name\n"
            repo.appendText(event)
            Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
            Assertions.assertEquals(event.length.toLong(), repo.length())
        }

        setup()
        val videoList = uut.load()
        Assertions.assertEquals(1, videoList.size)
        Assertions.assertEquals(1, videoList[0].data.size)
        Assertions.assertEquals(id, videoList[0].id)
        Assertions.assertTrue((videoList[0].data[AttributeType.Name]!!.isEqual(Name(name))))
    }

    @Test
    fun testUpdateAddEvent() {
        fun setup() {
            println("Repo path is ${repo.absolutePath ?: "/nowhere!"}")
            val events = "Register 0 MI5\nUpdate 0 Add Cast Keely Hawes\nUpdate 0 Add Cast Matthew MacFadyen\n"
            repo.appendText(events)
            Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
            Assertions.assertEquals(events.length.toLong(), repo.length())
        }

        setup()
        val videoList = uut.load()
        Assertions.assertEquals(1, videoList.size)
        Assertions.assertEquals(2, videoList[0].data.size)
        val attribute = videoList[0].data[AttributeType.Cast]
        if (attribute is Cast)
            Assertions.assertEquals(2, attribute.performers.size)
        else
            fail("The cast attribute does not exist!")
        repo.delete()
    }

    @Test
    fun testUpdateRemoveEvent() {
        fun setup() {
            println("Repo path is ${repo.absolutePath ?: "/nowhere!"}")
            val events = "Register 0 North By Northwest\nUpdate 0 Add Directors Alfred Hitchcock\n" +
                    "Update 0 Add Directors His Assistant\nUpdate 0 Add Directors Steven Spielberg\n" +
                    "Update 0 Remove Directors Alfred Hitchcock\n"
            repo.appendText(events)
            Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
            Assertions.assertEquals(events.length.toLong(), repo.length())
        }

        setup()
        val videoList = uut.load()
        Assertions.assertEquals(1, videoList.size)
        Assertions.assertEquals(2, videoList[0].data.size)
        val attribute = videoList[0].data[AttributeType.Directors]
        if (attribute is Directors)
            Assertions.assertEquals(2, attribute.directors.size)
        else
            fail("The directors attribute does not exist!")
        repo.delete()
    }

    @Test
    fun testUpdateRemoveAllEvent() {
        fun setup() {
            println("Repo path is ${repo.absolutePath ?: "/nowhere!"}")
            val events = "Register 0 North By Northwest\nUpdate 0 Add Directors Alfred Hitchcock\n" +
                    "Update 0 RemoveAll Directors Dummy Director\n"
            repo.appendText(events)
            Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
            Assertions.assertEquals(events.length.toLong(), repo.length())
        }

        setup()
        val videoList = uut.load()
        Assertions.assertEquals(1, videoList.size)
        Assertions.assertEquals(1, videoList[0].data.size)
        repo.delete()
    }

    @Test
    fun `when there are invalid events in the repo, load should ignore them quietly`() {
        fun setup() {
            println("Repo path is ${repo.absolutePath ?: "/nowhere!"}")
            val events = "\nRegister\nRegister abcd\nRegister 0\nRegister 0 fred\n" +
                    "Update 0 Odd\nUpdate 0 Add \nUpdate 0 Add Name\nArchive 0\n"
            repo.appendText(events)
            Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
            val textSize = events.length
            Assertions.assertEquals(textSize.toLong(), repo.length())
        }

        setup()
        val videoList = uut.load()
        Assertions.assertEquals(1, videoList.size)
    }

    @Test
    fun `update a fully loaded video that has all and valid attributes`() {
        fun setup() {
            val events = "Register 0 A fully loaded video\n" +
                    "Update 0 Add Cast Some Star\n" +
                    "Update 0 Add Directors Some Director\n" +
                    "Update 0 Add Name The New Video Title\n" +
                    "Update 0 Add Provider Netflix\n" +
                    "Update 0 Add Release 10\n" +
                    "Update 0 Add Type Movie\n"
            repo.appendText(events)
            Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
            Assertions.assertEquals(events.length.toLong(), repo.length())
        }

        setup()
        val videoList = uut.load()
        Assertions.assertEquals(1, videoList.size)
    }

    @Test
    fun `update for code coverage`() {
        fun setup() {
            val events = "Register 0 A fully loaded video\n" +
                    "Update 0 Add CoverageDefault dummy arg\n" +
                    "Update 0 Add BrokenRegex\n" +
                    "Update\n" +
                    "Update 0 Add Type Unknown\n"
            repo.appendText(events)
            Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
            Assertions.assertEquals(events.length.toLong(), repo.length())
        }

        setup()
        val videoList = uut.load()
        Assertions.assertEquals(1, videoList.size)
        videoList[0].updateAttribute(CoverageDefault(), UpdateType.CoverageDefault)
        uut.update(videoList[0], UpdateType.CoverageDefault)
    }

    @Test
    fun `test that the default case for event type (CoverageDefault) does nothing`() {
        fun setup() {
            val events = "Register 0 A fully loaded video\n" +
                    "CoverageDefault 0 CoverageDefault Name to allow for 100% code coverage\n"
            repo.appendText(events)
            Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
            Assertions.assertEquals(events.length.toLong(), repo.length())
        }

        setup()
        val videoList = uut.load()
        Assertions.assertEquals(1, videoList.size)
    }

    @Test
    fun `test that an invalid id causes an abort`() {
        fun setup() {
            val events = "Register 0 A fully loaded video\n" +
                    "Update 23 Add Name dummy name for unregistered video\n"
            repo.appendText(events)
            Assertions.assertTrue(repo.exists() && repo.isFile && repo.length() > 0L)
            Assertions.assertEquals(events.length.toLong(), repo.length())
        }

        setup()
        val videoList = uut.load()
        Assertions.assertEquals(1, videoList.size)
    }
}
