@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)

package com.github.deweyreed.expired.ui.main

import android.animation.ArgbEvaluator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.deweyreed.expired.R
import com.github.deweyreed.expired.domain.entities.ItemEntity
import com.github.deweyreed.expired.domain.utils.cleanVoiceInput
import com.github.deweyreed.expired.domain.utils.convertChineseToLocalDate
import com.github.deweyreed.expired.domain.utils.prettify
import com.github.deweyreed.expired.ui.theme.ExpiredTheme
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import org.ocpsoft.prettytime.PrettyTime
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
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
    when (val state = viewModel.itemState.collectAsState(MainViewModel.ItemState.Loading).value) {
        MainViewModel.ItemState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        MainViewModel.ItemState.Empty -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Fab(
                    onCreateItem = viewModel::addOrUpdateItem,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
        is MainViewModel.ItemState.Data -> {
            ItemList(
                itemList = state.items,
                showRemainingTime = viewModel.showRemainingTime.collectAsState(true).value,
                changeShowRemainingTime = viewModel::changeShowRemainingTime,
                addOrUpdateItem = viewModel::addOrUpdateItem,
                consumeItem = viewModel::consumeItem,
            )
        }
    }
}

@Composable
fun ItemList(
    itemList: List<ItemEntity>,
    showRemainingTime: Boolean,
    changeShowRemainingTime: (Boolean) -> Unit,

    addOrUpdateItem: (ItemEntity) -> Unit,
    consumeItem: (ItemEntity) -> Unit,
) {
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
                        contentDescription = stringResource(R.string.analytics)
                    )
                }

                IconButton(onClick = { changeShowRemainingTime(!showRemainingTime) }) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.EventNote),
                        contentDescription = stringResource(R.string.time_format)
                    )
                }
            }
        },
        floatingActionButton = { Fab(onCreateItem = addOrUpdateItem) },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.LocalDining),
                        contentDescription = stringResource(R.string.add),
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
                        text = stringResource(
                            R.string.in_total_template,
                            itemList.sumOf { it.count }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .animateItemPlacement(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.subtitle1,
                    )
                }
            }

            if (!showRemainingTime) {
                item {
                    ListItem(
                        modifier = Modifier.animateItemPlacement(),
                        text = {
                            Text(text = stringResource(R.string.today))
                        },
                        secondaryText = {
                            Text(text = LocalDate.now().prettify(LocalContext.current))
                        }
                    )
                }
            }

            items(
                items = itemList,
                key = { it.id }
            ) {
                Item(
                    item = it,
                    onUpdateItem = addOrUpdateItem,
                    onConsumeItem = consumeItem,
                    modifier = Modifier.animateItemPlacement(),
                    showRemainingTime = showRemainingTime,
                )
            }
        }
    }
}

@Composable
fun Fab(
    onCreateItem: (ItemEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showInputDialog by remember { mutableStateOf(false) }

    FloatingActionButton(
        onClick = { showInputDialog = true },
        modifier = modifier,
    ) {
        Icon(
            painter = rememberVectorPainter(image = Icons.Rounded.ShoppingCart),
            contentDescription = stringResource(R.string.add),
        )
    }

    if (showInputDialog) {
        CreateItemDialog(
            onDismissRequest = { showInputDialog = false },
            continuousAdd = true,
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
    continuousAdd: Boolean = false,
    onDismissRequest: () -> Unit,
    onCreateItem: (ItemEntity) -> Unit,
) {
    var name by remember(oldItem) { mutableStateOf(oldItem.name) }
    val nameLauncher = rememberVoiceInputLauncher {
        name = it.cleanVoiceInput()
    }

    var time by remember(oldItem) { mutableStateOf(oldItem.expiredTime) }
    val timeLauncher = rememberVoiceInputLauncher {
        time = convertChineseToLocalDate(it.cleanVoiceInput()) ?: LocalDate.now().plusDays(1)
    }

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

                    if (continuousAdd) {
                        name = ""
                        count = 1
                        time = LocalDate.now().plusDays(1)
                    } else {
                        onDismissRequest()
                    }
                },
                enabled = name.isNotBlank() && count > 0 && time.isAfter(LocalDate.now())
            ) {
                Text(text = stringResource(R.string.add))
            }
        },
        title = {
            Text(text = stringResource(R.string.add))
        },
        text = {
            Column {
                Spacer(modifier = Modifier.height(16.dp))

                val focusRequester = remember { FocusRequester() }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.focusRequester(focusRequester),
                    label = { Text(text = stringResource(R.string.name)) },
                    trailingIcon = {
                        val context = LocalContext.current
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Rounded.Mic),
                            contentDescription = stringResource(R.string.voice_input),
                            modifier = Modifier.clickable {
                                requestVoiceInput(context, nameLauncher)
                            }
                        )
                    }
                )
                LaunchedEffect(Unit) {
                    delay(100)
                    focusRequester.requestFocus()
                }

                Spacer(modifier = Modifier.height(8.dp))

                val dialogState = rememberMaterialDialogState()

                OutlinedTextField(
                    value = time.prettify(LocalContext.current),
                    onValueChange = { },
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        dialogState.show()
                    },
                    enabled = false,
                    label = { Text(text = stringResource(R.string.expired_time)) },
                    trailingIcon = {
                        val context = LocalContext.current
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Rounded.Mic),
                            contentDescription = stringResource(R.string.voice_input),
                            modifier = Modifier.clickable {
                                requestVoiceInput(context, timeLauncher)
                            }
                        )
                    }
                )

                MaterialDialog(
                    dialogState = dialogState,
                    buttons = {
                        positiveButton(stringResource(R.string.ok))
                        negativeButton(stringResource(R.string.cancel))
                    }
                ) {
                    datepicker(
                        initialDate = time,
                        yearRange = time.year..(time.year + 30),
                        allowedDateValidator = { !it.isBefore(LocalDate.now()) },
                    ) { date ->
                        time = date
                    }
                }

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
                            contentDescription = stringResource(R.string.remove)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = count.toString())
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { count += 1 }) {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Rounded.Add),
                            contentDescription = if (oldItem.id == ItemEntity.ID_NEW) {
                                stringResource(R.string.add)
                            } else {
                                stringResource(R.string.update)
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
    val unknown = stringResource(R.string.unknown)
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { activityResult ->
            onResult(
                if (activityResult.resultCode == Activity.RESULT_OK) {
                    activityResult.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        ?.joinToString() ?: unknown
                } else {
                    unknown
                }
            )
        }
    )
}

private fun requestVoiceInput(
    context: Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    intent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
    )
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    try {
        launcher.launch(Intent.createChooser(intent, null))
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, e.message.toString(), Toast.LENGTH_LONG).show()
    }
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

    val plainColor = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
    val seriousColor = MaterialTheme.colors.error
    val textColor = remember(item.expiredTime) {
        val expire = item.expiredTime
        val today = LocalDate.now()
        val target = today.plusMonths(1)

        val argbEvaluator = ArgbEvaluator()

        when {
            expire.isBefore(today) -> seriousColor
            expire == target || expire.isAfter(target) -> plainColor
            else -> {
                Color(
                    argbEvaluator.evaluate(
                        ((ChronoUnit.DAYS.between(today, expire).toFloat()) /
                            (ChronoUnit.DAYS.between(today, target).toFloat()))
                            .coerceIn(0f, 1f),
                        seriousColor.toArgb(),
                        plainColor.toArgb()
                    ) as Int
                )
            }
        }
    }

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
                },
                color = textColor,
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
                color = textColor,
            )
        },
        trailing = {
            IconButton(onClick = { onConsumeItem(item) }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.LocalDining),
                    contentDescription = stringResource(R.string.consume),
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
