package com.example.todolist.data

import androidx.compose.runtime.Immutable


@Immutable
data class TodoThingsModel(
    val mainTodo: String,
    val description: String,
    val isChecked: Boolean
)