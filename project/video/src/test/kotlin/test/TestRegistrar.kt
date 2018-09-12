package test

import com.pajato.argus.core.data.Attribute
import com.pajato.argus.core.data.CoreVideo
import com.pajato.argus.core.data.ErrorKey
import com.pajato.argus.core.data.Name
import com.pajato.argus.core.data.Video
import com.pajato.argus.core.data.VideoError
import com.pajato.argus.core.video.VideoRegistrar

class TestRegistrar : VideoRegistrar {
    val videoStore : MutableList<Video> = mutableListOf()

    private val idMap: MutableMap<kotlin.String, Int> = mutableMapOf()
    private val videoMap: MutableMap<Int, CoreVideo> = mutableMapOf()
    private var idCounter = 0

    fun reset() {
        videoStore.clear()
        idMap.clear()
        videoMap.clear()
        idCounter = 0
    }

    override  fun findAll(data: MutableSet<Attribute>): List<Video> {
        fun doesNotMatch(video: CoreVideo): Boolean {
            val videoAttributes : MutableMap<String, Attribute> = video.data
            for (attr in data) {
                if (!videoAttributes.containsKey(attr.key) || videoAttributes[attr.key] != attr) return true
            }
            return false
        }

        val list = videoMap.values.toList()
        return if (data.size == 0) list else list.filter { doesNotMatch(it) }
    }

    override fun findById(id: Int): Video {
        return videoMap.getOrDefault(id, VideoError(ErrorKey.NoSuchVideo))
    }

    override fun findByName(name: kotlin.String): Video {
        val id = idMap.getOrDefault(name, -1)
        return if (id != -1) findById(id) else VideoError(ErrorKey.NoSuchVideo)
    }

    override fun register(name: kotlin.String): Video {
        fun isDuplicate() : Boolean {
            val id = idMap[name]
            val video = videoMap[id] ?: VideoError(ErrorKey.NoSuchVideo)
            return (video is CoreVideo && video.data.size == 1)
        }

        fun createVideo(): Video {
            fun processAttributes(video: CoreVideo) {
                fun registerVideoWithAttributes(video: CoreVideo, attrs: MutableMap<String, Attribute>) {
                    videoStore.add(video)
                    idMap[name] = idCounter
                    videoMap[idCounter++] = video
                    video.data.putAll(attrs)
                }

                val attributes : MutableMap<String, Attribute> = mutableMapOf()
                val nameAttribute = Name(name)
                attributes[nameAttribute.key] = nameAttribute
                registerVideoWithAttributes(video, attributes)
            }

            val video = CoreVideo(idCounter)
            processAttributes(video)
            return video
        }

        return if (isDuplicate()) VideoError(ErrorKey.AlreadyExists) else createVideo()
    }

    override fun update(id: Int, data: MutableSet<Attribute>): Video {
        fun processAttribute(video: Video, attribute: Attribute) {
            val key : String = attribute.key
            if (video is CoreVideo) video.data[key] = attribute
        }

        val video = findById(id)
        if (video is VideoError) return video
        for (attribute: Attribute in data)
            processAttribute(video, attribute)
        return video
    }

}
