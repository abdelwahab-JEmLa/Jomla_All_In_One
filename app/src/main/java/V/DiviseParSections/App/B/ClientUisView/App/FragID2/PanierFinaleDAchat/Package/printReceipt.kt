package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.B_ClientInfosProtoJuin3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.TransactionCommercial
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val PRINT_INTENT = "pe.diegoveloper.printing"
private const val TAG = "PrintService"

class  PrintReceiptHandler

data class ArticleImpression(
    val nomArticle: String,
    val quantite: Int,
    val prixUnitaire: Double,
    val couleur: String? = null
)

fun printReceipt(
    context: Context,
    bonAchat: TransactionCommercial?,
    repositorysModel: GroupeRepositorysProtoAvJuin3Model,
    scope: CoroutineScope? = null,
    datasB_ClientInfosProtoJuin3List: List<B_ClientInfosProtoJuin3>
) {
    if (bonAchat == null) return

    val printFunction = {
        // Get client information
        val client = datasB_ClientInfosProtoJuin3List.find { it.id == bonAchat.clientAcheteurID }

        // Generate current date string
        val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date())

        // Get all confirmed products in this receipt
        val products = repositorysModel.repositoryC2_ProduitAcheteOperation
            .modelDatasSnapList
            .filter {
                it.parent_1_3_TransactionCommercial == bonAchat.vid &&
                        it.etateActuellementEst == _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
            }

        // Create add map to group articles by product name and sum quantities
        val productMap = mutableMapOf<String, ArticleImpression>()
        var totalAmount = 0.0

        // Process each product and sum quantities across its colors
        products.forEach { product ->
            // Get product details
            val productDetails = repositorysModel._2_1_ProduitsDataBase_Repository
                .modelDatasSnapList
                .find { it.vid == product.produitAcheterID }

            val productName = productDetails?.nom ?: "_015_Produits"

            // Use provisional price if available, otherwise use the default price
            val productPrice = if (product.provisoireMonPrix > 0.0) {
                product.provisoireMonPrix
            } else {
                productDetails?.monPrixVent ?: 0.0
            }

            // Get colors with quantity > 0 and sum their quantities
            val totalQuantity = repositorysModel._1_1_CouleurAcheteOperation_Repository
                .modelDatasSnapList
                .filter {
                    it.parentProduitAchateOperationVID == product.vid &&
                            it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI &&
                            it.totaleQuantity > 0
                }
                .sumOf { it.totaleQuantity }

            // Skip products with no quantities
            if (totalQuantity <= 0) return@forEach

            // If product already exists in map, upsertLenceCommandeRepoGroupedProtoAvantJuin3 quantity; otherwise upsert new entry
            productMap[productName]?.let { existingArticle ->
                productMap[productName] = existingArticle.copy(
                    quantite = existingArticle.quantite + totalQuantity
                )
            } ?: run {
                productMap[productName] = ArticleImpression(
                    nomArticle = productName,
                    quantite = totalQuantity,
                    prixUnitaire = productPrice
                )
            }
        }

        // Convert map to list of articles
        val articles = productMap.values.toList()

        // Calculate total amount after grouping
        totalAmount = articles.sumOf { it.quantite * it.prixUnitaire }

        // Get client credit balance
        val creditBalance = client?.currentCreditBalance ?: 0.0

        // Prepare text for printing
        val (texteImprimable, totalBon) = prepareTexteToPrint(
            nomClient = client?.nom ?: "ClientAchteur",
            dateString = dateString,
            articles = articles,
            ancienCredits = creditBalance
        )

        // Send to printer
        val intent = Intent(PRINT_INTENT).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, texteImprimable.toString())
        }
        ContextCompat.startActivity(context, intent, null)
        Log.d(TAG, "Impression lancée. Total: $totalBon")
    }

    // Execute in coroutine scope if provided, otherwise run directly
    if (scope != null) {
        scope.launch {
            printFunction()
        }
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

private fun round(value: Double): Double = kotlin.math.round(value * 10) / 10
