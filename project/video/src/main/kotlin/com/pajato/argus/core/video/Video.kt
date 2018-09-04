package com.pajato.argus.core.video

import com.pajato.argus.core.data.Attribute
import com.pajato.argus.core.data.Video
import com.pajato.argus.core.data.VideoType

interface VideoRegistrar {
    fun register(name: kotlin.String, type: VideoType): Video
    fun update(id: Int, data: MutableSet<Attribute>): Video
    fun findByName(name: kotlin.String): Video
    fun findById(id: Int): Video
}

class VideoInteractor(private val registrar: VideoRegistrar) : VideoRegistrar {
    override fun update(id: Int, data: MutableSet<Attribute>): Video {
        return registrar.update(id, data)
    }

    override fun findByName(name: kotlin.String): Video {
        return registrar.findByName(name)
    }

    override fun findById(id: Int): Video {
        return registrar.findById(id)
    }

    override fun register(name: kotlin.String, type: VideoType): Video {
        return registrar.register(name, type)
    }
}
