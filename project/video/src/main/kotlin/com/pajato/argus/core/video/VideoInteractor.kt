package com.pajato.argus.core.video

interface VideoRegistrar {
    fun findAll(filterData: MutableSet<Attribute> = mutableSetOf()): List<Video>
    fun findByName(name: kotlin.String): Video
    fun findById(id: Long): Video
    fun register(name: kotlin.String): Video
    fun update(id: Long, data: MutableSet<Attribute>, type: UpdateType): Video
}

class VideoInteractor(private val registrar: VideoRegistrar) : VideoRegistrar {

    override fun findAll(filterData: MutableSet<Attribute>): List<Video> {
        return registrar.findAll(filterData)
    }

    override fun findById(id: Long): Video {
        return registrar.findById(id)
    }

    override fun findByName(name: kotlin.String): Video {
        return registrar.findByName(name)
    }

    override fun register(name: kotlin.String): Video {
        return registrar.register(name)
    }

    override fun update(id: Long, data: MutableSet<Attribute>, type: UpdateType): Video {
        return registrar.update(id, data, type)
    }
}
