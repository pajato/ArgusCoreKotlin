package com.pajato.argus.core.video

interface VideoRegistrar {
    fun findAll(filterData: MutableSet<Attribute> = mutableSetOf()): List<Video>
    fun findByName(name: kotlin.String): Video
    fun findById(videoId: Long): Video
    fun register(name: kotlin.String): Video
    fun update(videoId: Long, videoData: MutableSet<Attribute>, updateType: UpdateType): Video
}

class VideoInteractor(private val registrar: VideoRegistrar) : VideoRegistrar {

    override fun findAll(filterData: MutableSet<Attribute>): List<Video> {
        return registrar.findAll(filterData)
    }

    override fun findById(videoId: Long): Video {
        return registrar.findById(videoId)
    }

    override fun findByName(name: kotlin.String): Video {
        return registrar.findByName(name)
    }

    override fun register(name: kotlin.String): Video {
        return registrar.register(name)
    }

    override fun update(videoId: Long, videoData: MutableSet<Attribute>, updateType: UpdateType): Video {
        return registrar.update(videoId, videoData, updateType)
    }
}
