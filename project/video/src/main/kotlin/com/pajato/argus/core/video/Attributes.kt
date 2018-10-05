package com.pajato.argus.core.video

import java.lang.IllegalArgumentException
import java.lang.NumberFormatException

object AttributeFactory {
    fun createAttribute(type: AttributeType, arg: String): Attribute? = when (type) {
        AttributeType.Cast -> Cast(mutableListOf<String>().apply { add(arg) })
        AttributeType.Directors -> Directors(mutableListOf<String>().apply { add(arg) })
        AttributeType.Name -> Name(arg)
        AttributeType.Provider -> Provider(arg)
        AttributeType.Release -> Release(getLong(arg))
        AttributeType.Type -> try { Type(VideoType.valueOf(arg)) } catch (exc: IllegalArgumentException) { null }
        else -> CoverageDefault()
    }
}

enum class AttributeType {
    Cast, Directors, Name, Provider, Release, Type, CoverageDefault
}

sealed class Attribute {
    abstract val attrType: AttributeType
    abstract val updateByReplace: Boolean
    abstract fun isEqual(to: Attribute): Boolean
    open fun update(attribute: Attribute, type: UpdateType) {}
}

class Cast(val performers: MutableList<String>) : Attribute() {
    override val attrType = AttributeType.Cast
    override val updateByReplace = false

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

    override fun isEqual(to: Attribute): Boolean {
        return to is Name && name == to.name
    }
}

class Provider(val name: String) : Attribute() {
    override val attrType = AttributeType.Provider
    override val updateByReplace = true

    override fun isEqual(to: Attribute): Boolean {
        return to is Provider && name == to.name
    }
}

class Release(val timeStamp: Long) : Attribute() {
    override val attrType = AttributeType.Release
    override val updateByReplace = true

    override fun isEqual(to: Attribute): Boolean {
        return to is Release && timeStamp == to.timeStamp
    }
}

class Type(val type: VideoType) : Attribute() {
    override val attrType = AttributeType.Type
    override val updateByReplace = true

    override fun isEqual(to: Attribute): Boolean {
        return to is Type && type == to.type
    }
}

class CoverageDefault : Attribute() {
    override val attrType = AttributeType.CoverageDefault
    override val updateByReplace = false

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
