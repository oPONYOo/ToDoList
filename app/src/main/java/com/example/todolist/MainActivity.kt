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
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.todolist.data.ToDoViewModel
import com.example.todolist.ui.LandingScreen
import com.example.todolist.ui.ToDoTabBar
import com.example.todolist.ui.ToDoTabs
import com.example.todolist.ui.theme.ToDoListTheme
import com.example.todolist.ui.theme.gray1
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.ui.theme.lime


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
                    scaffoldState.drawerState.open()
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
//    viewModel: ToDoViewModel= viewModel()
) {

    var tabSelected by remember { mutableStateOf(ToDoScreen.TODO) }
    BackdropScaffold(
        modifier = modifier,
        scaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed),
        frontLayerScrimColor = Color.Unspecified,
        appBar = {
            HomeTabBar(openDrawer, tabSelected, onTabSelected = { tabSelected = it })
        },
        backLayerContent = {
            EditToDo()
        },
        frontLayerContent = {
            Image(painterResource(id = R.drawable.ic_baseline_horizontal_rule_24), contentDescription = null, modifier = Modifier.fillMaxWidth())
            when (tabSelected) {
                ToDoScreen.TODO -> {
                    val names: List<String> = List(10) { "compose" }
                    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
                        items(items = names) { name ->
                            TodoList(name)
                        }
                    }
                }
                ToDoScreen.Sleep -> {

                }
                ToDoScreen.Eat -> {

                }
            }
        }
    )


}

@Composable
private fun TodoList(name: String) {
    var expanded by remember { mutableStateOf(false) }

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
            }
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Text(
                modifier = Modifier,
                text = name,
                style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            if (expanded) {
                Text(
                    text = ("Composem ipsum color sit lazy, " +
                            "padding theme elit, sed do bouncy. ").repeat(4),
                )
            }
        }
        IconButton(onClick = { expanded = !expanded }, modifier = Modifier.padding(0.dp, 5.dp)) {
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

@Composable
fun EditToDo() {
    var text by remember { mutableStateOf("Hello") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 20.dp, 10.dp, 0.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
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
            .padding(10.dp, 0.dp, 10.dp, 0.dp)
            .height(0.5.dp),
        color = Color.LightGray
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 0.dp, 10.dp, 10.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
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

private fun Modifier.swipeToDismiss(
    onDismissed: () -> Unit
): Modifier = composed {
    // This `Animatable` stores the horizontal offset for the element.
    val offsetX = remember { Animatable(0f) }
    pointerInput(Unit) {
        // Used to calculate a settling position of a fling animation.
        val decay = splineBasedDecay<Float>(this)
        // Wrap in a coroutine scope to use suspend functions for touch events and animation.
        coroutineScope {
            while (true) {
                // Wait for a touch down event.
                val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                // Interrupt any ongoing animation.
                offsetX.stop()
                // Prepare for drag events and record velocity of a fling.
                val velocityTracker = VelocityTracker()
                // Wait for drag events.
                awaitPointerEventScope {
                    horizontalDrag(pointerId) { change ->
                        // Record the position after offset
                        val horizontalDragOffset = offsetX.value + change.positionChange().x
                        launch {
                            // Overwrite the `Animatable` value while the element is dragged.
                            offsetX.snapTo(horizontalDragOffset)
                        }
                        // Record the velocity of the drag.
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        // Consume the gesture event, not passed to external
                        change.consumePositionChange()
                    }
                }
                // Dragging finished. Calculate the velocity of the fling.
                val velocity = velocityTracker.calculateVelocity().x
                // Calculate where the element eventually settles after the fling animation.
                val targetOffsetX = decay.calculateTargetValue(offsetX.value, velocity)
                // The animation should end as soon as it reaches these bounds.
                offsetX.updateBounds(
                    lowerBound = -size.width.toFloat(),
                    upperBound = size.width.toFloat()
                )
                launch {
                    if (targetOffsetX.absoluteValue <= size.width) {
                        // Not enough velocity; Slide back to the default position.
                        offsetX.animateTo(targetValue = 0f, initialVelocity = velocity)
                    } else {
                        // Enough velocity to slide away the element to the edge.
                        offsetX.animateDecay(velocity, decay)
                        // The element was swiped away.
                        onDismissed()
                    }
                }
            }
        }
    }
        // Apply the horizontal offset to the element.
        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
}

enum class SplashState { Shown, Completed }
