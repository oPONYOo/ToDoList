package com.example.todolist

import android.app.Activity
import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.todolist.ui.ToDoTabBar
import com.example.todolist.ui.ToDoTabs
import kotlinx.coroutines.launch
import com.example.todolist.data.TodoDB
import com.example.todolist.ui.theme.*
import com.google.accompanist.insets.*
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.viewmodel.compose.viewModel

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
    val items = viewModel.todoData.observeAsState(listOf()).value
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
            if (items.isEmpty()){


            }else{
                LazyColumn(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .systemBarsPadding()
                ) {
                    items(count = items!!.size) { i ->
                        val task = items.getOrNull(i)
                        if (task != null) {
                            key(task.id) {
                                TodoList(task = task)
                            }
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

            /*Surface(
                modifier = modifier.fillMaxSize(),
                color = Color.Gray,
                shape = BottomSheetShape
            ) {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    Row(modifier = Modifier.align(Alignment.CenterHorizontally),
                    ){
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Localized description"
                        )
                        Spacer(modifier = Modifier.padding(start = 4.dp))
                        Text(
                            text = "작성하기"
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(top = 50.dp))

            }*/


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
                task?.mainTxt?.let {
                    Text(
                        modifier = Modifier,
                        text = it,
                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                if (expanded) {
                    task?.subTxt?.let {
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
                    .width(720.dp)
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
                    val todoEntity = TodoDB(mainTxt = titleTxt, subTxt = subTxt)
                    viewModel.insertRecord(todoEntity)
                    viewModel.loadRecords()
                    titleTxt = ""
                    subTxt = ""
                },
            ) {
                Text("추가")
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

//@Preview(
//    showBackground = true,
//    widthDp = 320,
//    uiMode = UI_MODE_NIGHT_YES,
//    name = "DefaultPreviewDark"
//)
//@Preview(showBackground = true, widthDp = 320)
//@Composable
//fun DefaultPreview() {
//    ToDoListTheme {
//        MainScreen()
//    }
//}


enum class SplashState { Shown, Completed }
