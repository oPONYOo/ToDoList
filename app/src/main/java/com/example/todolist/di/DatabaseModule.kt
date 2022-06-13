package com.example.todolist.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.todolist.data.TodoDao
import com.example.todolist.data.TodoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Singleton
    @Provides
    fun getAppDB(context: Application): TodoDatabase{
        return TodoDatabase.getAppDB(context)
    }

    @Singleton
    @Provides
    fun getDao(appDB: TodoDatabase): TodoDao {
        return appDB.getDao()
    }

}

