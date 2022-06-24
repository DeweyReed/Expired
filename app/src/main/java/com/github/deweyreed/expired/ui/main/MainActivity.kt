@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)

package com.github.deweyreed.expired.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.EventNote
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.deweyreed.expired.domain.entities.ItemEntity
import com.github.deweyreed.expired.domain.utils.convertChineseToLocalDate
import com.github.deweyreed.expired.domain.utils.prettify
import com.github.deweyreed.expired.ui.theme.ExpiredTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import org.ocpsoft.prettytime.PrettyTime
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpiredTheme {
                Main()
            }
        }
    }
}

@Composable
fun Main(viewModel: MainViewModel = viewModel()) {
    val itemList by viewModel.items.collectAsState(emptyList())

    val showRemainingTime by viewModel.showRemainingTime.collectAsState(initial = true)
    var showAnalytics by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                cutoutShape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50))
            ) {
                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { showAnalytics = !showAnalytics }) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.Analytics),
                        contentDescription = "Toggle analytics"
                    )
                }

                IconButton(onClick = { viewModel.changeShowRemainingTime(!showRemainingTime) }) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.EventNote),
                        contentDescription = "Toggle the time format"
                    )
                }
            }
        },
        floatingActionButton = { Fab(onCreateItem = viewModel::addOrUpdateItem) },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.LocalDining),
                        contentDescription = "Add",
                        modifier = Modifier
                            .size(108.dp)
                            .padding(16.dp)
                            .align(Alignment.Center)
                            .animateItemPlacement(),
                        tint = MaterialTheme.colors.primary,
                    )
                }
            }

            if (showAnalytics) {
                item {
                    Text(
                        text = "${itemList.size} in total",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .animateItemPlacement(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.subtitle1,
                    )
                }
            }

            items(
                items = itemList,
                key = { it.id }
            ) {
                Item(
                    item = it,
                    onUpdateItem = viewModel::addOrUpdateItem,
                    onConsumeItem = viewModel::consumeItem,
                    modifier = Modifier.animateItemPlacement(),
                    showRemainingTime = showRemainingTime,
                )
            }
        }
    }
}

@Composable
fun Fab(onCreateItem: (ItemEntity) -> Unit) {
    var showInputDialog by remember { mutableStateOf(false) }

    FloatingActionButton(
        onClick = { showInputDialog = true },
    ) {
        Icon(
            painter = rememberVectorPainter(image = Icons.Rounded.ShoppingCart),
            contentDescription = "Add",
        )
    }

    if (showInputDialog) {
        CreateItemDialog(
            onDismissRequest = { showInputDialog = false },
            onCreateItem = onCreateItem,
        )
    }
}

@Composable
fun CreateItemDialog(
    oldItem: ItemEntity = ItemEntity(
        name = "",
        expiredTime = LocalDate.now().plusDays(1)
    ),
    onDismissRequest: () -> Unit,
    onCreateItem: (ItemEntity) -> Unit,
) {
    var name by remember(oldItem) { mutableStateOf(oldItem.name) }
    val nameLauncher = rememberVoiceInputLauncher { name = it }

    var time by remember(oldItem) { mutableStateOf(oldItem.expiredTime) }
    val timeLauncher = rememberVoiceInputLauncher { time = convertChineseToLocalDate(it) }

    var count by remember(oldItem) { mutableStateOf(oldItem.count) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onCreateItem(
                        oldItem.copy(
                            name = name,
                            count = count,
                            expiredTime = time
                        )
                    )
                    onDismissRequest()
                },
                enabled = name.isNotBlank() && count > 0 && time.isAfter(LocalDate.now())
            ) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(text = "Add an item")
        },
        text = {
            Column {
                Spacer(modifier = Modifier.height(16.dp))

                val focusRequester = remember { FocusRequester() }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.focusRequester(focusRequester),
                    label = { Text(text = "Name") },
                    trailingIcon = {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Rounded.Mic),
                            contentDescription = "Voice input",
                            modifier = Modifier.clickable { nameLauncher.requestVoiceInput() }
                        )
                    }
                )
                LaunchedEffect(Unit) {
                    delay(100)
                    focusRequester.requestFocus()
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = time.prettify(LocalContext.current),
                    onValueChange = { },
                    label = { Text(text = "Expired Time") },
                    trailingIcon = {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Rounded.Mic),
                            contentDescription = "Voice input",
                            modifier = Modifier.clickable { timeLauncher.requestVoiceInput() }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { count -= 1 },
                        enabled = count > 1,
                    ) {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Rounded.Remove),
                            contentDescription = "Remove"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = count.toString())
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { count += 1 }) {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Rounded.Add),
                            contentDescription = if (oldItem.id == ItemEntity.ID_NEW) {
                                "Add"
                            } else {
                                "Update"
                            }
                        )
                    }
                }
            }
        },
        properties = DialogProperties(
            dismissOnClickOutside = false,
        ),
    )
}

@Composable
private fun rememberVoiceInputLauncher(
    onResult: (String) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { activityResult ->
            onResult(
                if (activityResult.resultCode == Activity.RESULT_OK) {
                    activityResult.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        ?.joinToString() ?: "Unknown"
                } else {
                    "Unknown"
                }
            )
        }
    )
}

private fun ManagedActivityResultLauncher<Intent, ActivityResult>.requestVoiceInput() {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    intent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
    )
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something")
    launch(intent)
}

@Composable
fun Item(
    item: ItemEntity,
    onUpdateItem: (ItemEntity) -> Unit,
    onConsumeItem: (ItemEntity) -> Unit,
    modifier: Modifier = Modifier,
    showRemainingTime: Boolean = true,
) {
    val prettyTime = remember { PrettyTime() }
    var showInputDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    ListItem(
        modifier = modifier.clickable {
            showInputDialog = true
        },
        text = {
            Text(
                text = buildString {
                    append(item.name)
                    if (item.count > 1) {
                        append(" x ${item.count}")
                    }
                }
            )
        },
        secondaryText = {
            Text(
                text = if (showRemainingTime) {
                    prettyTime.format(
                        Date(
                            item.expiredTime.atStartOfDay().atZone(ZoneId.systemDefault())
                                .toInstant().toEpochMilli()
                        )
                    )
                } else {
                    item.expiredTime.prettify(context)
                },
                color = if (item.expiredTime.isBefore(LocalDate.now())) {
                    MaterialTheme.colors.error
                } else {
                    Color.Unspecified
                }
            )
        },
        trailing = {
            IconButton(onClick = { onConsumeItem(item) }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.LocalDining),
                    contentDescription = "Consume",
                    tint = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        }
    )

    if (showInputDialog) {
        CreateItemDialog(
            oldItem = item,
            onDismissRequest = { showInputDialog = false },
            onCreateItem = onUpdateItem,
        )
    }
}
