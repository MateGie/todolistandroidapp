package com.todolistapp.todolist.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val status: String = TaskStatus.ACTIVE.name,
    val createdAt: Long = System.currentTimeMillis()
) {
    val statusEnum: TaskStatus
        get() = runCatching { TaskStatus.valueOf(status) }.getOrDefault(TaskStatus.ACTIVE)
}
