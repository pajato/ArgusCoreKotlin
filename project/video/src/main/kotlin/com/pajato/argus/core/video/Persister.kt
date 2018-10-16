package com.pajato.argus.core.video

import java.io.File

class Persister(val eventStore: File) {
    companion object {
        val baseRegex = """^([\w]+) (\d+) (.*$)""".toRegex()
    }

    fun archive(video: CoreVideo) {
        ArchiveEvent(video.videoId.toString()).store(this)
    }

    fun clear() {
        eventStore.writeText("")
    }

    fun load(): MutableMap<Long, CoreVideo> {
        val idMap = mutableMapOf<Long, CoreVideo>()
        fun parseAndExecuteEvent(line: String) {
            fun toEvent(eventName: String, videoId: String, rest: String): VideoEvent? =
                    videoEventMap[eventName]?.create(videoId, rest)

            baseRegex.find(line)?.apply {
                val eventName = groupValues[1]
                val videoId = groupValues[2]
                val rest = groupValues[3]
                val event = toEvent(eventName, videoId, rest) ?: return
                event.load(idMap)
            }
        }

        eventStore.forEachLine { line ->
            parseAndExecuteEvent(line)
        }
        return idMap
    }

    fun persist(text: String) {
        eventStore.appendText(text)
    }

    fun register(video: CoreVideo, name: String) {
        RegisterEvent(video.videoId.toString(), name).store(this)
    }

    fun update(updateType: String, videoId: Long, attributeName: String, attributeValue: String) {
        UpdateEvent(videoId.toString(), attributeName, attributeValue, updateType).store(this)
    }

}

private val videoEventMap = mapOf(
        ArchiveEvent.eventName to ArchiveEvent(),
        RegisterEvent.eventName to RegisterEvent(),
        UpdateEvent.eventName to UpdateEvent()
)

