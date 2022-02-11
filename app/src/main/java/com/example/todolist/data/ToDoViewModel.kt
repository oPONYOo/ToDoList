package com.example.todolist.data

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ToDoViewModel @Inject constructor(
    private val repository: TodoThingsRepository
) : ViewModel() {

    var todoData : Flow<List<TodoDB>> = repository.todoThings


    fun insertRecord(todoEntity: TodoDB) {
        repository.insertRecords(todoEntity)
    }

    fun deleteRecord(todoEntity: TodoDB) {
        repository.deleteRecords(todoEntity)
    }

    fun updateRecord(todoEntity: TodoDB) {
        repository.updateRecords(todoEntity)
    }


}