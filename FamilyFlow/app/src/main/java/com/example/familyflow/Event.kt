package com.example.familyflow.util

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * This prevents events from being handled multiple times.
 */
class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    // Data class to represent calendar events
    data class Event(
        val date: String,  // Date as a string (e.g., "14" for 14th of the month)
        val title: String  // Title or description of the event
    )

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}