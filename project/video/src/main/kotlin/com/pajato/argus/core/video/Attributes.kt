package com.pajato.argus.core.video

object AttributeFactory {
    fun createAttribute(type: AttributeType, arg: String): Attribute? = when (type) {
        AttributeType.Cast -> Cast(mutableListOf<String>().apply { add(arg) })
        AttributeType.Directors -> Directors(mutableListOf<String>().apply { add(arg) })
        AttributeType.Name -> Name(arg)
        AttributeType.Provider -> Provider(arg)
        AttributeType.Release -> Release(getLong(arg))
        AttributeType.Series -> Series(mutableListOf<Series.Episode>().apply { add(arg.toEpisode()) })
        AttributeType.Type -> try { Type(VideoType.valueOf(arg)) } catch (exc: IllegalArgumentException) { null }
        else -> CoverageDefault()
    }
}

private fun String.toEpisode(): Series.Episode {
    val episodeRegex = """^(\d+) (\d+) ([\w]+) (.+)$""".toRegex()
    fun getEpisodeData(match: MatchResult): MutableMap<AttributeType, Attribute>? {
        val attributeType = AttributeType.valueOf(match.groupValues[3])
        val attributeArg = match.groupValues[4]
        val attribute = AttributeFactory.createAttribute(attributeType, attributeArg) ?: return null
        return mutableMapOf(attributeType to attribute)
    }
    return episodeRegex.find(this)?.let {
        val seriesNumber = it.groupValues[1].toInt()
        val episodeNumber = it.groupValues[2].toInt()
        val episodeData = getEpisodeData(it) ?: return Series.Episode()
        Series.Episode(seriesNumber, episodeNumber, episodeData)
    } ?: Series.Episode()
}

enum class AttributeType {
    Cast, Directors, Name, Provider, Release, Series, Type, CoverageDefault
}

sealed class Attribute {
    abstract val attrType: AttributeType
    abstract val updateByReplace: Boolean
    abstract val values: List<String>
    abstract fun isEqual(to: Attribute): Boolean
    open fun update(attribute: Attribute, type: UpdateType) {}
}

class Cast(val performers: MutableList<String>) : Attribute() {
    override val attrType = AttributeType.Cast
    override val updateByReplace = false
    override val values get() = performers

    override fun isEqual(to: Attribute): Boolean {
        return to is Cast && hasSameEntries(performers, to.performers)
    }

    override fun update(attribute: Attribute, type: UpdateType) {
        if (attribute !is Cast) return
        when (type) {
            UpdateType.Add -> performers.addAll(attribute.performers)
            UpdateType.Remove -> performers.removeAll(attribute.performers)
            UpdateType.RemoveAll -> return
            else -> return
        }
    }
}

class Directors(val directors: MutableList<String>) : Attribute() {
    override val attrType = AttributeType.Directors
    override val updateByReplace = false
    override val values get() = directors

    override fun isEqual(to: Attribute): Boolean {
        return to is Directors && hasSameEntries(directors, to.directors)
    }

    override fun update(attribute: Attribute, type: UpdateType) {
        if (attribute !is Directors) return
        when (type) {
            UpdateType.Add -> directors.addAll(attribute.directors)
            UpdateType.Remove -> directors.removeAll(attribute.directors)
            UpdateType.RemoveAll -> return
            else -> return
        }
    }
}

class Name(val name: String) : Attribute() {
    override val attrType = AttributeType.Name
    override val updateByReplace = true
    override val values get() = listOf(name)

    override fun isEqual(to: Attribute): Boolean {
        return to is Name && name == to.name
    }
}

class Provider(val name: String) : Attribute() {
    override val attrType = AttributeType.Provider
    override val updateByReplace = true
    override val values get() = listOf(name)

    override fun isEqual(to: Attribute): Boolean {
        return to is Provider && name == to.name
    }
}

class Release(val timeStamp: Long) : Attribute() {
    override val attrType = AttributeType.Release
    override val updateByReplace = true
    override val values get() = listOf(timeStamp.toString())

    override fun isEqual(to: Attribute): Boolean {
        return to is Release && timeStamp == to.timeStamp
    }
}

class Series(val episodes: MutableList<Episode>) : Attribute() {
    class Episode(
            val seriesNumber: Int = 0,
            val episodeNumber: Int = 0,
            val episodeData: MutableMap<AttributeType, Attribute> = mutableMapOf()
    )
    override val attrType = AttributeType.Series
    override val updateByReplace = false
    override val values: List<String>
        get() {
            val list = mutableListOf<String>()
            for (episode in episodes)
                for (episodeAttribute in episode.episodeData.values)
                    for (value in episodeAttribute.values)
                        list.add("${episode.seriesNumber} ${episode.episodeNumber} ${episodeAttribute.attrType.name}" +
                            value)
            return list
        }
    override fun isEqual(to: Attribute): Boolean {
        return to == this
    }

    override fun update(attribute: Attribute, type: UpdateType) {
        fun getMatchingEpisodes(candidateEpisodes: List<Episode>): List<Episode> {
            val list = mutableListOf<Episode>()
            candidateEpisodes.forEach {
                for (episode in episodes)
                    if (it.seriesNumber == episode.seriesNumber && it.episodeNumber == episode.episodeNumber)
                        list.add(episode)
            }
            return list
        }
        fun preventRecursionByFilteringSeriesAttributeFromEachEpisode(series: Series) {
            fun pruneSeriesFromEpisodeData(episode: Episode) {
                var attributeToDelete: Attribute? = null
                for (episodeAttribute in episode.episodeData.values)
                    if (episodeAttribute is Series)
                        attributeToDelete = episodeAttribute
                if (attributeToDelete != null)
                    episode.episodeData.remove(attributeToDelete.attrType)
            }

            for (episode in series.episodes) {
                pruneSeriesFromEpisodeData(episode)
            }

        }
        fun remove(candidateEpisodes: List<Episode>) {
            val episodesToDelete = getMatchingEpisodes(candidateEpisodes)
            episodes.removeAll(episodesToDelete)
        }
        fun removeAndAddAll(candidateEpisodes: List<Episode>) {
            remove(candidateEpisodes)
            episodes.addAll(candidateEpisodes)
        }

        if (attribute !is Series) return
        preventRecursionByFilteringSeriesAttributeFromEachEpisode(attribute)
        when (type) {
            UpdateType.Add -> removeAndAddAll(attribute.episodes)
            UpdateType.Remove -> remove(attribute.episodes)
            UpdateType.RemoveAll -> episodes.removeAll(episodes)
            else -> return
        }
    }
}

class Type(val type: VideoType) : Attribute() {
    override val attrType = AttributeType.Type
    override val updateByReplace = true
    override val values get() = listOf(type.name)

    override fun isEqual(to: Attribute): Boolean {
        return to is Type && type == to.type
    }
}

class CoverageDefault : Attribute() {
    override val attrType = AttributeType.CoverageDefault
    override val updateByReplace = false
    override val values get() = listOf<String>()

    override fun isEqual(to: Attribute): Boolean {
        return false
    }
}

fun getLong(arg: String): Long = try { arg.toLong() } catch (exc: NumberFormatException) { 0L }

fun hasSameEntries(first: List<String>, second: List<String>): Boolean {
    if (first.size != second.size) return false
    for (string in first)
        if (!second.contains(string)) return false
    return true
}
