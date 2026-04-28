package com.todolistapp.todolist.data.repository

import com.todolistapp.todolist.data.local.TaskDao
import com.todolistapp.todolist.data.model.Task
import com.todolistapp.todolist.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>> =
        taskDao.getTasksByStatus(status)

    suspend fun addTask(task: Task) = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
}
