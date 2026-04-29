package com.todolistapp.todolist.data.firebase

import com.google.android.gms.tasks.Task as GmsTask
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.todolistapp.todolist.data.model.Task
import com.todolistapp.todolist.data.model.TaskStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirestoreTaskRepository(private val userId: String) {

    private val firestore = FirebaseFirestore.getInstance()
    private val tasksRef = firestore
        .collection("users")
        .document(userId)
        .collection("tasks")

    fun observeAllTasks(): Flow<List<Task>> = observeQuery(tasksRef)

    fun observeTasksByStatus(status: TaskStatus): Flow<List<Task>> =
        observeQuery(tasksRef.whereEqualTo("status", status.name))

    private fun observeQuery(query: Query): Flow<List<Task>> = callbackFlow {
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val tasks = snapshot?.documents
                ?.mapNotNull { doc ->
                    doc.toObject(Task::class.java)?.copy(id = doc.id)
                }
                ?.sortedByDescending { it.createdAt }
                ?: emptyList()

            trySend(tasks)
        }

        awaitClose { registration.remove() }
    }

    suspend fun addTask(task: Task) {
        val doc = tasksRef.document()
        doc.set(task.copy(id = doc.id)).await()
    }

    suspend fun updateTask(task: Task) {
        tasksRef.document(task.id).set(task).await()
    }

    suspend fun deleteTask(task: Task) {
        tasksRef.document(task.id).delete().await()
    }

    private suspend fun <T> GmsTask<T>.await(): T =
        suspendCancellableCoroutine { continuation ->
            addOnSuccessListener { result ->
                if (continuation.isActive) continuation.resume(result)
            }
            addOnFailureListener { error ->
                if (continuation.isActive) continuation.resumeWithException(error)
            }
        }
}
