package com.todolistapp.todolist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.todolistapp.todolist.data.firebase.FirestoreTaskRepository
import com.todolistapp.todolist.data.model.Task
import com.todolistapp.todolist.data.model.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val userId: String) : ViewModel() {

    private val repository = FirestoreTaskRepository(userId)

    private val _filter = MutableStateFlow(FilterType.ALL)
    val filter: StateFlow<FilterType> = _filter.asStateFlow()

    private val _allExpanded = MutableStateFlow(false)
    val allExpanded: StateFlow<Boolean> = _allExpanded.asStateFlow()

    val tasks: StateFlow<List<Task>> = _filter.flatMapLatest { filterType ->
        when (filterType) {
            FilterType.ALL -> repository.observeAllTasks()
            FilterType.ACTIVE -> repository.observeTasksByStatus(TaskStatus.ACTIVE)
            FilterType.IN_PROGRESS -> repository.observeTasksByStatus(TaskStatus.IN_PROGRESS)
            FilterType.ON_HOLD -> repository.observeTasksByStatus(TaskStatus.ON_HOLD)
            FilterType.COMPLETED -> repository.observeTasksByStatus(TaskStatus.COMPLETED)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun setFilter(filter: FilterType) {
        _filter.value = filter
    }

    fun toggleAllExpanded() {
        _allExpanded.value = !_allExpanded.value
    }

    fun addTask(title: String, description: String, status: TaskStatus = TaskStatus.ACTIVE) {
        if (title.isBlank()) return

        viewModelScope.launch {
            repository.addTask(
                Task(
                    title = title.trim(),
                    description = description.trim(),
                    status = status.name
                )
            )
        }
    }

    fun updateTask(task: Task, newTitle: String, newDescription: String, newStatus: TaskStatus) {
        if (newTitle.isBlank()) return

        viewModelScope.launch {
            repository.updateTask(
                task.copy(
                    title = newTitle.trim(),
                    description = newDescription.trim(),
                    status = newStatus.name
                )
            )
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun setStatus(task: Task, status: TaskStatus) {
        viewModelScope.launch {
            repository.updateTask(task.copy(status = status.name))
        }
    }

    class Factory(private val userId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TaskViewModel(userId) as T
        }
    }
}
