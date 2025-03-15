package P6_AiGroupeForSupplier

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clientjetpack.R

val images = arrayOf(
    // Image generated using Gemini from the prompt "cupcake image"
    R.drawable.confiseries,
    // Image generated using Gemini from the prompt "cookies images"
    R.drawable.cosmitiques,
    // Image generated using Gemini from the prompt "cake images"
    R.drawable.atay_moukassarat,
)
val imageDescriptions = arrayOf(
    R.string.image1_description,
    R.string.image2_description,
    R.string.image3_description,
)

@Composable
fun GenerativeAiScreen(
    generativeAiViewModel: GenerativeAiViewModel = viewModel()
) {
    val selectedImages = remember { mutableStateListOf<Int>() }
    val placeholderPrompt = stringResource(R.string.prompt_placeholder)
    var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
    val uiState by generativeAiViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { generativeAiViewModel.transactionPrompt() },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Process Transaction"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Title
            Text(
                text = stringResource(R.string.baking_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            // Image Selection Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                itemsIndexed(images) { index, image ->
                    var imageModifier = Modifier
                        .padding(horizontal = 8.dp)
                        .requiredSize(200.dp)
                        .clickable {
                            if (selectedImages.contains(index)) {
                                selectedImages.remove(index)
                            } else if (selectedImages.size < 2) {
                                selectedImages.add(index)
                            }
                        }

                    if (selectedImages.contains(index)) {
                        imageModifier = imageModifier.border(
                            BorderStroke(4.dp, MaterialTheme.colorScheme.primary)
                        )
                    }

                    Image(
                        painter = painterResource(image),
                        contentDescription = stringResource(imageDescriptions[index]),
                        modifier = imageModifier
                    )
                }
            }

            // Prompt Input and Submit Button Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    label = { Text(stringResource(R.string.label_prompt)) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                )

                Button(
                    onClick = {
                        val bitmaps = selectedImages.map { index ->
                            BitmapFactory.decodeResource(
                                context.resources,
                                images[index]
                            )
                        }
                        generativeAiViewModel.sendPrompt(bitmaps, prompt)
                    },
                    enabled = prompt.isNotEmpty() && selectedImages.size == 2
                ) {
                    Text(stringResource(R.string.action_go))
                }
            }

            // State Handling
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                when (val state = uiState) {
                    is UiStateInterface.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is UiStateInterface.Error -> {
                        Text(
                            text = state.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            textAlign = TextAlign.Center
                        )
                    }
                    is UiStateInterface.Success -> {
                        Text(
                            text = state.outputText,
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    UiStateInterface.Initial -> {
                        Text(
                            text = ("R.string.initial_message"),
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiStateInterface.Error -> {
                snackbarHostState.showSnackbar(
                    message = (uiState as UiStateInterface.Error).errorMessage,
                    duration = SnackbarDuration.Long
                )
            }
            is UiStateInterface.Success -> {
                selectedImages.clear()
                prompt = placeholderPrompt
            }
            else -> { /* no-op */ }
        }
    }
}
