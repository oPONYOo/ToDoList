package com.example.todolist.data

import java.io.Serializable

data class Todo(
    var mainTodo: String = "",
    var description: String ="",
    var onEdit: Boolean = true,
): Serializable{

}
