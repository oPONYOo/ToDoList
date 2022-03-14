package com.example.todolist

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.todolist.data.ToDoViewModel
import com.example.todolist.ui.LandingScreen
import com.example.todolist.data.TodoDB
import com.example.todolist.ui.theme.*
import com.google.accompanist.insets.*
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.ui.AnimationGaugeBar
import com.example.todolist.ui.EditToDo
import com.example.todolist.ui.EmptyScreen
import com.example.todolist.util.collectAsStateLifecycleAware


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListTheme {
                MyApp()
            }
        }
    }
}

@Composable
private fun MyApp() {
    MainScreen()

}

@VisibleForTesting
@Composable
fun MainScreen() {
    Surface {
        val transitionState = remember { MutableTransitionState(SplashState.Shown) }
        val transition = updateTransition(transitionState, label = "splashTransition")
        val splashAlpha by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 100) }, label = "splashAlpha"
        ) {
            if (it == SplashState.Shown) 1f else 0f
        }
        val contentAlpha by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 300) }, label = "contentAlpha"
        ) {
            if (it == SplashState.Shown) 0f else 1f
        }
        val contentTopPadding by transition.animateDp(
            transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
            label = "contentTopPadding"
        ) {
            if (it == SplashState.Shown) 100.dp else 0.dp
        }

        Box {
            LandingScreen(
                modifier = Modifier.alpha(splashAlpha),
                onTimeout = { transitionState.targetState = SplashState.Completed }
            )
            Greeting(
                modifier = Modifier.alpha(contentAlpha),
                topPadding = contentTopPadding,
            )
        }
    }
}


@Composable
private fun Greeting(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp
) {
    Card(
        backgroundColor = Color.Transparent,
        modifier = Modifier
    ) {
        Column(modifier = modifier) {
            Spacer(Modifier.padding(top = topPadding))
            Home()
        }
    }


}

@Composable
fun Home(
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.statusBarsPadding()
    ) {
        CardContent(
            Modifier
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CardContent(
    modifier: Modifier = Modifier,
    viewModel: ToDoViewModel = viewModel()
) {
    var checkNum = 0

//    val items by viewModel.todoData.observeAsState(listOf())
//    val items by viewModel.todoData.collectAsState(listOf())//liveData -> Flow(플랫폼 의존성 다운)
    //State를 선언할 때는 by를 통해 사용(delegation)

    val todoItems: List<TodoDB> by viewModel.todoData.collectAsStateLifecycleAware(initial = emptyList())

    for (i in todoItems.indices) {
        if (todoItems[i].check == true) {
            checkNum++
        }
    }
    BackdropScaffold(
        modifier = modifier,
        scaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed),
        frontLayerScrimColor = Color.Unspecified,
        backLayerBackgroundColor = Color.Unspecified,
        frontLayerElevation = 8.dp,
        appBar = {},
        persistentAppBar = false,
        headerHeight = 120.dp,
        backLayerContent = {
            if (todoItems.isEmpty()) {
                EmptyScreen()
            } else {
                Column {
                    AnimationGaugeBar(checkNum, Color.DarkGray, todoItems)
                    LazyColumn(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .systemBarsPadding()
                    ) {
                        items(todoItems, key = { task -> task.id }) { task ->
                            TodoList(task)
                        }

                    }
                }

            }
        },
        frontLayerContent = {
            ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                EditToDo()
            }
        }
    )


}

@Composable
private fun TodoList(task: TodoDB, viewModel: ToDoViewModel = viewModel()) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    task.let { it1 -> viewModel.deleteRecord(it1) }
                })
            }
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                .border(BorderStroke(2.dp, gray1), RoundedCornerShape(16.dp))
                .background(Color.Transparent, RoundedCornerShape(16.dp))
        ) {
            Checkbox(
                modifier = Modifier.padding(0.dp, 7.dp),
                checked = task.check!!,
                onCheckedChange = { checked ->
                    Log.e("checked", "$checked")
                    val todoEntity =
                        TodoDB(
                            id = task.id,
                            mainTxt = task.mainTxt,
                            subTxt = task.subTxt,
                            check = checked
                        )
                    viewModel.updateRecord(todoEntity)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.LightGray,
                    checkmarkColor = Color.Gray,
                    uncheckedColor = gray1
                )
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)

            ) {
                task.mainTxt?.let {
                    Text(
                        modifier = Modifier,
                        text = it,
                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                if (expanded) {
                    task.subTxt?.let {
                        Text(
                            text = it,
                        )
                    }
                }
            }
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.padding(0.dp, 5.dp)
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) {
                        stringResource(R.string.show_less)
                    } else {
                        stringResource(R.string.show_more)
                    }

                )
            }
        }
    }

}


enum class ToDoScreen {
    TODO, Eat, Sleep
}

enum class SplashState { Shown, Completed }
