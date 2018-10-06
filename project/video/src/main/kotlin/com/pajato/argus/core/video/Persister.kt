package com.pajato.argus.core.video

import java.io.File

class Persister(private val eventStore: File) {
    fun load(): List<CoreVideo> {
        val list = mutableListOf<CoreVideo>()
        val idMap = mutableMapOf<Long, CoreVideo>()
        val baseRegex = """^([\w]+) (\d+) (.*$)""".toRegex()
        val updateRegex = """^([\w]+) ([\w]+) (.*$)""".toRegex()
        fun parseAndExecuteEvent(line: String) {
            fun executeEvent(command: VideoEventType, id: Long, rest: String) {
                fun processArchiveEvent() {
                    val state = rest.toBoolean()
                    idMap[id]?.archived = state
                }
                fun processRegisterEvent() {
                    val data = mutableMapOf<AttributeType, Attribute>()
                    data[AttributeType.Name] = Name(rest)
                    val video = CoreVideo(id, data)
                    list.add(video)
                    idMap[id] = video
                }
                fun processUpdateEvent() {
                    fun executeUpdateEvent(updateType: UpdateType, attributeType: AttributeType, arg: String) {
                        val video = idMap[id] ?: return
                        val attribute = AttributeFactory.createAttribute(attributeType, arg) ?: return
                        video.updateAttribute(attribute, updateType)
                    }

                    updateRegex.find(rest)?.apply {
                        val updateType = UpdateType.valueOf(groupValues[1])
                        val attributeType = AttributeType.valueOf(groupValues[2])
                        val arg = groupValues[3]
                        executeUpdateEvent(updateType, attributeType, arg)
                    }
                }

                when (command) {
                    VideoEventType.Archive -> processArchiveEvent()
                    VideoEventType.Register -> processRegisterEvent()
                    VideoEventType.Update -> processUpdateEvent()
                    else -> return
                }
            }

            baseRegex.find(line)?.apply {
                val command = VideoEventType.valueOf(groupValues[1])
                val id = groupValues[2].toLong()
                val rest = groupValues[3]
                executeEvent(command, id, rest)
            }
        }

        eventStore.forEachLine { line ->
            parseAndExecuteEvent(line)
        }
        return list
    }

    fun archive(video: CoreVideo) {
        persist(ArchiveEvent(video.videoId))
    }

    fun register(video: CoreVideo, name: String) {
        persist(RegisterEvent(video.videoId, name))
    }

    fun update(video: CoreVideo, type: UpdateType) {
        fun persistAttribute(attr: Attribute) {
            fun putString(value: String) {
                persist(UpdateEvent(type, video.videoId, attr.attrType, value))
            }
            fun putArray(values: List<String>) {
                for (value in values)
                putString(value)
            }

            when (attr) {
                is Cast -> putArray(attr.performers)
                is Directors -> putArray(attr.directors)
                is Name -> putString(attr.name)
                is Provider -> putString(attr.name)
                is Release -> putString(attr.timeStamp.toString())
                is Type -> putString(attr.type.name)
                else -> persist(CoverageDefaultEvent())
            }
        }

        for (attr in video.videoData.values)
            persistAttribute(attr)
    }

    private fun persist(event: VideoEvent) {
        when (event) {
            is ArchiveEvent -> eventStore.appendText("${event.type.name} ${event.id}")
            is RegisterEvent -> eventStore.appendText("${event.type.name} ${event.id} ${event.name}")
            is UpdateEvent -> eventStore.appendText(
                    "${event.type.name} ${event.id} ${event.subtype.name} ${event.attrType.name} ${event.value}"
            )
            else -> return
        }
    }
}
