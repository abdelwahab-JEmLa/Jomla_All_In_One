package z_GeminiAi
//
//import android.graphics.BitmapFactory
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.requiredSize
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Button
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.clientjetpack.R
//
//val images = arrayOf(
//    // Image generated using Gemini from the prompt "cupcake image"
//    R.drawable.baked_goods_1,
//    // Image generated using Gemini from the prompt "cookies images"
//    R.drawable.baked_goods_2,
//    // Image generated using Gemini from the prompt "cake images"
//    R.drawable.baked_goods_3,
//)
//val imageDescriptions = arrayOf(
//    R.string.image1_description,
//    R.string.image2_description,
//    R.string.image3_description,
//)
//
//@Composable
//fun BakingScreen(
//    bakingViewModel: BakingViewModel = viewModel()
//) {
//    val selectedImages = remember { mutableStateListOf<Int>() }
//    val placeholderPrompt = stringResource(R.string.prompt_placeholder)
//    var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
//    val uiState by bakingViewModel.uiState.collectAsState()
//    val context = LocalContext.current
//
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Text(
//            text = stringResource(R.string.baking_title),
//            style = MaterialTheme.typography.titleLarge,
//            modifier = Modifier.padding(16.dp)
//        )
//
//        LazyRow(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            itemsIndexed(images) { index, image ->
//                var imageModifier = Modifier
//                    .padding(start = 8.dp, end = 8.dp)
//                    .requiredSize(200.dp)
//                    .clickable {
//                        if (selectedImages.contains(index)) {
//                            selectedImages.remove(index)
//                        } else if (selectedImages.size < 2) {
//                            selectedImages.add(index)
//                        }
//                    }
//                if (selectedImages.contains(index)) {
//                    imageModifier = imageModifier.border(BorderStroke(4.dp, MaterialTheme.colorScheme.primary))
//                }
//                Image(
//                    painter = painterResource(image),
//                    contentDescription = stringResource(imageDescriptions[index]),
//                    modifier = imageModifier
//                )
//            }
//        }
//
//        Row(
//            modifier = Modifier.padding(all = 16.dp)
//        ) {
//            TextField(
//                value = prompt,
//                label = { Text(stringResource(R.string.label_prompt)) },
//                onValueChange = { prompt = it },
//                modifier = Modifier
//                    .weight(0.8f)
//                    .padding(end = 16.dp)
//                    .align(Alignment.CenterVertically)
//            )
//
//            Button(
//                onClick = {
//                    val bitmaps = selectedImages.map { index ->
//                        BitmapFactory.decodeResource(
//                            context.resources,
//                            images[index]
//                        )
//                    }
//                    bakingViewModel.sendPrompt(bitmaps, prompt)
//                },
//                enabled = prompt.isNotEmpty() && selectedImages.size == 2,
//                modifier = Modifier
//                    .align(Alignment.CenterVertically)
//            ) {
//                Text(text = stringResource(R.string.action_go))
//            }
//        }
//
//        when (val state = uiState) {
//            is UiState.Loading -> {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
//            }
//            is UiState.Error -> {
//                Text(
//                    text = state.errorMessage,
//                    color = MaterialTheme.colorScheme.error,
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxSize()
//                        .verticalScroll(rememberScrollState())
//                )
//            }
//            is UiState.Success -> {
//                Text(
//                    text = state.outputText,
//                    textAlign = TextAlign.Start,
//                    color = MaterialTheme.colorScheme.onSurface,
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxSize()
//                        .verticalScroll(rememberScrollState())
//                )
//            }
//            else -> {
//                // Initial state, do nothing
//            }
//        }
//    }
//}
