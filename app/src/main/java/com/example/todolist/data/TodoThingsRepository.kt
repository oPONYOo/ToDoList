package com.example.todolist.data

import javax.inject.Inject

class TodoThingsRepository @Inject constructor(
    private val todoDao: TodoDao
//    private val todoThingsLocalDataSource: TodoThingsLocalDataSource
) {
//    val todoThings: List<TodoThingsModel> = todoThingsLocalDataSource.todoThings

    /*fun getToDoThings(mainTxt: String): TodoThingsModel? {
        return todoThingsLocalDataSource.todoThings.firstOrNull {
            it.mainTodo == mainTxt
        }
    }*/

    fun getRecords(): List<TodoDB> {
        return todoDao.getRecords()
    }

    fun insertRecords(todoEntity: TodoDB) {
        todoDao.insertRecords(todoEntity)
    }

    fun deleteRecords(todoEntity: TodoDB){
        todoDao.deleteRecords(todoEntity)
    }

}