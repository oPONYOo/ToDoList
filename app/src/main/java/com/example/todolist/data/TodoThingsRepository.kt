package com.example.todolist.data

import javax.inject.Inject

class TodoThingsRepository @Inject constructor(
    private val todoThingsLocalDataSource: TodoThingsLocalDataSource
) {
    val todoThings: List<TodoThingsModel> = todoThingsLocalDataSource.todoThings

    fun getToDoThings(mainTxt: String): TodoThingsModel? {
        return todoThingsLocalDataSource.todoThings.firstOrNull {
            it.mainTodo == mainTxt
        }
    }

}