package z_GeminiAi

import android.graphics.Bitmap
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
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// Add this data class to store the grouped data
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

// Update BakingViewModel with new functionality
class BakingViewModel : ViewModel() {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState = _uiState.asStateFlow()

    private val gson = Gson()
    private val model = GenerativeModel(
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
            text("En tant que modèle linguistique, je fonctionne comme un assistant IA qui traite et génère du texte. Ma logique pour créer le GroupeurBonCommendToSupplier à partir de la SoldArticlesTabelle suit ces étapes :\n\nAnalyser les données d'entrée : Je parcours chaque objet JSON dans le tableau SoldArticlesTabelle. J'identifie les clés importantes pour le regroupement, notamment idArticle, nameArticle, clientSoldToItId, et les quantités de couleur (color1SoldQuantity, color2SoldQuantity, etc.).\n\nCréer une structure de données pour le résultat : J'initialise un tableau vide, GroupeurBonCommendToSupplier, qui contiendra les objets regroupés.\n\nGrouper par article : Pour chaque objet de la SoldArticlesTabelle, je vérifie si un objet avec le même idArticle existe déjà dans GroupeurBonCommendToSupplier.\n\nSi l'article n'existe pas : Je crée un nouvel objet avec les valeurs de idArticle, nameArticle, les quantités de couleur de l'objet courant, et j'initialise clientSoldToItId avec la valeur actuelle de clientSoldToItId. J'ajoute ensuite ce nouvel objet à GroupeurBonCommendToSupplier.\n\nSi l'article existe déjà : Je récupère l'objet existant dans GroupeurBonCommendToSupplier. J'ajoute les quantités de couleur de l'objet courant aux quantités correspondantes de l'objet existant. Je mets à jour le champ clientSoldToItId en ajoutant l'ID client actuel, séparé par une virgule si nécessaire (par exemple, \"2,5\").\n\nGénérer la sortie JSON : Une fois que tous les objets de la SoldArticlesTabelle ont été traités, je renvoie le tableau GroupeurBonCommendToSupplier au format JSON.")
        }
    )

    private val chatHistory = listOf(
        content("user") {
            text("""
                {"SoldArticlesTabelle": [{
                "clientSoldToItId": 2,
                "color1IdPicked": 0,
                "color1SoldQuantity": 15,
                "color2IdPicked": 0,
                "color2SoldQuantity": 0,
                "color3IdPicked": 0,
                "color3SoldQuantity": 0,
                "color4IdPicked": 0,
                "color4SoldQuantity": 0,
                "confimed": false,
                "date": "1730656574914",
                "idArticle": 65,
                "nameArticle": "Silca®",
                "vid": 4
                }]}
            """)
            text("GroupeurBonCommendToSupplier")
        },
        content("model") {
            text("""
                {
                  "GroupeurBonCommendToSupplier": [
                    {
                      "idArticle": 65,
                      "nameArticle": "Silca®",
                      "color1SoldQuantity": 15,
                      "color2SoldQuantity": 0,
                      "color3SoldQuantity": 0,
                      "color4SoldQuantity": 0,
                      "clientSoldToItId": "2"
                    }
                  ]
                }
            """)
        }
    )
    fun sendPrompt(
        bitmaps: List<Bitmap>,
        prompt: String
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = model.generateContent(
                    content {
                        bitmaps.forEach { image(it) }
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    private fun parseResponseToTabele(jsonResponse: String): List<GroupeurBonCommendToSupplierTabele> {
        return try {
            val jsonObject = JsonParser.parseString(jsonResponse).asJsonObject
            val groupeurArray = jsonObject
                .getAsJsonObject("GroupeurBonCommendToSupplier")
                .asJsonArray

            groupeurArray.map { element ->
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
        } catch (e: Exception) {
            throw IllegalStateException("Failed to parse JSON response: ${e.message}")
        }
    }

    fun transactionPrompt() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = UiState.Loading

                val chat = model.startChat(chatHistory)
                val response = chat.sendMessage(
                    content {
                        text("Convert current SoldArticlesTabelle to GroupeurBonCommendToSupplier format")
                    }
                )

                response.text?.let { jsonResponse ->
                    val groupedDataList = parseResponseToTabele(jsonResponse)
                    groupedDataList.forEach { groupedData ->
                        saveToFirebase(groupedData)
                    }
                    _uiState.value = UiState.Success("Transaction processed successfully")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Transaction failed")
            }
        }
    }

    private suspend fun saveToFirebase(data: GroupeurBonCommendToSupplierTabele) {
        firebaseDatabase.getReference("K_GroupeurBonCommendToSupplierRef")
            .child(data.vid.toString())
            .setValue(data)
            .await()
    }


    private fun parseResponseToTabele2(jsonResponse: String): List<GroupeurBonCommendToSupplierTabele> {
        return try {
            // Parse JSON string to our data class
            val response = gson.fromJson(jsonResponse, GroupeurResponse::class.java)

            // Convert each item to GroupeurBonCommendToSupplierTabele
            response.items.map { item ->
                GroupeurBonCommendToSupplierTabele(
                    idArticle = item.idArticle,
                    nameArticle = item.nameArticle,
                    color1SoldQuantity = item.color1SoldQuantity,
                    color2SoldQuantity = item.color2SoldQuantity,
                    color3SoldQuantity = item.color3SoldQuantity,
                    color4SoldQuantity = item.color4SoldQuantity,
                    clientSoldToItId = item.clientSoldToItId
                )
            }
        } catch (e: Exception) {
            throw IllegalStateException("Failed to parse JSON response: ${e.message}")
        }
    }

    fun transactionPromptWithouTexr() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = UiState.Loading

                val chat = model.startChat()
                val response = chat.sendMessage(
                    content {
                        text("Convert SoldArticlesTabelle to GroupeurBonCommendToSupplier format")
                    }
                )

                response.text?.let { jsonResponse ->
                    // Parse JSON and convert to GroupeurBonCommendToSupplierTabele
                    val groupedDataList = parseResponseToTabele(jsonResponse)

                    // Save each item to Firebase
                    groupedDataList.forEach { groupedData ->
                        firebaseDatabase.getReference("K_GroupeurBonCommendToSupplierRef")
                            .child(groupedData.vid.toString())
                            .setValue(groupedData)
                    }

                    _uiState.value = UiState.Success("Transaction processed successfully")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Transaction failed")
            }
        }
    }
}

// Data class to match JSON structure
data class GroupeurResponse(
    @SerializedName("GroupeurBonCommendToSupplier")
    val items: List<GroupeurItem>
)

data class GroupeurItem(
    @SerializedName("idArticle")
    val idArticle: Int,
    @SerializedName("nameArticle")
    val nameArticle: String,
    @SerializedName("color1SoldQuantity")
    val color1SoldQuantity: Int,
    @SerializedName("color2SoldQuantity")
    val color2SoldQuantity: Int,
    @SerializedName("color3SoldQuantity")
    val color3SoldQuantity: Int,
    @SerializedName("color4SoldQuantity")
    val color4SoldQuantity: Int,
    @SerializedName("clientSoldToItId")
    val clientSoldToItId: String
)
