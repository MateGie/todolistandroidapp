package com.todolistapp.todolist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.todolistapp.todolist.data.model.TaskStatus

@Composable
fun TaskStatus.color(): Color = when (this) {
    TaskStatus.ACTIVE -> MaterialTheme.colorScheme.primary
    TaskStatus.IN_PROGRESS -> Color(0xFFFF9800) // Orange
    TaskStatus.ON_HOLD -> MaterialTheme.colorScheme.outline
    TaskStatus.COMPLETED -> Color(0xFF4CAF50) // Green
}

@Composable
fun StatusDot(status: TaskStatus, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(10.dp)
            .background(color = status.color(), shape = CircleShape)
    )
}
