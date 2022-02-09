package com.example.todolist.data

import androidx.room.*

@Dao
interface TodoDao {

    @Query("SELECT * FROM todoList ORDER BY id DESC")
    fun getRecords(): List<TodoDB>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTodoThings(vararg todoList: TodoDB)

    @Insert
    fun insertRecords(todoEntity: TodoDB)

    @Query("INSERT INTO todoList(mainTxt,subTxt)VALUES(:mainTxt,:subTxt)")
    fun myInsertTodo(mainTxt: String?, subTxt: String?)

    @Delete
    fun deleteRecords(todoEntity: TodoDB)
}