package com.todolistapp.todolist.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun TodoListScreen(viewModel: TaskViewModel = viewModel()) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val allExpanded by viewModel.allExpanded.collectAsStateWithLifecycle()

    // Per-task expanded state: taskId -> Boolean
    val expandedIds = remember { mutableStateMapOf<Long, Boolean>() }

    // When allExpanded changes, sync all visible tasks
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
                title = { Text("Moje Zadania") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    // Expand / Collapse all button
                    val hasAnyDescription = tasks.any { it.description.isNotBlank() }
                    if (hasAnyDescription) {
                        IconButton(onClick = { viewModel.toggleAllExpanded() }) {
                            Icon(
                                imageVector = if (allExpanded)
                                    Icons.Default.KeyboardArrowUp
                                else
                                    Icons.Default.KeyboardArrowDown,
                                contentDescription = if (allExpanded) "Zwij wszystkie" else "Rozwij wszystkie"
                            )
                        }
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
                        text = "Brak zadan.\nDotknij + aby dodac nowe.",
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

    // --- Add Dialog ---
    if (showAddDialog) {
        AddEditTaskDialog(
            onConfirm = { title, desc, status ->
                viewModel.addTask(title, desc, status)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    // --- Edit Dialog ---
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

    // --- Delete Confirmation ---
    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Usun zadanie") },
            text = { Text("Czy na pewno chcesz usunac \"${task.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTask(task)
                    taskToDelete = null
                }) { Text("Usun", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) { Text("Anuluj") }
            }
        )
    }
}

