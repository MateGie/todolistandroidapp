package com.todolistapp.todolist.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.todolistapp.todolist.data.model.Task
import com.todolistapp.todolist.data.model.TaskStatus

fun TaskStatus.label(): String = when (this) {
    TaskStatus.ACTIVE      -> "Aktywne"
    TaskStatus.IN_PROGRESS -> "W trakcie"
    TaskStatus.ON_HOLD     -> "Odlozone"
    TaskStatus.COMPLETED   -> "Ukonczone"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskDialog(
    task: Task? = null,
    onConfirm: (title: String, description: String, status: TaskStatus) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var selectedStatus by remember { mutableStateOf(task?.status ?: TaskStatus.ACTIVE) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Nowe zadanie" else "Edytuj zadanie") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tytul *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    isError = title.isBlank()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Opis (opcjonalny)") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )

                // Status dropdown
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedStatus.label(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        TaskStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.label()) },
                                onClick = {
                                    selectedStatus = status
                                    dropdownExpanded = false
                                },
                                leadingIcon = {
                                    StatusDot(status = status)
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (title.isNotBlank()) onConfirm(title, description, selectedStatus) },
                enabled = title.isNotBlank()
            ) { Text(if (task == null) "Dodaj" else "Zapisz") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        }
    )
}
