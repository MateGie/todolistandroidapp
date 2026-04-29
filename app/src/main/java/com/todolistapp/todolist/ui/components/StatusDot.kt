package com.todolistapp.todolist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.todolistapp.todolist.data.model.TaskStatus

fun TaskStatus.color(): Color = when (this) {
    TaskStatus.ACTIVE -> Color(0xFF2196F3)
    TaskStatus.IN_PROGRESS -> Color(0xFFFF9800)
    TaskStatus.ON_HOLD -> Color(0xFF9E9E9E)
    TaskStatus.COMPLETED -> Color(0xFF4CAF50)
}

@Composable
fun StatusDot(status: TaskStatus, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(10.dp)
            .background(color = status.color(), shape = CircleShape)
    )
}
