package com.pajato.argus.core.video

import java.io.File
import java.util.*

class Registrar(file: File) : VideoRegistrar {
    val videoList : MutableList<Video> = mutableListOf()
    private val videoStore = Persister(file)
    private val idMap: MutableMap<kotlin.String, Long> = mutableMapOf()
    private val videoMap: MutableMap<Long, CoreVideo> = mutableMapOf()
    private var lastTimestamp = 0L

    init {
        videoStore.load()
    }

    fun reset() {
        videoList.clear()
        idMap.clear()
        videoMap.clear()
    }

    override fun findAll(filterData: MutableSet<Attribute>): List<Video> {
        fun matches(video: CoreVideo): Boolean {
            filterData.forEach {
                val attribute = video.data[it.attrType] ?: return false
                if (!attribute.isEqual(it)) return false
            }
            return true
        }

        val list = videoMap.values.toList()
        return if (filterData.size == 0) list else list.filter { matches(it) }
    }

    override fun findById(id: Long): Video {
        return videoMap[id] ?: VideoError(ErrorKey.NoSuchVideo)
    }

    override fun findByName(name: kotlin.String): Video {
        val id = idMap.getOrDefault(name, -1L)
        return if (id != -1L) findById(id) else VideoError(ErrorKey.NoSuchVideo)
    }

    override fun register(name: kotlin.String): Video {
        fun createVideo(): Video {
            fun getUniqueTimestamp(): Long {
                do {
                    val nextTimestamp = Date().time
                    if (nextTimestamp > lastTimestamp) {
                        lastTimestamp = nextTimestamp
                        return nextTimestamp
                    }
                    Thread.sleep(1)
                } while (true)
            }
            fun processAttributes(video: CoreVideo) {
                fun registerVideoWithAttributes(video: CoreVideo, attrs: MutableMap<AttributeType, Attribute>) {
                    videoList.add(video)
                    val id = video.id
                    idMap[name] = id
                    videoMap[id] = video
                    video.data.putAll(attrs)
                }

                val attributes : MutableMap<AttributeType, Attribute> = mutableMapOf()
                val nameAttribute = Name(name)
                attributes[nameAttribute.attrType] = nameAttribute
                registerVideoWithAttributes(video, attributes)
            }

            val video = CoreVideo(getUniqueTimestamp())
            processAttributes(video)
            videoStore.register(video, name)
            return video
        }

        return if (idMap[name] != null) VideoError(ErrorKey.AlreadyExists) else createVideo()
    }

    override fun update(id: Long, data: MutableSet<Attribute>, type: UpdateType): Video {
        fun updateAttribute(video: CoreVideo, attribute: Attribute) {
            video.updateAttribute(attribute, type)
            videoStore.update(video, type)
        }

        val video = findById(id)
        return when (video) {
            is VideoError -> video
            is CoreVideo -> {
                for (attribute in data)
                    updateAttribute(video, attribute)
                video
            }
        }
    }
}
