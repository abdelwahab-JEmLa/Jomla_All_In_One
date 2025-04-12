package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.PanieAchates.APP.ViewModel

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchat
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Constants
private const val PRINT_INTENT = "pe.diegoveloper.printing"
private const val TAG = "PrintService"


data class ArticleImpression(
    val nomArticle: String,
    val quantite: Int,
    val prixUnitaire: Double,
    val couleur: String? = null
)


fun printReceipt(
    context: Context,
    bonAchat: _1_3_BonAchat?,
    repositorysModel: _0_0_HeadOfRepositorys_Model,
    database: AppDatabase,
    scope: CoroutineScope? = null
) {
    if (bonAchat == null) return

    val printFunction = {
        // Get client information
        val client = repositorysModel._3_ClientsDataBase_Repository
            .modelDatasSnapList.find { it.vid == bonAchat.clientAcheteurID }

        // Generate current date string
        val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date())

        // Get all confirmed products in this receipt
        val products = repositorysModel._1_2_ProduitAcheteOperation_Repository
            .modelDatasSnapList
            .filter {
                it.parent_1_3_BonAchat == bonAchat.vid &&
                        it.etateActuellementEst == Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
            }

        // Create a list of articles to print
        val articles = mutableListOf<ArticleImpression>()
        var totalAmount = 0.0

        // Process each product and its colors
        products.forEach { product ->
            // Get product details
            val productDetails = repositorysModel._2_1_ProduitsDataBase_Repository
                .modelDatasSnapList
                .find { it.vid == product.produitAcheterID }

            // Get colors with quantity > 0
            val colors = repositorysModel._1_1_CouleurAcheteOperation_Repository
                .modelDatasSnapList
                .filter {
                    it.parentProduitAchateOperationVID == product.vid &&
                            it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI &&
                            it.totaleQuantity > 0
                }

            // Process each color
            colors.forEach { color ->

                // Calculate price and subtotal
                val prixUnitaire = productDetails?.monPrixVent ?: 0.0
                val quantite = color.totaleQuantity
                val subtotal = prixUnitaire * quantite

                totalAmount += subtotal

                // Format article name with color
                val nomArticleComplet =productDetails?.nom ?: "Produit"

                articles.add(
                    ArticleImpression(
                        nomArticle = nomArticleComplet,
                        quantite = quantite,
                        prixUnitaire = prixUnitaire
                    )
                )
            }
        }

        // Get client credit balance
        val creditBalance = client?.currentCreditBalance ?: 0.0

        // Prepare text for printing
        val (texteImprimable, totalBon) = prepareTexteToPrint(
            nomClient = client?.nom ?: "Client",
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
        append("<SMALL><BOLD>    Quantité      Prix         <NORMAL>Sous-total<BR>")
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
