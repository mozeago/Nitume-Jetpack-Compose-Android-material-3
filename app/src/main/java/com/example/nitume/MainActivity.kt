@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.nitume

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nitume.data.model.APIInterface
import com.example.nitume.data.model.NitumeModel
import com.example.nitume.ui.theme.AppTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://jsonplaceholder.typicode.com/"

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme() {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.app_name)) },
                            actions = {
                                TextField(
                                    value = "",
                                    onValueChange = { },
                                    label = { Text("Search") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    leadingIcon = {
                                        Icon(Icons.Filled.Search, contentDescription = "Search")
                                    },
                                )
                            }
                        )
                    }, bottomBar = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Filled.Home, contentDescription = ""
                                )
                                Text(text = "Home")
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Filled.List, contentDescription = ""
                                )
                                Text(text = "My Tasks")
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Filled.Notifications, contentDescription = ""
                                )
                                Text(text = "Alerts")
                            }
                        }
                    }, content = {
                        NitumeTask()
                    }, floatingActionButton = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            FloatingActionButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    Icons.Filled.AddCircle, contentDescription = ""
                                )
                                Text(text = "Add Task")
                            }
                        }
                    })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ComposablePreview() {
    AppTheme {
        NitumeTask()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NitumeTask() {
    val padding = 16.dp
    val currentContext = LocalContext.current
    val nitumeTaskList = remember { mutableStateListOf<NitumeModel>() }
    fetchNitumetasksJSON(context = currentContext, nitumeTaskList = nitumeTaskList)
    LazyColumn {
        items(nitumeTaskList) { nitumeTask ->
            Card(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.task_placeholder),
                        contentDescription = stringResource(
                            id = R.string.image_placeholder_text
                        ),
                        modifier = Modifier.padding(5.dp, 10.dp, 20.dp, 5.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier.padding(padding)
                    ) {
                        Text(
                            text = nitumeTask.title, style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = nitumeTask.body,
                            style = MaterialTheme.typography.bodySmall,

                            )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            BadgedBox(badge = { Badge { Text(text = "5") } }) {
                                Icon(
                                    Icons.Filled.FavoriteBorder, contentDescription = ""
                                )
                            }
                            BadgedBox(badge = { Badge { Text(text = "200") } }) {
                                Icon(
                                    Icons.Filled.Share, contentDescription = ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun fetchNitumetasksJSON(context: Context, nitumeTaskList: MutableList<NitumeModel>) {
    val retrofitbuilder =
        Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL)
            .build().create(APIInterface::class.java)
    val responseData = retrofitbuilder.getNitumeTasks()
    responseData.enqueue(object : Callback<List<NitumeModel>?> {
        /**
         * Invoked for a received HTTP response.
         *
         *
         * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
         * Call [Response.isSuccessful] to determine if the response indicates success.
         */
        override fun onResponse(
            call: Call<List<NitumeModel>?>, response: Response<List<NitumeModel>?>
        ) {
            val responseBody = response.body()!!
            nitumeTaskList.addAll(responseBody)
        }

        override fun onFailure(call: Call<List<NitumeModel>?>, t: Throwable) {
            Log.d("Fetch ", "Failed to fetch")
        }
    })
}