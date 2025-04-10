package com.example.familyflow.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.familyflow.data.converter.StringSetConverter

@Entity(tableName = "tasks")
@TypeConverters(StringSetConverter::class)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val days: Set<String>,
    val assignedTo: String?,
    val isDone: Boolean = false,
    val roomType: String // "kitchen", "bathroom", "livingroom"
)