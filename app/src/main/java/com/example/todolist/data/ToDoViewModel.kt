package com.example.todolist.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.di.DefaultDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ToDoViewModel @Inject constructor(
    private val toDoRepository: TodoThingsRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
    ): ViewModel(){
    val mianTxt: List<TodoThingsModel> = toDoRepository.todoThings

    private val _toDoThings = MutableLiveData<List<TodoThingsModel>>()
    val toDoThings: LiveData<List<TodoThingsModel>>
        get() = _toDoThings

    init {
        _toDoThings.value = toDoRepository.todoThings
    }

    fun toDoThingsChanged(newToDo: String) {
        viewModelScope.launch {
            val newTodoThings = withContext(defaultDispatcher) {
                toDoRepository.todoThings
                    .filter { it.mainTodo.contains(newToDo) }
            }
            _toDoThings.value = newTodoThings
        }
    }
}