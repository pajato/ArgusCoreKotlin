package com.pajato.argus.core.data

sealed class Video {
    abstract val type: VideoType
    abstract val data: MutableMap<String, Attribute>
}

class TVShow(val id: Int) : Video() {
    override val type = VideoType.tvShow
    override val data = mutableMapOf<String, Attribute>()
}

class Movie(val id: Int) : Video() {
    override val type = VideoType.movie
    override val data = mutableMapOf<String, Attribute>()
}

class VideoError(val key: ErrorKey) : Video() {
    override val type = VideoType.error
    override val data = mutableMapOf<String, Attribute>()
}

class Episode(val attributes: MutableList<Attribute>) {}
class SeriesInstance(val episodes: MutableList<Episode>) {}

sealed class Attribute {
    abstract val key: String
}

class Provider(val name: String) : Attribute() {override val key = "Provider"}
class Cast(val performers: List<String>) : Attribute() {override val key = "Cast"}
class Directors(val directors: List<String>) : Attribute() {override val key = "Directors"}
class Series(val series: MutableList<SeriesInstance>) : Attribute() {override val key = "Series"}
class Name(val name: String) : Attribute() {override val key = "Name"}
class Release(val timeStamp: Long) : Attribute() {override val key = "Release"}

enum class VideoType {
    error, movie, movieSeries, tvShow, tvSeries
}

enum class ErrorKey {
    AlreadyExists, NoSuchVideo, Ok, UnsupportedVideoType
}
