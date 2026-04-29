package com.todolistapp.todolist.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.todolistapp.todolist.data.model.Task
import com.todolistapp.todolist.data.model.TaskStatus

@Composable
fun TaskItem(
    task: Task,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    dismissState: androidx.compose.material3.SwipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )
) {
    val status = task.statusEnum
    val itemColor = status.color()
    val isCompleted = status == TaskStatus.COMPLETED
    val hasLongDescription = task.description.isNotBlank()
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "arrowRotation"
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                    else -> Color.Transparent
                },
                animationSpec = tween(durationMillis = 150),
                label = "dismissBackground"
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Usuń",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = itemColor.copy(alpha = 0.12f).compositeOver(MaterialTheme.colorScheme.surface)
            ),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(animationSpec = tween(durationMillis = 100))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusDot(
                        status = status,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                            color = if (isCompleted)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                            else
                                MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (hasLongDescription && !isExpanded) {
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (hasLongDescription) {
                        IconButton(
                            onClick = onToggleExpand,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isExpanded) "Zwiń" else "Rozwiń",
                                modifier = Modifier.rotate(arrowRotation)
                            )
                        }
                    }

                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edytuj")
                    }
                }

                AnimatedVisibility(
                    visible = isExpanded && hasLongDescription,
                    enter = expandVertically(animationSpec = tween(durationMillis = 100)) +
                            fadeIn(animationSpec = tween(durationMillis = 100)),
                    exit = shrinkVertically(animationSpec = tween(durationMillis = 100)) +
                           fadeOut(animationSpec = tween(durationMillis = 100))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 32.dp, end = 16.dp, bottom = 12.dp)
                    ) {
                        HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }
    }
}
