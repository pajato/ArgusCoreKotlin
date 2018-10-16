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
    fun updateWithStore(videoData: MutableSet<Attribute> = mutableSetOf(), updateKey: String, persister: Persister): Video {
        val updater = UpdaterFactory.create(updateKey, this) ?: return this
        for (attribute in videoData) {
            updater.update(attribute)
            attribute.persist(updateKey, videoId, persister)
        }
        return this
    }

    fun updateForLoad(videoData: MutableSet<Attribute> = mutableSetOf(), updateKey: String): Video {
        val updater = UpdaterFactory.create(updateKey, this) ?: return this
        for (attribute in videoData)
            updater.update(attribute)
        return this
    }
}

class VideoError(val key: ErrorKey, val message: String = "") : Video()
