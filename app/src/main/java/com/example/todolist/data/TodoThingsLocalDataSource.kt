package com.example.todolist.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoThingsLocalDataSource @Inject constructor() {
    val todoThings = listOf(
        TodoThingsModel(
            mainTodo = "뫄뫄뫄 하기",
            description = "앙녕앙녀앙ㅇ녀아앙ㄴ알야랑아연ㅇ라ㅓㄱ앙영앙영아영",
            isChecked = false
        )
    )
}