package com.example.todolist

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import com.example.todolist.data.ToDoViewModel
import com.example.todolist.ui.LandingScreen
import com.example.todolist.ui.ToDoTabBar
import com.example.todolist.ui.ToDoTabs
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.data.Todo
import com.example.todolist.data.TodoThingsModel
import com.example.todolist.ui.theme.*
import com.google.accompanist.insets.*
import dagger.hilt.android.AndroidEntryPoint

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
    Surface(color = Color.Gray) {
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
                topPadding = contentTopPadding
            )
        }
    }
}


@Composable
private fun Greeting(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
) {
    Card(
        backgroundColor = Color.Transparent,
        modifier = Modifier
    ) {
        Column(modifier = modifier) {
            Spacer(Modifier.padding(top = topPadding))
            home(
                modifier = modifier
            )
        }
    }


}

@Composable
fun home(
    modifier: Modifier = Modifier,
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.statusBarsPadding()
    ) {
        val scope = rememberCoroutineScope()
        CardContent(
            openDrawer = {
                scope.launch {
                    scaffoldState.drawerState.isClosed
                }
            }
        )
    }
}

enum class ToDoScreen {
    TODO, Eat, Sleep
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CardContent(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ToDoViewModel = viewModel()
) {
    val todoThings by viewModel.toDoThings.observeAsState()
    val allTasks = stringArrayResource(R.array.tasks)
    val tasks = remember { mutableStateListOf(*allTasks) }
    var tabSelected by remember { mutableStateOf(ToDoScreen.TODO) }
    BackdropScaffold(
        modifier = modifier,
        scaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed),
        frontLayerScrimColor = Color.Unspecified,
        appBar = {
            HomeTabBar(openDrawer, tabSelected, onTabSelected = { tabSelected = it })
        },
        backLayerContent = {
            when (tabSelected) {
                ToDoScreen.TODO -> {
                    LazyColumn(modifier = Modifier
                        .padding(vertical = 4.dp)
                        .systemBarsPadding()) {
                        /*items(count = tasks.size) { i ->
                            val task = tasks.getOrNull(i)
                            if (task != null) {
                                key(task) {
                                    TodoList(
                                        task = task,
                                        onRemove = { tasks.remove(task) }
                                    )
                                }
                            }

                        }*/
                        items(count = todoThings!!.size) { i ->
                            val task = todoThings?.getOrNull(i)
                            if (task != null) {
                                key(task) {
                                    TodoList(task = task, onRemove = {})
                                }
                            }
                        }

                    }
                }
                ToDoScreen.Sleep -> {

                }
                ToDoScreen.Eat -> {

                }
            }


        },
        frontLayerContent = {
            Surface(modifier = modifier.fillMaxSize(), color = Color.Gray, shape = BottomSheetShape) {
                Column(modifier = Modifier.padding(start = 24.dp, top = 20.dp, end = 24.dp)) {
                    Icon(
                        Icons.Outlined.ArrowDropUp,
                        contentDescription = "Localized description"
                    )
//                    Text(
//                        text = "위로",
//                    )
                    Spacer(Modifier.height(8.dp))
                    ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                        ExtendedFloatingActionButtonDemo()
                        EditToDo()
                    }
                }
                }




        }
    )


}

@Composable
private fun TodoList(task: TodoThingsModel, onRemove: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = { onRemove() })
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
                .background(Color.White, RoundedCornerShape(16.dp))
        ) {
            var selectAll by remember { mutableStateOf(false) }
            Checkbox(
                modifier = Modifier.padding(0.dp, 7.dp),
                checked = selectAll,
                onCheckedChange = { checked ->
                    selectAll = checked
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
                Text(
                    modifier = Modifier,
                    text = task.mainTodo,
                    style = MaterialTheme.typography.h5.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                if (expanded) {
                    Text(
                        text = task.description,
                    )
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

@Composable
fun EditToDo() {
    var titleTxt by remember { mutableStateOf("") }
    var subTxt by remember { mutableStateOf("") }
    Row(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = titleTxt,
            onValueChange = { titleTxt = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 60.dp, 0.dp),

            textStyle = MaterialTheme.typography.h6,
            placeholder = {
                Text(
                    text = "제목",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.LightGray
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = Color.LightGray,
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.LightGray,
                unfocusedIndicatorColor = Color.Transparent
            ),

            maxLines = 2

        )
    }



    Divider(
        modifier = Modifier
            .height(0.5.dp),
        color = Color.LightGray
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 100.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
    ) {
        TextField(
            value = subTxt,
            onValueChange = { subTxt = it },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            textStyle = MaterialTheme.typography.body1,
            placeholder = {
                Text(
                    text = "내용",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.LightGray
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = Color.LightGray,
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.LightGray,
                unfocusedIndicatorColor = Color.Transparent
            ),

            )

    }

}


@Composable
fun ExtendedFloatingActionButtonDemo() {
    Scaffold(
        backgroundColor = Color(0xFFFEFEFA),
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                },
                backgroundColor = lime,
                contentColor = Color.Black,
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Localized description"
                )
            }
        }
    ){}
    /*Scaffold(bottomBar = {
        ExtendedFloatingActionButton(
            modifier = Modifier
                .navigationBarsWithImePadding()
            ,
            icon = { Icon(Icons.Filled.Favorite,"") },
            text = { Text("add") },
            onClick = { *//*do something*//* },
            elevation = FloatingActionButtonDefaults.elevation(8.dp),
            backgroundColor = lime,
        )
    },  ) {

    }*/

}

@Composable
private fun HomeTabBar(
    openDrawer: () -> Unit,
    tabSelected: ToDoScreen,
    onTabSelected: (ToDoScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    ToDoTabBar(
        modifier = modifier,
        onMenuClicked = openDrawer
    ) { tabBarModifier ->
        ToDoTabs(
            modifier = tabBarModifier,
            titles = ToDoScreen.values().map { it.name },
            tabSelected = tabSelected,
            onTabSelected = { newTab -> onTabSelected(ToDoScreen.values()[newTab.ordinal]) }
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun DefaultPreview() {
    ToDoListTheme {
        MainScreen()
    }
}


enum class SplashState { Shown, Completed }
