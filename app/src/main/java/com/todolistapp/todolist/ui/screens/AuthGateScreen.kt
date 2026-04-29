package com.todolistapp.todolist.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.todolistapp.todolist.viewmodel.AuthViewModel

@Composable
fun AuthGateScreen(authViewModel: AuthViewModel = viewModel()) {
    val uiState = authViewModel.uiState.collectAsStateWithLifecycle().value

    if (uiState.userId == null) {
        AuthScreen(
            uiState = uiState,
            onEmailChange = authViewModel::onEmailChange,
            onPasswordChange = authViewModel::onPasswordChange,
            onLogin = authViewModel::login,
            onRegister = authViewModel::register
        )
    } else {
        TodoListScreen(
            userId = uiState.userId,
            onLogout = authViewModel::logout
        )
    }
}
