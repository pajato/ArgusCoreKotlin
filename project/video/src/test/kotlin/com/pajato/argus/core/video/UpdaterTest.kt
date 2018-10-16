package com.pajato.argus.core.video

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

class UpdaterTest {

    @Test
    fun `exercise the updater factory via CoreVideo methods for code coverage`() {
        val video = CoreVideo(0L)
        Assertions.assertEquals(video, video.updateWithStore(updateKey = "fred", persister = Persister(File("fred"))))
        Assertions.assertEquals(video, video.updateForLoad(updateKey = "fred"))
    }
}
