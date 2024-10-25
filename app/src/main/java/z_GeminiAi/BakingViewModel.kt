package z_GeminiAi
//
//import android.graphics.Bitmap
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.clientjetpack.BuildConfig
//import com.google.ai.client.generativeai.GenerativeModel
//import com.google.ai.client.generativeai.type.content
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class BakingViewModel : ViewModel() {
//    private val _uiState: MutableStateFlow<UiState> =
//        MutableStateFlow(UiState.Initial)
//    val uiState: StateFlow<UiState> =
//        _uiState.asStateFlow()
//
//    private val generativeModel = GenerativeModel(
//        modelName = "gemini-1.5-pro",
//        apiKey = BuildConfig.apiKey
//    )
//
//    fun sendPrompt(
//        bitmaps: List<Bitmap>,
//        prompt: String
//    ) {
//        _uiState.value = UiState.Loading
//
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val response = generativeModel.generateContent(
//                    content {
//                        bitmaps.forEach { image(it) }
//                        text(prompt)
//                    }
//                )
//                response.text?.let { outputContent ->
//                    _uiState.value = UiState.Success(outputContent)
//                }
//            } catch (e: Exception) {
//                _uiState.value = UiState.Error(e.localizedMessage ?: "")
//            }
//        }
//    }
//}
