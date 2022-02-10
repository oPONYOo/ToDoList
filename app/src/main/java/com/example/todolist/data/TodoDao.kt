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

    @Query("INSERT INTO todoList(mainTxt,subTxt,`check`)VALUES(:mainTxt,:subTxt,:check)")
    fun myInsertTodo(mainTxt: String?, subTxt: String?, check: Boolean?)

    @Delete
    fun deleteRecords(todoEntity: TodoDB)

    @Update
    fun updateRecords(todoEntity: TodoDB)

}