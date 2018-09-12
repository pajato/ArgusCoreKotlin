package com.pajato.argus.core.data

sealed class Video

class CoreVideo(val id: Int, val data: MutableMap<String, Attribute> = mutableMapOf()) : Video()

class VideoError(val key: ErrorKey, val message: String = "") : Video()

class Episode(val attributes: MutableList<Attribute>)
class SeriesInstance(val episodes: MutableList<Episode>)

sealed class Attribute {
    abstract val key: String
}

class Cast(val performers: List<String>) : Attribute() {override val key = "Cast"}
class Directors(val directors: List<String>) : Attribute() {override val key = "Directors"}
class Name(val name: String) : Attribute() {override val key = "Name"}
class Provider(val name: String) : Attribute() {override val key = "Provider"}
class Release(val timeStamp: Long) : Attribute() {override val key = "Release"}
class Series(val series: MutableList<SeriesInstance>) : Attribute() {override val key = "Series"}
class Type(val type: VideoType) : Attribute() {override val key = "Type"}

enum class VideoType {
    Error, Movie, MovieSeries, TvShow, TvSeries
}

enum class ErrorKey {
    AlreadyExists, NoSuchVideo, Ok, UnsupportedVideoType
}
