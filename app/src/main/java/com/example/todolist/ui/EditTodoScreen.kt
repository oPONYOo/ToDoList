package com.example.todolist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.data.ToDoViewModel
import com.example.todolist.data.TodoDB

@Composable
fun EditToDo(viewModel: ToDoViewModel = viewModel()) {
    var titleTxt by remember { mutableStateOf("") }
    var subTxt by remember { mutableStateOf("") }
    Column {
        Row {
            //state Hoisting(상태 끌어올리기) compose 내부에서 상태를 저장해야할 때 많이 사용, 자식 Composable의 state를 호출부로 끌어올리는 것을 뜻함.
            //구조 분해 사용 val (textState, setTextState) = mutableStateOf("") TextField(value = textState, onValueChange = setTextState)
            //by 사용(Delegation 사용) o
            TextField(
                value = titleTxt,
                onValueChange = { textValue -> titleTxt = textValue },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
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
                    backgroundColor = Color.Transparent,
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
        Row {

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
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),

                )
        }
    }
}