package z_GeminiAi

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.StringReader

@Entity
data class GroupeurBonCommendToSupplierTabele(
    @PrimaryKey(autoGenerate = true) var vid: Long = 0,
    val idArticle: Int,
    val nameArticle: String,
    val color1SoldQuantity: Int,
    val color2SoldQuantity: Int,
    val color3SoldQuantity: Int,
    val color4SoldQuantity: Int,
    val clientSoldToItId: String
)

class GenerativeAiViewModel : ViewModel() {
    private val TAG = "GenerativeAiViewModel"
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState = _uiState.asStateFlow()
    private val gson = Gson()

    private val generativeAiViewModel = GenerativeModel(
        "gemini-1.5-pro-002",
        apiKey="AIzaSyAojWu_v6fYUYhJB_tNEJ0GmKuinT-aXdo",
        generationConfig = generationConfig {
            temperature = 1f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 8192
            responseMimeType = "text/plain"
        },
        systemInstruction = content {
            text("En tant que modèle linguistique, je fonctionne comme un assistant IA qui traite et génère du texte...")
        }
    ).also { Log.d(TAG, "GenerativeModel initialized with config: temperature=1f, topK=40, topP=0.95f") }

    val chatHistory = listOf(
        content("user") {
            text("```json\n{\n  \"SoldArticlesTabelle\": [\n    {\n\"clientSoldToItId\": 2,\n\"color1IdPicked\": 0,\n\"color1SoldQuantity\": 15,\n\"color2IdPicked\": 0,\n\"color2SoldQuantity\": 0,\n\"color3IdPicked\": 0,\n\"color3SoldQuantity\": 0,\n\"color4IdPicked\": 0,\n\"color4SoldQuantity\": 0,\n\"confimed\": false,\n\"date\": \"1730656574914\",\n\"idArticle\": 65,\n\"nameArticle\": \"Silca®\",\n\"vid\": 4\n},\n{\n\"clientSoldToItId\": 5,\n\"color1IdPicked\": 0,\n\"color1SoldQuantity\": 0,\n\"color2IdPicked\": 0,\n\"color2SoldQuantity\": 0,\n\"color3IdPicked\": 0,\n\"color3SoldQuantity\": 0,\n\"color4IdPicked\": 0,\n\"color4SoldQuantity\": 10,\n\"confimed\": false,\n\"date\": \"1730658367174\",\n\"idArticle\": 65,\n\"nameArticle\": \"Silca®\",\n\"vid\": 5\n},{\n\"clientSoldToItId\": 2,\n\"color1IdPicked\": 0,\n\"color1SoldQuantity\": 0,\n\"color2IdPicked\": 0,\n\"color2SoldQuantity\": 0,\n\"color3IdPicked\": 0,\n\"color3SoldQuantity\": 7,\n\"color4IdPicked\": 0,\n\"color4SoldQuantity\": 0,\n\"confimed\": false,\n\"date\": \"1730658367174\",\n\"idArticle\": 100,\n\"nameArticle\": \"far®\",\n\"vid\": 6\n},{\n\"clientSoldToItId\": 7,\n\"color1IdPicked\": 0,\n\"color1SoldQuantity\": 10,\n\"color2IdPicked\": 0,\n\"color2SoldQuantity\": 0,\n\"color3IdPicked\": 0,\n\"color3SoldQuantity\": 0,\n\"color4IdPicked\": 0,\n\"color4SoldQuantity\": 10,\n\"confimed\": false,\n\"date\": \"1730658367174\",\n\"idArticle\": 65,\n\"nameArticle\": \"Silca®\",\n\"vid\": 7\n}\n]\n}\n```")
            text("GroupeurBonCommendToSupplier")
        },
        content("model") {
            text("```json\n{\n  \"GroupeurBonCommendToSupplier\": [\n    {\n        \"idArticle\": 65,\n        \"nameArticle\": \"Silca®\",\n        \"color1SoldQuantity\": 15,\n        \"color2SoldQuantity\": 0,\n        \"color3SoldQuantity\": 0,\n        \"color4SoldQuantity\": 10,\n        \"clientSoldToItId\": \"2,5,7\" \n    },\n    {\n        \"idArticle\": 100,\n        \"nameArticle\": \"far®\",\n        \"color1SoldQuantity\": 0,\n        \"color2SoldQuantity\": 0,\n        \"color3SoldQuantity\": 7,\n        \"color4SoldQuantity\": 0,\n        \"clientSoldToItId\": \"2\"\n    }\n  ]\n}\n```\n")
        },
        content("user") {
            text("```json\n{\n  \"SoldArticlesTabelle\": [{\n\"clientSoldToItId\": 2,\n\"color1IdPicked\": 0,\n\"color1SoldQuantity\": 30,\n\"color2IdPicked\": 0,\n\"color2SoldQuantity\": 10,\n\"color3IdPicked\": 0,\n\"color3SoldQuantity\": 0,\n\"color4IdPicked\": 0,\n\"color4SoldQuantity\": 0,\n\"confimed\": false,\n\"date\": \"1730656574914\",\n\"idArticle\": 40,\n\"nameArticle\": \"ggg®\",\n\"vid\": 4\n},\n{\n\"clientSoldToItId\": 5,\n\"color1IdPicked\": 0,\n\"color1SoldQuantity\": 0,\n\"color2IdPicked\": 0,\n\"color2SoldQuantity\": 0,\n\"color3IdPicked\": 0,\n\"color3SoldQuantity\": 0,\n\"color4IdPicked\": 0,\n\"color4SoldQuantity\": 10,\n\"confimed\": false,\n\"date\": \"1730658367174\",\n\"idArticle\": 30,\n\"nameArticle\": \"fgg®\",\n\"vid\": 5\n}{\n\"clientSoldToItId\": 2,\n\"color1IdPicked\": 0,\n\"color1SoldQuantity\": 40,\n\"color2IdPicked\": 0,\n\"color2SoldQuantity\": 0,\n\"color3IdPicked\": 0,\n\"color3SoldQuantity\": 0,\n\"color4IdPicked\": 0,\n\"color4SoldQuantity\": 0,\n\"confimed\": false,\n\"date\": \"1730656574914\",\n\"idArticle\": 40,\n\"nameArticle\": \"ggg®\",\n\"vid\": 4\n}\n  ]\n}\n```\n")
        },
    )

    fun transactionPrompt() {
        Log.d(TAG, "Starting transaction prompt")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = UiState.Loading
                Log.d(TAG, "UI State set to Loading")

                Log.d(TAG, "Starting chat with history of ${chatHistory.size} messages")
                val chat = generativeAiViewModel.startChat(chatHistory)

                val response = chat.sendMessage(
                    content {
                        text("GroupeurBonCommendToSupplier")
                    }
                )

                response.text?.let { jsonResponse ->
                    Log.d(TAG, "Received response: $jsonResponse")

                    // Clean the JSON response
                    val cleanedJson = cleanJsonResponse(jsonResponse)
                    Log.d(TAG, "Cleaned JSON: $cleanedJson")

                    val groupedDataList = parseResponseToTabele(cleanedJson)
                    Log.d(TAG, "Parsed ${groupedDataList.size} items from response")

                    groupedDataList.forEach { groupedData ->
                        saveToFirebase(groupedData)
                    }

                    _uiState.value = UiState.Success("Transaction processed successfully")
                } ?: run {
                    _uiState.value = UiState.Error("Empty response received")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Transaction failed", e)
                _uiState.value = UiState.Error(e.message ?: "Transaction failed")
            }
        }
    }

    private fun cleanJsonResponse(response: String): String {
        // Extract JSON content from potential code blocks
        val jsonPattern = "```json\\s*(.+?)\\s*```".toRegex(RegexOption.DOT_MATCHES_ALL)
        val matchResult = jsonPattern.find(response)

        return matchResult?.groupValues?.get(1)?.trim() ?: response.trim()
    }

    private fun parseResponseToTabele(jsonResponse: String): List<GroupeurBonCommendToSupplierTabele> {
        Log.d(TAG, "Parsing JSON response")
        try {
            // Create a lenient JsonReader
            val reader = JsonReader(StringReader(jsonResponse))
            reader.isLenient = true

            val jsonObject = JsonParser.parseReader(reader).asJsonObject
            Log.d(TAG, "JSON parsed successfully")

            // Get the array directly from the object
            val groupeurArray = jsonObject.get("GroupeurBonCommendToSupplier").asJsonArray
                ?: throw IllegalStateException("GroupeurBonCommendToSupplier array not found")

            return groupeurArray.map { element ->
                val item = element.asJsonObject
                GroupeurBonCommendToSupplierTabele(
                    idArticle = item.get("idArticle").asInt,
                    nameArticle = item.get("nameArticle").asString,
                    color1SoldQuantity = item.get("color1SoldQuantity").asInt,
                    color2SoldQuantity = item.get("color2SoldQuantity").asInt,
                    color3SoldQuantity = item.get("color3SoldQuantity").asInt,
                    color4SoldQuantity = item.get("color4SoldQuantity").asInt,
                    clientSoldToItId = item.get("clientSoldToItId").asString
                )
            }
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "JSON syntax error", e)
            throw IllegalStateException("Invalid JSON format: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse JSON response", e)
            throw IllegalStateException("Failed to parse JSON response: ${e.message}")
        }
    }

    private suspend fun saveToFirebase(data: GroupeurBonCommendToSupplierTabele) {
        Log.d(TAG, "Saving to Firebase - ID: ${data.vid}, Article: ${data.nameArticle}")
        try {
            firebaseDatabase.getReference("K_GroupeurBonCommendToSupplierRef")
                .child(data.vid.toString())
                .setValue(data)
                .await()
            Log.d(TAG, "Successfully saved item ${data.vid} to Firebase")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save item ${data.vid} to Firebase", e)
            throw e
        }
    }
}
