package com.pajato.argus.core.video

enum class VideoType {
    Error, Movie, MovieSeries, TvShow, TvSeries
}

enum class ErrorKey {
    AlreadyExists, NoSuchVideo, Ok, UnsupportedVideoType
}

sealed class Video

class CoreVideo(val videoId: Long, val videoData: MutableMap<AttributeType, Attribute> = mutableMapOf()) : Video() {
    var archived = false
    fun updateAttribute(attribute: Attribute, type: UpdateType) {
        fun updateUsingAdd()  {
            val videoAttribute = videoData[attribute.attrType]
            if (videoAttribute == null || attribute.updateByReplace)
                videoData[attribute.attrType] = attribute
            else
                videoAttribute.update(attribute, type)
        }
        fun updateUsingRemove() {
            if (videoData.containsKey(attribute.attrType) && attribute.updateByReplace)
                videoData.remove(attribute.attrType)
            else
                videoData[attribute.attrType]?.update(attribute, type)
        }

        when (type) {
            UpdateType.Add -> updateUsingAdd()
            UpdateType.Remove -> updateUsingRemove()
            UpdateType.RemoveAll -> if (videoData.containsKey(attribute.attrType)) videoData.remove(attribute.attrType)
            else -> return
        }
    }
}

class VideoError(val key: ErrorKey, val message: String = "") : Video()

enum class VideoEventType {
    Archive, Register, Update, CoverageDefault
}

enum class UpdateType {
    Add, Remove, RemoveAll, CoverageDefault
}

sealed class VideoEvent {
    abstract val type: VideoEventType
}

class ArchiveEvent(val id: Long): VideoEvent() { override val type = VideoEventType.Archive }
class RegisterEvent(val id: Long, val name: String): VideoEvent() { override val type = VideoEventType.Register }
class UpdateEvent(
        val subtype: UpdateType,
        val id: Long,
        val attrType: AttributeType,
        val value: String
): VideoEvent() {
    override val type = VideoEventType.Update
}

class CoverageDefaultEvent : VideoEvent() { override val type = VideoEventType.CoverageDefault }
