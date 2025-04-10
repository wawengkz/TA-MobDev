package com.example.familyflow.data.converter

import androidx.room.TypeConverter
import org.json.JSONArray
import org.json.JSONException

/**
 * Type converter for Room database to convert between Set<String> and String
 */
class StringSetConverter {
    @TypeConverter
    fun fromString(value: String?): Set<String> {
        if (value == null) return emptySet()

        return try {
            val jsonArray = JSONArray(value)
            val stringSet = mutableSetOf<String>()

            for (i in 0 until jsonArray.length()) {
                stringSet.add(jsonArray.getString(i))
            }

            stringSet
        } catch (e: JSONException) {
            emptySet()
        }
    }

    @TypeConverter
    fun fromSet(set: Set<String>?): String {
        if (set == null) return "[]"

        val jsonArray = JSONArray()
        set.forEach { jsonArray.put(it) }

        return jsonArray.toString()
    }
}