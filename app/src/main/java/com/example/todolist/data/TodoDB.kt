package com.example.todolist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todoList")
 class TodoDB(
    @PrimaryKey(autoGenerate = true)@ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "mainTxt") val mainTxt: String? = null,
    @ColumnInfo(name = "subTxt") val subTxt: String? = null,
    @ColumnInfo(name = "check") val check: Boolean? = false
)