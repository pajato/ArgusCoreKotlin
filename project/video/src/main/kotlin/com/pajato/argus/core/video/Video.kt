package com.pajato.argus.core.video

import com.pajato.argus.core.data.Attribute
import com.pajato.argus.core.data.Video

interface VideoRegistrar {
    fun findAll(data: MutableSet<Attribute> = mutableSetOf()): List<Video>
    fun findByName(name: kotlin.String): Video
    fun findById(id: Int): Video
    fun register(name: kotlin.String): Video
    fun update(id: Int, data: MutableSet<Attribute>): Video
}

class VideoInteractor(private val registrar: VideoRegistrar) : VideoRegistrar {

    override fun findAll(data: MutableSet<Attribute>): List<Video> {
        return registrar.findAll(data)
    }

    override fun findById(id: Int): Video {
        return registrar.findById(id)
    }

    override fun findByName(name: kotlin.String): Video {
        return registrar.findByName(name)
    }

    override fun register(name: kotlin.String): Video {
        return registrar.register(name)
    }

    override fun update(id: Int, data: MutableSet<Attribute>): Video {
        return registrar.update(id, data)
    }
}
