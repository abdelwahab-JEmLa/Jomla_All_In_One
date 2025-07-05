package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.Repo3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1ProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrintReceiptHandler {
    private val PRINT_INTENT = "pe.diegoveloper.printing"
    private val TAG = "PrintReceiptHandler"

    data class ArticleImpression(
        val nomArticle: String,
        val quantite: Int,
        val prixUnitaire: Double,
        val couleur: String? = null
    )

    // New method for current vent system
    fun printVentReceipt(
        context: Context,
        fVentCouleurOperationRepository: Repo10OperationVentCouleur,
        bProduitInfosRepository: RepoM1ProduitInfos,
        b1CouleurOuGoutProduitDataBaseRepository: Repo3CouleurProduitInfos,
        client: HClientInfos?,
        scope: CoroutineScope? = null
    ) {
        val printFunction = {
            val dateString = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            // Get all vents that are not removed from cart
            val activeVents = fVentCouleurOperationRepository.onVentFilteredDatas.filter { vent ->
                vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                        vent.quantityAchete > 0
            }

            val productMap = mutableMapOf<String, MutableList<ArticleImpression>>()

            activeVents.forEach { vent ->
                // Get product details
                val product = bProduitInfosRepository.datasValue.find {
                    it.keyID == vent.parentM1ProduitInfosKeyId
                }

                // Get color details
                val colorInfo = b1CouleurOuGoutProduitDataBaseRepository.datasValue.find {
                    it.key == vent.parentM3CouleurProduitInfosKeyID
                }

                val productName = product?.nom?.takeIf { it.isNotBlank() }
                    ?: product?.nomMutable?.takeIf { it.isNotBlank() }
                    ?: "Produit #${vent.parentM1ProduitInfosKeyId}"

                val colorName = colorInfo?.nomCouleurStrSiSonImageDispo ?: "Couleur standard"
                val articleName = if (colorName != "Couleur standard") {
                    "$productName ($colorName)"
                } else {
                    productName
                }

                val article = ArticleImpression(
                    nomArticle = articleName,
                    quantite = vent.quantityAchete,
                    prixUnitaire = vent.provisoireMonPrix,
                    couleur = colorName
                )

                if (productMap.containsKey(productName)) {
                    productMap[productName]?.add(article)
                } else {
                    productMap[productName] = mutableListOf(article)
                }
            }

            // Flatten articles and group similar items
            val finalArticles = mutableListOf<ArticleImpression>()
            productMap.values.forEach { articles ->
                articles.groupBy { "${it.nomArticle}_${it.prixUnitaire}" }.forEach { (_, groupedArticles) ->
                    val firstArticle = groupedArticles.first()
                    val totalQuantity = groupedArticles.sumOf { it.quantite }
                    finalArticles.add(
                        firstArticle.copy(quantite = totalQuantity)
                    )
                }
            }

            val creditBalance = client?.currentCreditBalance ?: 0.0
            val clientName = client?.nom?.takeIf { it.isNotBlank() } ?: "Client"

            val (texteImprimable, totalBon) = prepareTexteToPrint(
                nomClient = clientName,
                dateString = dateString,
                articles = finalArticles,
                ancienCredits = creditBalance
            )

            // Log the receipt content before printing
            logReceiptContent(
                clientName = clientName,
                dateString = dateString,
                articles = finalArticles,
                totalBon = totalBon,
                creditBalance = creditBalance,
                receiptText = texteImprimable.toString()
            )

            val intent = Intent(PRINT_INTENT).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, texteImprimable.toString())
            }
            ContextCompat.startActivity(context, intent, null)
        }

        if (scope != null) {
            scope.launch { printFunction() }
        } else {
            printFunction()
        }
    }

    private fun prepareTexteToPrint(
        nomClient: String,
        dateString: String,
        articles: List<ArticleImpression>,
        ancienCredits: Double
    ): Pair<StringBuilder, Double> {
        val texteImprimable = StringBuilder()
        var totaleBon = 0.0
        var pageCounter = 0

        texteImprimable.apply {
            append("<BIG><CENTER>Abdelwahab<BR>")
            append("<BIG><CENTER>JeMla.Com<BR>")
            append("<SMALL><CENTER>0553885037<BR>")
            append("<SMALL><CENTER>Facture<BR>")
            append("<BR>")
            append("<SMALL><CENTER>$nomClient                        $dateString<BR>")
            append("<BR>")
            append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
            append("<SMALL><BOLD>    Quantité      Tariff         <NORMAL>Sous-total<BR>")
            append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
        }

        articles.forEachIndexed { index, article ->
            val arrondi = round(article.prixUnitaire * 10) / 10
            val subtotal = arrondi * article.quantite
            if (subtotal != 0.0) {
                texteImprimable.apply {
                    append("<MEDIUM1><LEFT>${article.nomArticle}<BR>")
                    append("    <MEDIUM1><LEFT>${article.quantite}   ")
                    append("<MEDIUM1><LEFT>${arrondi}Da   ")
                    append("<SMALL>$subtotal<BR>")
                    append("<LEFT><NORMAL><MEDIUM1>---------------------<BR>")
                }

                totaleBon += subtotal
                if ((index + 1) % 15 == 0) {
                    pageCounter++
                    texteImprimable.append("<BR><CENTER>PAGE $pageCounter<BR><BR><BR>")
                }
            }
        }

        texteImprimable.apply {
            append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
            append("<BR><BR>")
            append("<MEDIUM1><CENTER>Total<BR>")
            append("<MEDIUM3><CENTER>${round(totaleBon * 10) / 10}Da<BR>")
            if (ancienCredits < 0) {
                append("<MEDIUM1><CENTER>Credit Du Compte actuel<BR>")
                append("<MEDIUM2><CENTER>${round(ancienCredits * 10) / 10}Da<BR>")
            }
            append("<CENTER>---------------------<BR>")
            append("<BR><BR><BR>>")
        }

        return Pair(texteImprimable, totaleBon)
    }

    /**
     * Logs the receipt content for debugging and monitoring purposes
     */
    private fun logReceiptContent(
        clientName: String,
        dateString: String,
        articles: List<ArticleImpression>,
        totalBon: Double,
        creditBalance: Double,
        receiptText: String
    ) {
        Log.d(TAG, "=== RECEIPT PRINT LOG ===")
        Log.d(TAG, "Client: $clientName")
        Log.d(TAG, "Date: $dateString")
        Log.d(TAG, "Articles count: ${articles.size}")

        articles.forEachIndexed { index, article ->
            val subtotal = round(article.prixUnitaire * article.quantite)
            Log.d(TAG, "Article ${index + 1}: ${article.nomArticle}")
            Log.d(TAG, "  - Quantity: ${article.quantite}")
            Log.d(TAG, "  - Unit Price: ${round(article.prixUnitaire)}Da")
            Log.d(TAG, "  - Subtotal: ${subtotal}Da")
            if (article.couleur != null && article.couleur != "Couleur standard") {
                Log.d(TAG, "  - Color: ${article.couleur}")
            }
        }

        Log.d(TAG, "Total Amount: ${round(totalBon)}Da")
        if (creditBalance < 0) {
            Log.d(TAG, "Client Credit Balance: ${round(creditBalance)}Da")
        }

        Log.d(TAG, "=== RAW RECEIPT TEXT ===")
        // Clean the receipt text for better readability in logs
        val cleanedText = receiptText
            .replace("<BIG>", "")
            .replace("<CENTER>", "")
            .replace("<SMALL>", "")
            .replace("<LEFT>", "")
            .replace("<NORMAL>", "")
            .replace("<MEDIUM1>", "")
            .replace("<MEDIUM2>", "")
            .replace("<MEDIUM3>", "")
            .replace("<BOLD>", "")
            .replace("<BR>", "\n")

        Log.d(TAG, cleanedText)
        Log.d(TAG, "=== END RECEIPT LOG ===")
    }

    private fun round(value: Double): Double = kotlin.math.round(value * 10) / 10
}
