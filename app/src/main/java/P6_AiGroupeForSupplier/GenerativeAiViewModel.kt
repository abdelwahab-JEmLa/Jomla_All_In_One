package P6_AiGroupeForSupplier

import Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Models.SoldArticlesTabelle
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.StringReader


class GenerativeAiViewModel : ViewModel() {
    private val TAG = "GenerativeAiViewModel"
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val _uiStateInterface = MutableStateFlow<UiStateInterface>(UiStateInterface.Initial)
    val uiState = _uiStateInterface.asStateFlow()
    private val refSoldArticlesTabelle = firebaseDatabase.getReference("O_SoldArticlesTabelle")

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
        systemInstruction = content { text("Je vais décrire la logique de transformation entre les deux structures de données de manière descriptive :\n\nRegroupement par Article\n\n\nLe système prend plusieurs commandes individuelles et les regroupe par article (idArticle)\nPour chaque article unique, il crée une seule entrée dans le résultat final\n\n\nAgrégation des Quantités\n\n\nPour chaque article, le système additionne les quantités des différentes couleurs (color1SoldQuantity à color4SoldQuantity)\nPar exemple, si deux clients commandent le même article avec color1SoldQuantity = 1, le résultat final aura color1SoldQuantity = 2\n\n\nGestion des Clients\n\n\nLe système collecte tous les IDs des clients (clientSoldToItId) qui ont commandé chaque article\nCes IDs sont combinés dans une chaîne de caractères séparée par des virgules\nPar exemple, si les clients 1 et 3 commandent le même article, clientSoldToItId devient \"1,3\"\n\n\nStructure du Résultat\n\n\nLe résultat final est encapsulé dans un objet avec une clé \"GroupeurBonCommendToSupplier\"\nChaque article conserve ses informations de base (idArticle, nameArticle)\nLes quantités sont agrégées\nLa liste des clients est consolidée\n\n\nExemple Concret\n\n\nDans les données initiales, l'article \"pupg caramel\" (idArticle: 1072) apparaît deux fois:\n\nUne fois pour le client 1 avec color1SoldQuantity = 1\nUne fois pour le client 3 avec color1SoldQuantity = 1\n\n\nDans le résultat final, il apparaît une seule fois avec:\n\ncolor1SoldQuantity = 2 (1+1)\nclientSoldToItId = \"1,3\"\n\n\n\nCette transformation permet de consolider les commandes multiples en une vue synthétique par article, utile pour la gestion des commandes fournisseurs.") },

        ).also { Log.d(TAG, "GenerativeModel initialized with config: temperature=1f, topK=40, topP=0.95f") }

    /**transactionPrompt*/
    val chatHistory = listOf(
        content("user") {
            text("```json\n{\n null: [\n    {\n\"clientSoldToItId\": 2,\n\"color1IdPicked\": 0,\n\"color1SoldQuantity\": 15,\n\"color2IdPicked\": 0,\n\"color2SoldQuantity\": 0,\n\"color3IdPicked\": 0,\n\"color3SoldQuantity\": 0,\n\"color4IdPicked\": 0,\n\"color4SoldQuantity\": 0,\n\"confimed\": false,\n\"date\": \"1730656574914\",\n\"idArticle\": 65,\n\"nameArticle\": \"Silca®\",\n\"vid\": 4\n},\n{\n\"clientSoldToItId\": 5,\n\"color1IdPicked\": 0,\n\"color1SoldQuantity\": 0,\n\"color2IdPicked\": 0,\n\"color2SoldQuantity\": 0,\n\"color3IdPicked\": 0,\n\"color3SoldQuantity\": 0,\n\"color4IdPicked\": 0,\n\"color4SoldQuantity\": 10,\n\"confimed\": false,\n\"date\": \"1730658367174\",\n\"idArticle\": 65,\n\"nameArticle\": \"Silca®\",\n\"vid\": 5\n},{\n\"clientSoldToItId\": 2,\n\"color1IdPicked\": 0,\n\"color1SoldQuantity\": 0,\n\"color2IdPicked\": 0,\n\"color2SoldQuantity\": 0,\n\"color3IdPicked\": 0,\n\"color3SoldQuantity\": 7,\n\"color4IdPicked\": 0,\n\"color4SoldQuantity\": 0,\n\"confimed\": false,\n\"date\": \"1730658367174\",\n\"idArticle\": 100,\n\"nameArticle\": \"far®\",\n\"vid\": 6\n},{\n\"clientSoldToItId\": 7,\n\"color1IdPicked\": 0,\n\"color1SoldQuantity\": 10,\n\"color2IdPicked\": 0,\n\"color2SoldQuantity\": 0,\n\"color3IdPicked\": 0,\n\"color3SoldQuantity\": 0,\n\"color4IdPicked\": 0,\n\"color4SoldQuantity\": 10,\n\"confimed\": false,\n\"date\": \"1730658367174\",\n\"idArticle\": 65,\n\"nameArticle\": \"Silca®\",\n\"vid\": 7\n}\n]\n}\n```")
            text("Groupe IT")
        },
        content("model") {
            text("```json\n{\n  \"GroupeurBonCommendToSupplier\": [\n    {\n        \"idArticle\": 65,\n        \"nameArticle\": \"Silca®\",\n        \"color1SoldQuantity\": 15,\n        \"color2SoldQuantity\": 0,\n        \"color3SoldQuantity\": 0,\n        \"color4SoldQuantity\": 10,\n        \"clientSoldToItId\": \"2,5,7\" \n    },\n    {\n        \"idArticle\": 100,\n        \"nameArticle\": \"far®\",\n        \"color1SoldQuantity\": 0,\n        \"color2SoldQuantity\": 0,\n        \"color3SoldQuantity\": 7,\n        \"color4SoldQuantity\": 0,\n        \"clientSoldToItId\": \"2\"\n    }\n  ]\n}\n```\n")
        },
        content("user") {
            text("```json\n{\n  \"SoldArticlesTabelle\": [{\n\"clientSoldToItId\": 2,\n\"color1IdPicked\": 0,\n\"color1SoldQuantity\": 30,\n\"color2IdPicked\": 0,\n\"color2SoldQuantity\": 10,\n\"color3IdPicked\": 0,\n\"color3SoldQuantity\": 0,\n\"color4IdPicked\": 0,\n\"color4SoldQuantity\": 0,\n\"confimed\": false,\n\"date\": \"1730656574914\",\n\"idArticle\": 40,\n\"nameArticle\": \"ggg®\",\n\"vid\": 4\n},\n{\n\"clientSoldToItId\": 5,\n\"color1IdPicked\": 0,\n\"color1SoldQuantity\": 0,\n\"color2IdPicked\": 0,\n\"color2SoldQuantity\": 0,\n\"color3IdPicked\": 0,\n\"color3SoldQuantity\": 0,\n\"color4IdPicked\": 0,\n\"color4SoldQuantity\": 10,\n\"confimed\": false,\n\"date\": \"1730658367174\",\n\"idArticle\": 30,\n\"nameArticle\": \"fgg®\",\n\"vid\": 5\n}{\n\"clientSoldToItId\":3,\n\"color1IdPicked\": 0,\n\"color1SoldQuantity\": 40,\n\"color2IdPicked\": 0,\n\"color2SoldQuantity\": 0,\n\"color3IdPicked\": 0,\n\"color3SoldQuantity\": 0,\n\"color4IdPicked\": 0,\n\"color4SoldQuantity\": 0,\n\"confimed\": false,\n\"date\": \"1730656574914\",\n\"idArticle\": 40,\n\"nameArticle\": \"ggg®\",\n\"vid\": 4\n}\n  ]\n}\n```\n")
            text("Groupe IT")
        },
        content("model") {
            text("```json\n{\n  \"GroupeurBonCommendToSupplier\": [\n    {\n      \"idArticle\": 40,\n      \"nameArticle\": \"ggg®\",\n      \"color1SoldQuantity\": 70,\n      \"color2SoldQuantity\": 10,\n      \"color3SoldQuantity\": 0,\n      \"color4SoldQuantity\": 0,\n      \"clientSoldToItId\": \"2,3\"\n    },\n    {\n      \"idArticle\": 30,\n      \"nameArticle\": \"fgg®\",\n      \"color1SoldQuantity\": 0,\n      \"color2SoldQuantity\": 0,\n      \"color3SoldQuantity\": 0,\n      \"color4SoldQuantity\": 10,\n      \"clientSoldToItId\": \"5\"\n    }\n  ]\n}\n```\n")
        },
        content("user") {
            text("\n```json\n[\n  {\n    \"ClientSoldToItId\": \"1\",\n    \"idArticle\": 484,\n    \"nameArticle\": \"Wiwi Halkom®\",\n    \"color1IdPicked\": 52,\n    \"color1SoldQuantity\": 2,\n    \"color2IdPicked\": null,\n    \"color2SoldQuantity\": 0,\n    \"date\": \"1730881542266\",\n    \"confimed\": false\n  },\n  {\n    \"ClientSoldToItId\": \"1\",\n    \"idArticle\": 3557,\n    \"nameArticle\": \"hnina\",\n    \"color1IdPicked\": 1,\n    \"color1SoldQuantity\": 1,\n    \"color2IdPicked\": null,\n    \"color2SoldQuantity\": 0,\n    \"date\": \"1730881582192\",\n    \"confimed\": false\n  },\n  {\n    \"ClientSoldToItId\": \"1\",\n    \"idArticle\": 472,\n    \"nameArticle\": \"Kinder Joy®\",\n    \"color1IdPicked\": 49,\n    \"color1SoldQuantity\": 1,\n    \"color2IdPicked\": 48,\n    \"color2SoldQuantity\": 1,\n    \"date\": \"1730881748326\",\n    \"confimed\": false\n  },\n  {\n    \"ClientSoldToItId\": \"3\",\n    \"idArticle\": 1072,\n    \"nameArticle\": \"pupg caramel\",\n    \"color1IdPicked\": 152,\n    \"color1SoldQuantity\": 1,\n    \"color2IdPicked\": null,\n    \"color2SoldQuantity\": 0,\n    \"date\": \"1730881614758\",\n    \"confimed\": false\n  },\n  {\n    \"ClientSoldToItId\": \"3\",\n    \"idArticle\": 472,\n    \"nameArticle\": \"Kinder Joy®\",\n    \"color1IdPicked\": 49,\n    \"color1SoldQuantity\": 1,\n    \"color2IdPicked\": null,\n    \"color2SoldQuantity\": 0,\n    \"date\": \"1730881624639\",\n    \"confimed\": false\n  },\n  {\n    \"ClientSoldToItId\": \"3\",\n    \"idArticle\": 447,\n    \"nameArticle\": \"Rigolo®\",\n    \"color1IdPicked\": 52,\n    \"color1SoldQuantity\": 3,\n    \"color2IdPicked\": null,\n    \"color2SoldQuantity\": 0,\n    \"date\": \"1730881633492\",\n    \"confimed\": false\n  },\n  {\n    \"ClientSoldToItId\": \"3\",\n    \"idArticle\": 3558,\n    \"nameArticle\": \"hnina\",\n    \"color1IdPicked\": 1,\n    \"color1SoldQuantity\": 2,\n    \"color2IdPicked\": null,\n    \"color2SoldQuantity\": 0,\n    \"date\": \"1730881653581\",\n    \"confimed\": false\n  },\n  {\n    \"ClientSoldToItId\": \"3\",\n    \"idArticle\": 842,\n    \"nameArticle\": \"optilla 1k\",\n    \"color1IdPicked\": 93,\n    \"color1SoldQuantity\": 1,\n    \"color2IdPicked\": null,\n    \"color2SoldQuantity\": 0,\n    \"date\": \"1730881676393\",\n    \"confimed\": false\n  },\n  {\n    \"ClientSoldToItId\": \"3\",\n    \"idArticle\": 3559,\n    \"nameArticle\": \"optil70\",\n    \"color1IdPicked\": 1,\n    \"color1SoldQuantity\": 1,\n    \"color2IdPicked\": null,\n    \"color2SoldQuantity\": 0,\n    \"date\": \"1730881689481\",\n    \"confimed\": false\n  }\n]\n```\n\n\n\n")
            text("Groupe IT\n\n\n")
        },
        content("model") {
            text("```json\n{\n  \"GroupeurBonCommendToSupplier\": [\n    {\n      \"idArticle\": 484,\n      \"nameArticle\": \"Wiwi Halkom®\",\n      \"color1SoldQuantity\": 2,\n      \"color2SoldQuantity\": 0,\n      \"clientSoldToItId\": \"1\"\n    },\n    {\n      \"idArticle\": 3557,\n      \"nameArticle\": \"hnina\",\n      \"color1SoldQuantity\": 3,\n      \"color2SoldQuantity\": 0,\n      \"clientSoldToItId\": \"1,3\"\n    },\n    {\n      \"idArticle\": 472,\n      \"nameArticle\": \"Kinder Joy®\",\n      \"color1SoldQuantity\": 2,\n      \"color2SoldQuantity\": 1,\n      \"clientSoldToItId\": \"1,3\"\n    },\n    {\n      \"idArticle\": 1072,\n      \"nameArticle\": \"pupg caramel\",\n      \"color1SoldQuantity\": 1,\n      \"color2SoldQuantity\": 0,\n      \"clientSoldToItId\": \"3\"\n    },\n    {\n      \"idArticle\": 447,\n      \"nameArticle\": \"Rigolo®\",\n      \"color1SoldQuantity\": 3,\n      \"color2SoldQuantity\": 0,\n      \"clientSoldToItId\": \"3\"\n    },\n    {\n      \"idArticle\": 842,\n      \"nameArticle\": \"optilla 1k\",\n      \"color1SoldQuantity\": 1,\n      \"color2SoldQuantity\": 0,\n      \"clientSoldToItId\": \"3\"\n    },\n    {\n      \"idArticle\": 3559,\n      \"nameArticle\": \"optil70\",\n      \"color1SoldQuantity\": 1,\n      \"color2SoldQuantity\": 0,\n      \"clientSoldToItId\": \"3\"\n    }\n  ]\n}\n```\n")
        },
        content("user") {
            text("[\n  null,\n  {\n    \"clientSoldToItId\": 1,\n    \"color1IdPicked\": 152,\n    \"color1SoldQuantity\": 1,\n    \"color2IdPicked\": 0,\n    \"color2SoldQuantity\": 0,\n    \"color3IdPicked\": 0,\n    \"color3SoldQuantity\": 0,\n    \"color4IdPicked\": 0,\n    \"color4SoldQuantity\": 0,\n    \"confimed\": false,\n    \"date\": \"1730893482695\",\n    \"idArticle\": 1072,\n    \"nameArticle\": \"pupg caramel\",\n    \"vid\": 1\n  },\n  {\n    \"clientSoldToItId\": 1,\n    \"color1IdPicked\": 10,\n    \"color1SoldQuantity\": 1,\n    \"color2IdPicked\": 29,\n    \"color2SoldQuantity\": 1,\n    \"color3IdPicked\": 30,\n    \"color3SoldQuantity\": 1,\n    \"color4IdPicked\": 31,\n    \"color4SoldQuantity\": 1,\n    \"confimed\": false,\n    \"date\": \"1730893501475\",\n    \"idArticle\": 65,\n    \"nameArticle\": \"Silca®\",\n    \"vid\": 2\n  },\n  {\n    \"clientSoldToItId\": 1,\n    \"color1IdPicked\": 1,\n    \"color1SoldQuantity\": 1,\n    \"color2IdPicked\": 0,\n    \"color2SoldQuantity\": 0,\n    \"color3IdPicked\": 0,\n    \"color3SoldQuantity\": 0,\n    \"color4IdPicked\": 0,\n    \"color4SoldQuantity\": 0,\n    \"confimed\": false,\n    \"date\": \"1730893527556\",\n    \"idArticle\": 3559,\n    \"nameArticle\": \"hnina\",\n    \"vid\": 3\n  },\n  {\n    \"clientSoldToItId\": 3,\n    \"color1IdPicked\": 152,\n    \"color1SoldQuantity\": 1,\n    \"color2IdPicked\": 0,\n    \"color2SoldQuantity\": 0,\n    \"color3IdPicked\": 0,\n    \"color3SoldQuantity\": 0,\n    \"color4IdPicked\": 0,\n    \"color4SoldQuantity\": 0,\n    \"confimed\": false,\n    \"date\": \"1730893536586\",\n    \"idArticle\": 1072,\n    \"nameArticle\": \"pupg caramel\",\n    \"vid\": 4\n  },\n  {\n    \"clientSoldToItId\": 3,\n    \"color1IdPicked\": 10,\n    \"color1SoldQuantity\": 1,\n    \"color2IdPicked\": 0,\n    \"color2SoldQuantity\": 0,\n    \"color3IdPicked\": 30,\n    \"color3SoldQuantity\": 1,\n    \"color4IdPicked\": 31,\n    \"color4SoldQuantity\": 1,\n    \"confimed\": false,\n    \"date\": \"1730893545997\",\n    \"idArticle\": 65,\n    \"nameArticle\": \"Silca®\",\n    \"vid\": 5\n  },\n  {\n    \"clientSoldToItId\": 3,\n    \"color1IdPicked\": 0,\n    \"color1SoldQuantity\": 0,\n    \"color2IdPicked\": 68,\n    \"color2SoldQuantity\": 1,\n    \"color3IdPicked\": 0,\n    \"color3SoldQuantity\": 0,\n    \"color4IdPicked\": 0,\n    \"color4SoldQuantity\": 0,\n    \"confimed\": false,\n    \"date\": \"1730893554058\",\n    \"idArticle\": 588,\n    \"nameArticle\": \"Sunsilk®\",\n    \"vid\": 6\n  },\n  {\n    \"clientSoldToItId\": 3,\n    \"color1IdPicked\": 1,\n    \"color1SoldQuantity\": 1,\n    \"color2IdPicked\": 0,\n    \"color2SoldQuantity\": 0,\n    \"color3IdPicked\": 0,\n    \"color3SoldQuantity\": 0,\n    \"color4IdPicked\": 0,\n    \"color4SoldQuantity\": 0,\n    \"confimed\": false,\n    \"date\": \"1730893576154\",\n    \"idArticle\": 3561,\n    \"nameArticle\": \"optilla ne\",\n    \"vid\": 7\n  },\n  {\n    \"clientSoldToItId\": 3,\n    \"color1IdPicked\": 1,\n    \"color1SoldQuantity\": 1,\n    \"color2IdPicked\": 0,\n    \"color2SoldQuantity\": 0,\n    \"color3IdPicked\": 0,\n    \"color3SoldQuantity\": 0,\n    \"color4IdPicked\": 0,\n    \"color4SoldQuantity\": 0,\n    \"confimed\": false,\n    \"date\": \"1730893588846\",\n    \"idArticle\": 3559,\n    \"nameArticle\": \"hnina\",\n    \"vid\": 8\n  }\n]")
            text("Groupe IT")
        },
        content("model") {
            text("```json\n{\n  \"GroupeurBonCommendToSupplier\": [\n    {\n      \"idArticle\": 1072,\n      \"nameArticle\": \"pupg caramel\",\n      \"color1SoldQuantity\": 2,\n      \"color2SoldQuantity\": 0,\n      \"color3SoldQuantity\": 0,\n      \"color4SoldQuantity\": 0,\n      \"clientSoldToItId\": \"1,3\"\n    },\n    {\n      \"idArticle\": 65,\n      \"nameArticle\": \"Silca®\",\n      \"color1SoldQuantity\": 2,\n      \"color2SoldQuantity\": 1,\n      \"color3SoldQuantity\": 2,\n      \"color4SoldQuantity\": 2,\n      \"clientSoldToItId\": \"1,3\"\n    },\n    {\n      \"idArticle\": 3559,\n      \"nameArticle\": \"hnina\",\n      \"color1SoldQuantity\": 2,\n      \"color2SoldQuantity\": 0,\n      \"color3SoldQuantity\": 0,\n      \"color4SoldQuantity\": 0,\n      \"clientSoldToItId\": \"1,3\"\n    },\n    {\n      \"idArticle\": 588,\n      \"nameArticle\": \"Sunsilk®\",\n      \"color1SoldQuantity\": 0,\n      \"color2SoldQuantity\": 1,\n      \"color3SoldQuantity\": 0,\n      \"color4SoldQuantity\": 0,\n      \"clientSoldToItId\": \"3\"\n    },\n    {\n      \"idArticle\": 3561,\n      \"nameArticle\": \"optilla ne\",\n      \"color1SoldQuantity\": 1,\n      \"color2SoldQuantity\": 0,\n      \"color3SoldQuantity\": 0,\n      \"color4SoldQuantity\": 0,\n      \"clientSoldToItId\": \"3\"\n    }\n  ]\n}\n```\n")
        },
    )

    fun transactionPrompt() {
        Log.d(TAG, "Starting transaction prompt")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiStateInterface.value = UiStateInterface.Loading
                Log.d(TAG, "UI State set to Loading")

                // Fetch data from Firebase
                val snapshot = refSoldArticlesTabelle.get().await()
                val soldArticles = snapshot.children.mapNotNull { it.getValue(SoldArticlesTabelle::class.java) }

                // Convert Firebase data to JSON format
                val jsonData = buildString {
                    append("```json\n")
                    append("{\n")
                    append("  \"SoldArticlesTabelle\": [\n")
                    soldArticles.forEachIndexed { index, article ->
                        append("    {\n")
                        append("      \"clientSoldToItId\": ${article.clientSoldToItId},\n")
                        append("      \"color1IdPicked\": ${article.color1IdPicked},\n")
                        append("      \"color1SoldQuantity\": ${article.color1SoldQuantity},\n")
                        append("      \"color2IdPicked\": ${article.color2IdPicked},\n")
                        append("      \"color2SoldQuantity\": ${article.color2SoldQuantity},\n")
                        append("      \"color3IdPicked\": ${article.color3IdPicked},\n")
                        append("      \"color3SoldQuantity\": ${article.color3SoldQuantity},\n")
                        append("      \"color4IdPicked\": ${article.color4IdPicked},\n")
                        append("      \"color4SoldQuantity\": ${article.color4SoldQuantity},\n")
                        append("      \"confimed\": ${article.confimed},\n")
                        append("      \"date\": \"${article.date}\",\n")
                        append("      \"idArticle\": ${article.idArticle},\n")
                        append("      \"nameArticle\": \"${article.nameArticle}\",\n")
                        append("      \"vid\": ${article.vid}\n")
                        append("    }${if (index < soldArticles.size - 1) "," else ""}\n")
                    }
                    append("  ]\n")
                    append("}\n")
                    append("```\n")
                }

                Log.d(TAG, "Starting chat with history of ${chatHistory.size} messages")
                val chat = generativeAiViewModel.startChat(chatHistory)

                val response = chat.sendMessage(
                    content {
                        text(jsonData)
                        text("Groupe IT\n\n\n")
                    }
                )

                response.text?.let { jsonResponse ->
                    Log.d(TAG, "Received response: $jsonResponse")
                    val cleanedJson = cleanJsonResponse(jsonResponse)
                    Log.d(TAG, "Cleaned JSON: $cleanedJson")

                    // Delete existing data before saving new data
                    firebaseDatabase.getReference("K_GroupeurBonCommendToSupplierRef")
                        .removeValue()
                        .await()
                    Log.d(TAG, "Cleared existing GroupeurBonCommendToSupplier data")

                    val groupedDataList = parseResponseToTabele(cleanedJson)
                    Log.d(TAG, "Parsed ${groupedDataList.size} items from response")

                    groupedDataList.forEach { groupedData ->
                        saveToFirebase(groupedData)
                    }

                    _uiStateInterface.value = UiStateInterface.Success("Transaction processed successfully")
                } ?: run {
                    _uiStateInterface.value = UiStateInterface.Error("Empty response received")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Transaction failed", e)
                _uiStateInterface.value = UiStateInterface.Error(e.message ?: "Transaction failed")
            }
        }
    }
    private suspend fun getNextVid(): Long {
        return try {
            val snapshot = firebaseDatabase.getReference("K_GroupeurBonCommendToSupplierRef")
                .get()
                .await()

            (snapshot.children.mapNotNull {
                it.key?.toLongOrNull()
            }.maxOrNull() ?: 0) + 1
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get next vid", e)
            1L // Start from 1 if we can't get the max
        }
    }

    private fun parseResponseToTabele(jsonResponse: String): List<GroupeurBonCommendToSupplierTabele> {
        Log.d(TAG, "Parsing JSON response")
        try {
            val reader = JsonReader(StringReader(jsonResponse))
            reader.isLenient = true

            val jsonObject = JsonParser.parseReader(reader).asJsonObject
            Log.d(TAG, "JSON parsed successfully")

            val groupeurArray = jsonObject.get("GroupeurBonCommendToSupplier").asJsonArray
                ?: throw IllegalStateException("GroupeurBonCommendToSupplier array not found")

            return runBlocking {
                val nextVid = getNextVid()
                groupeurArray.mapIndexed { index, element ->
                    val item = element.asJsonObject
                    GroupeurBonCommendToSupplierTabele(
                        vid = nextVid + index,
                        idArticle = item.get("idArticle")?.asInt
                            ?: throw IllegalStateException("idArticle is required"),
                        nameArticle = item.get("nameArticle")?.asString
                            ?: throw IllegalStateException("nameArticle is required"),
                        color1SoldQuantity = item.get("color1SoldQuantity")?.asInt ?: 0,
                        color2SoldQuantity = item.get("color2SoldQuantity")?.asInt ?: 0,
                        color3SoldQuantity = item.get("color3SoldQuantity")?.asInt ?: 0,
                        color4SoldQuantity = item.get("color4SoldQuantity")?.asInt ?: 0,
                        clientSoldToItId = item.get("clientSoldToItId")?.asString
                            ?: throw IllegalStateException("clientSoldToItId is required")
                    )
                }
            }
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "JSON syntax error", e)
            throw IllegalStateException("Invalid JSON format: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse JSON response", e)
            throw IllegalStateException("Failed to parse JSON response: ${e.message}")
        }
    }
    private fun cleanJsonResponse(response: String): String {
        // Extract JSON content from potential code blocks
        val jsonPattern = "```json\\s*(.+?)\\s*```".toRegex(RegexOption.DOT_MATCHES_ALL)
        val matchResult = jsonPattern.find(response)

        return matchResult?.groupValues?.get(1)?.trim() ?: response.trim()
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

    fun sendPrompt(
        bitmaps: List<Bitmap>,
        prompt: String
    ) {
        Log.d(TAG, "sendPrompt() called with ${bitmaps.size} bitmaps and prompt: $prompt")
        _uiStateInterface.value = UiStateInterface.Loading
        Log.d(TAG, "UI State set to Loading")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Generating content with images and prompt")
                val response = generativeAiViewModel.generateContent(
                    content {
                        bitmaps.forEach { image(it) }
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    Log.d(TAG, "Content generated successfully: ${outputContent.take(100)}...")
                    _uiStateInterface.value = UiStateInterface.Success(outputContent)
                } ?: run {
                    Log.w(TAG, "Generated content was null")
                    _uiStateInterface.value = UiStateInterface.Error("Generated content was empty")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error generating content", e)
                _uiStateInterface.value = UiStateInterface.Error(e.localizedMessage ?: "")
            }
        }
    }


}
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
