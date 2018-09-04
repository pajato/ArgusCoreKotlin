package test

import com.pajato.argus.core.data.Attribute
import com.pajato.argus.core.data.ErrorKey
import com.pajato.argus.core.data.Movie
import com.pajato.argus.core.data.Name
import com.pajato.argus.core.data.TVShow
import com.pajato.argus.core.data.Video
import com.pajato.argus.core.data.VideoError
import com.pajato.argus.core.data.VideoType
import com.pajato.argus.core.video.VideoRegistrar

class TestRegistrar : VideoRegistrar {
    val videoStore : MutableList<Video> = mutableListOf()

    private val idMap: MutableMap<kotlin.String, Int> = mutableMapOf()
    private val videoMap: MutableMap<Int, Video> = mutableMapOf()
    private val attrMap: MutableMap<Video, MutableMap<String, Attribute>> = mutableMapOf()
    private var idCounter = 0

    override fun update(id: Int, data: MutableSet<Attribute>): Video {
        fun processAttribute(video: Video, attribute: Attribute) {
            val key : String = attribute.key
            video.data[key] = attribute
        }

        val video = findById(id)
        if (video.type == VideoType.error) return video
        for (attribute: Attribute in data)
            processAttribute(video, attribute)
        return video
    }

    override fun register(name: kotlin.String, type: VideoType): Video {
        fun validate() : ErrorKey {
            fun validateType(): ErrorKey {
                val id = idMap[name]
                val key = ErrorKey.NoSuchVideo
                val video = videoMap[id] ?: VideoError(key)
                return if (video.type != type) ErrorKey.Ok else ErrorKey.AlreadyExists
            }

            return if (!idMap.containsKey(name)) ErrorKey.Ok else validateType()
        }

        fun createVideo(): Video {
            fun createBasicVideo(): Video {
                return when (type) {
                    VideoType.tvShow -> TVShow(idCounter)
                    VideoType.movie -> Movie(idCounter)
                    else -> VideoError(ErrorKey.UnsupportedVideoType)
                }
            }

            fun registerVideoWithAttributes(video: Video, attrs: MutableMap<String, Attribute>) {
                videoStore.add(video)
                idMap[name] = idCounter
                videoMap[idCounter++] = video
                attrMap[video] = attrs
            }

            val video = createBasicVideo()
            val attributes : MutableMap<String, Attribute> = mutableMapOf()
            val nameAttribute = Name(name)
            attributes[nameAttribute.key] = nameAttribute
            registerVideoWithAttributes(video, attributes)
            return video
        }

        val reasonKey = validate()
        return if (reasonKey == ErrorKey.Ok) createVideo() else VideoError(reasonKey)
    }

    override fun findByName(name: kotlin.String): Video {
        val id = idMap.getOrDefault(name, -1)
        return if (id != -1) findById(id) else VideoError(ErrorKey.NoSuchVideo)
    }

    override fun findById(id: Int): Video {
        return videoMap.getOrDefault(id, VideoError(ErrorKey.NoSuchVideo))
    }

}
