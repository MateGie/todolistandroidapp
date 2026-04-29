package com.todolistapp.todolist.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.todolistapp.todolist.viewmodel.FilterType

fun FilterType.label(): String = when (this) {
    FilterType.ALL -> "Wszystkie"
    FilterType.ACTIVE -> "Aktywne"
    FilterType.IN_PROGRESS -> "W trakcie"
    FilterType.ON_HOLD -> "Odłożone"
    FilterType.COMPLETED -> "Ukończone"
}

@Composable
fun FilterBar(
    selectedFilter: FilterType,
    onFilterSelected: (FilterType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterType.entries.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.label()) }
            )
        }
    }
}
