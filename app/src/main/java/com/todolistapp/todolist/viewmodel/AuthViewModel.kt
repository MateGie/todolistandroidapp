package com.todolistapp.todolist.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userId: String? = null
)

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(
        AuthUiState(userId = auth.currentUser?.uid)
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _uiState.update {
            it.copy(
                userId = firebaseAuth.currentUser?.uid,
                isLoading = false
            )
        }
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun login() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Wpisz email i hasło.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _uiState.update { it.copy(isLoading = false, errorMessage = null) }
            }
            .addOnFailureListener { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Błąd logowania"
                    )
                }
            }
    }

    fun register() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Wpisz email i hasło.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _uiState.update { it.copy(isLoading = false, errorMessage = null) }
            }
            .addOnFailureListener { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Błąd rejestracji"
                    )
                }
            }
    }

    fun logout() {
        auth.signOut()
        _uiState.update {
            it.copy(
                email = "",
                password = "",
                isLoading = false,
                errorMessage = null,
                userId = null
            )
        }
    }
}
