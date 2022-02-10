package com.example.todolist.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todolist.util.Converters

@Database(
    entities = [TodoDB::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {


    abstract fun getDao(): TodoDao

    companion object {
        private var dbInstance: TodoDatabase? = null

        fun getAppDB(context: Context): TodoDatabase {
            if (dbInstance == null) {
                dbInstance = Room.databaseBuilder<TodoDatabase>(
                    context.applicationContext, TodoDatabase::class.java, "TODO"
                )
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2)
                    .build()

            }
            return dbInstance!!
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'todoList' ADD COLUMN 'check' INTEGER")
            }

        }

//        private val MIGRATION_2_3 = object : Migration(2, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("DROP TABLE 'todoList'  'check' Boolean")
//            }
//
//        }


    }



}