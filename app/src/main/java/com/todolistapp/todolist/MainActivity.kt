package com.todolistapp.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.todolistapp.todolist.ui.screens.TodoListScreen
import com.todolistapp.todolist.ui.theme.ToDoListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                TodoListScreen()
            }
        }
    }
}
