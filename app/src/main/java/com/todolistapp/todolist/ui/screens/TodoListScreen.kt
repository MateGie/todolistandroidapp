package com.todolistapp.todolist.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.todolistapp.todolist.data.model.Task
import com.todolistapp.todolist.ui.components.AddEditTaskDialog
import com.todolistapp.todolist.ui.components.FilterBar
import com.todolistapp.todolist.ui.components.TaskItem
import com.todolistapp.todolist.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    userId: String,
    onLogout: () -> Unit
) {
    val viewModel: TaskViewModel = viewModel(
        key = userId,
        factory = TaskViewModel.Factory(userId)
    )

    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val allExpanded by viewModel.allExpanded.collectAsStateWithLifecycle()

    val expandedIds = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(allExpanded, tasks) {
        tasks.forEach { task ->
            if (task.description.isNotBlank()) {
                expandedIds[task.id] = allExpanded
            }
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moje zadania") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (tasks.any { it.description.isNotBlank() }) {
                        IconButton(onClick = { viewModel.toggleAllExpanded() }) {
                            Icon(
                                imageVector = if (allExpanded) {
                                    Icons.Default.KeyboardArrowUp
                                } else {
                                    Icons.Default.KeyboardArrowDown
                                },
                                contentDescription = if (allExpanded) "Zwiń wszystkie" else "Rozwiń wszystkie"
                            )
                        }
                    }

                    TextButton(onClick = onLogout) {
                        Text("Wyloguj")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj zadanie")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            FilterBar(
                selectedFilter = filter,
                onFilterSelected = viewModel::setFilter
            )

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Brak zadań.\nDotknij + aby dodać nowe.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        val isExpanded = expandedIds[task.id] ?: false
                        TaskItem(
                            task = task,
                            isExpanded = isExpanded,
                            onToggleExpand = {
                                expandedIds[task.id] = !isExpanded
                            },
                            onEdit = { taskToEdit = task },
                            onDelete = { taskToDelete = task }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditTaskDialog(
            onConfirm = { title, desc, status ->
                viewModel.addTask(title, desc, status)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    taskToEdit?.let { task ->
        AddEditTaskDialog(
            task = task,
            onConfirm = { title, desc, status ->
                viewModel.updateTask(task, title, desc, status)
                taskToEdit = null
            },
            onDismiss = { taskToEdit = null }
        )
    }

    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Usuń zadanie") },
            text = { Text("Czy na pewno chcesz usunąć \"${task.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTask(task)
                    taskToDelete = null
                }) {
                    Text("Usuń", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text("Anuluj")
                }
            }
        )
    }
}