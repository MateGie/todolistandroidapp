package com.todolistapp.todolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.todolistapp.todolist.data.local.AppDatabase
import com.todolistapp.todolist.data.model.Task
import com.todolistapp.todolist.data.model.TaskStatus
import com.todolistapp.todolist.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    private val _filter = MutableStateFlow(FilterType.ALL)
    val filter: StateFlow<FilterType> = _filter.asStateFlow()

    // Global expand/collapse all
    private val _allExpanded = MutableStateFlow(false)
    val allExpanded: StateFlow<Boolean> = _allExpanded.asStateFlow()

    val tasks: StateFlow<List<Task>>

    init {
        val dao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(dao)

        tasks = _filter.flatMapLatest { filterType ->
            when (filterType) {
                FilterType.ALL         -> repository.getAllTasks()
                FilterType.ACTIVE      -> repository.getTasksByStatus(TaskStatus.ACTIVE)
                FilterType.IN_PROGRESS -> repository.getTasksByStatus(TaskStatus.IN_PROGRESS)
                FilterType.ON_HOLD     -> repository.getTasksByStatus(TaskStatus.ON_HOLD)
                FilterType.COMPLETED   -> repository.getTasksByStatus(TaskStatus.COMPLETED)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    }

    fun setFilter(filter: FilterType) {
        _filter.value = filter
    }

    fun toggleAllExpanded() {
        _allExpanded.value = !_allExpanded.value
    }

    fun addTask(title: String, description: String, status: TaskStatus = TaskStatus.ACTIVE) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addTask(Task(title = title.trim(), description = description.trim(), status = status))
        }
    }

    fun updateTask(task: Task, newTitle: String, newDescription: String, newStatus: TaskStatus) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            repository.updateTask(
                task.copy(title = newTitle.trim(), description = newDescription.trim(), status = newStatus)
            )
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    fun setStatus(task: Task, status: TaskStatus) {
        viewModelScope.launch { repository.updateTask(task.copy(status = status)) }
    }
}
