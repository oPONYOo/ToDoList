package com.example.todolist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TodoDB::class], version = 1, exportSchema = false
)

abstract class TodoDatabase: RoomDatabase() {


    abstract fun getDao():TodoDao

    companion object {
        private var dbInstance: TodoDatabase? = null

        fun getAppDB(context: Context): TodoDatabase{
            if (dbInstance == null){
                dbInstance = Room.databaseBuilder<TodoDatabase>(
                    context.applicationContext, TodoDatabase::class.java, "TODO"
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return dbInstance!!
        }
    }

}