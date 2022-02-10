package com.example.todolist

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.todolist.data.ToDoViewModel
import com.example.todolist.ui.LandingScreen
import com.example.todolist.ui.ToDoTabBar
import com.example.todolist.ui.ToDoTabs
import kotlinx.coroutines.launch
import com.example.todolist.data.TodoDB
import com.example.todolist.ui.theme.*
import com.google.accompanist.insets.*
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.data.Todo
import kotlin.math.ceil

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var viewModel: ToDoViewModel
    var activity = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListTheme {
                MyApp()
            }
        }
        /*viewModel = ViewModelProvider(this).get(ToDoViewModel::class.java)
        viewModel.getRecordsObserver().observe(this, object : Observer<List<TodoDB>> {
            override fun onChanged(t: List<TodoDB>?) {
                Log.e("tt", "${t?.get(0)?.mainTxt}")

            }

        })*/


    }
}

@Composable
private fun MyApp() {
    MainScreen()

}

@VisibleForTesting
@Composable
fun MainScreen() {
    Surface() {
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
            home()
        }
    }


}

@Composable
fun home(

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
            },
            Modifier
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
    val items by viewModel.todoData.observeAsState(listOf())//State를 선언할 때는 by를 통해 사용(delegation)
//    val tabSelected by remember { mutableStateOf(ToDoScreen.TODO) }
    BackdropScaffold(
        modifier = modifier,
        scaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed),
        frontLayerScrimColor = Color.Unspecified,
        backLayerBackgroundColor = Color.Unspecified,
        frontLayerElevation = 8.dp,
        appBar = {
//            HomeTabBar(openDrawer, tabSelected, onTabSelected = { tabSelected = it })
        },
        persistentAppBar = false,
        headerHeight = 120.dp,
        backLayerContent = {
            if (items.isEmpty()) {
                EmptyScreen()
            } else {
                Column {
                    AnimationGaugeBar(5, Color.Black, items)
                    LazyColumn(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .systemBarsPadding()
                    ) {
                        items(items, key = {task -> task.id}) { task ->
                            TodoList(task)
                        }

                    }
                }

            }
            /*when (tabSelected) {
                ToDoScreen.TODO -> {

                }
                ToDoScreen.Sleep -> {

                }
                ToDoScreen.Eat -> {

                }
            }*/


        },
        frontLayerContent = {
            ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                EditToDo()
            }
        }
    )


}

@Composable
private fun TodoList(task: TodoDB?, viewModel: ToDoViewModel = viewModel()) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    task?.let { it1 -> viewModel.deleteRecord(it1) }
                    viewModel.loadRecords()
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
                .background(Color.White, RoundedCornerShape(16.dp))
        ) {
//            var selectAll by remember { mutableStateOf(task!!.check) }
            Checkbox(
                modifier = Modifier.padding(0.dp, 7.dp),
                checked = task!!.check!!,
                onCheckedChange = { checked ->
                    val todoEntity =
                        TodoDB(mainTxt = task.mainTxt, subTxt = task.subTxt, check = checked)
                    viewModel.updateRecord(todoEntity)
                    viewModel.loadRecords()
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

@Composable
fun EditToDo(viewModel: ToDoViewModel = viewModel()) {
    var titleTxt by remember { mutableStateOf("") }
    var subTxt by remember { mutableStateOf("") }
    Column() {
        Row {
            //state Hoisting(상태 끌어올리기) compose 내부에서 상태를 저장해야할 때 많이 사용, 자식 Composable의 state를 호출부로 끌어올리는 것을 뜻함.
            //구조 분해 사용 val (textState, setTextState) = mutableStateOf("") TextField(value = textState, onValueChange = setTextState)
            //by 사용(Delegation 사용) o
            TextField(
                value = titleTxt,
                onValueChange = { textValue -> titleTxt = textValue },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(start = 10.dp),
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
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),

                maxLines = 2

            )
            Button(
                modifier = Modifier.padding(top = 10.dp),
                onClick = {
                    val todoEntity = TodoDB(mainTxt = titleTxt, subTxt = subTxt, check = false)
                    viewModel.insertRecord(todoEntity)
                    viewModel.loadRecords()
                    titleTxt = ""
                    subTxt = ""
                },
            ) {
                Text("추가", fontFamily = FontFamily.Monospace)
            }

        }
        Divider(
            modifier = Modifier
                .height(0.5.dp),
            color = Color.LightGray
        )
        Row() {

            TextField(
                value = subTxt,
                onValueChange = { textValue -> subTxt = textValue },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(start = 10.dp),
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
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),

                )
        }
    }


}

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
    if (task.size == value) {
        Text(
            text = "목표를 모두 달성했어요!",
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(start = 5.dp)
        )
    } else {
        Text(
            text = "목표 ${task.size}개 중에 ${value}개를 달성했어요!",
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(start = 5.dp)
        )
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

@Composable
fun EmptyScreen() {
    Column( verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Row() {
            Image(
                painter = painterResource(R.drawable.checklist),
                contentDescription = "Content description for visually impaired",
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f)
            )
        }
        Row() {
            Text(text = "체크리스트를 통해 목표를 달성하세요!",
                fontFamily = FontFamily.Monospace)
        }


    }



}


/*@Composable
fun ExtendedFloatingActionButtonDemo(title: String?, subTitle: String?, todoViewModel: ToDoViewModel) {
    Scaffold(
        backgroundColor = Color.Transparent,
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
    ) {}


}*/

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

/*@Preview(
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
}*/

@Preview
@Composable
fun pre(){
    EmptyScreen()
}

enum class SplashState { Shown, Completed }
