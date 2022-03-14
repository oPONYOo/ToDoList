package com.example.todolist.ui

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.todolist.data.TodoDB
import kotlin.math.ceil

@Composable
fun AnimationGaugeBar(
    value: Int, // 표시할 값
    color: Color, // 게이지바의 색
    task: List<TodoDB>?

) {
    var animationPlayed by remember { //애니메이션 트리거를 위한 boolean 값
        mutableStateOf(false)
    }
    val checkValue = ceil(((value / task!!.size.toDouble())) * 100).toInt()
    val curValue = animateIntAsState(
        targetValue = if (animationPlayed) checkValue else 0,
        animationSpec = tween(
            durationMillis = 2000,
            delayMillis = 0
        )
    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    when {
        task.size == value -> {
            Text(
                text = "목표를 모두 달성했어요!",
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(start = 5.dp)
            )
        }
        value == 0 -> {
            Text(
                text = "달성할 목표가 ${task.size}개 남았어요!",
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(start = 5.dp)
            )
        }
        else -> {
            Text(
                text = "목표 ${task.size}개 중 ${value}개를 달성했어요!",
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(start = 5.dp)
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(CircleShape)
            .background(Color.LightGray)

    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(curValue.value / 100f)
                .clip(CircleShape)
                .background(color = color)
                .padding(8.dp)
        ) {
        }
    }
}