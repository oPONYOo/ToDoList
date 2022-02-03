package com.example.todolist.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringArrayResource
import com.example.todolist.R

@Composable
fun Home(){
    val allTasks = stringArrayResource(R.array.tasks)
    val tasks = remember { mutableStateListOf(*allTasks) }
}