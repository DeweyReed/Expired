package com.github.deweyreed.expired

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.deweyreed.expired.ui.theme.ExpiredTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpiredTheme {
                Greeting()
            }
        }
    }
}

@Composable
fun Greeting() {
    var text by remember { mutableStateOf("Click Me") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { activityResult ->
            text = if (activityResult.resultCode == Activity.RESULT_OK) {
                activityResult.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    ?.joinToString() ?: "EMPTY"
            } else {
                "EMPTY"
            }
        }
    )

    Button(
        onClick = {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something")
            launcher.launch(intent)
        },
    ) {
        Text(text = text)
    }
}
