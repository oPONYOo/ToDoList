package com.example.todolist.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TodoThingsRepository @Inject constructor(
    private val todoDao: TodoDao
//    private val todoThingsLocalDataSource: TodoThingsLocalDataSource
) {
    val todoThings: Flow<List<TodoDB>> = todoDao.getRecords()

    /*fun getToDoThings(mainTxt: String): TodoThingsModel? {
        return todoThingsLocalDataSource.todoThings.firstOrNull {
            it.mainTodo == mainTxt
        }
    }*/

    fun getRecords(): Flow<List<TodoDB>> {
        return todoDao.getRecords()
    }

//    suspend fun getRecords() = todoDao.getRecords()

    fun insertRecords(todoEntity: TodoDB) {
        todoDao.insertRecords(todoEntity)
    }

    fun deleteRecords(todoEntity: TodoDB){
        todoDao.deleteRecords(todoEntity)
    }

    fun updateRecords(todoEntity: TodoDB){
        todoDao.updateRecords(todoEntity)
    }

}