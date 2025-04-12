package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.PanieAchates.APP.ViewModel

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchat
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
import android.content.Context
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
// SOLUTION FOR imprission.kt

// 1. Rename the conflicting function to fix the overload error
// Change from:
// fun generateArticlesList(repositorysModel: _0_0_HeadOfRepositorys_Model, clientId: Long?, totalPrice: String): List<ArticleImpression>
// To:
fun generateArticlesForPrinting(
    repositorysModel: _0_0_HeadOfRepositorys_Model,
    clientId: Long?,
    totalPrice: String
): List<ArticleImpression> {
    val articlesList = mutableListOf<ArticleImpression>()

    // Get all products for this client/receipt
    val products = repositorysModel._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
        .filter { it.parent_1_3_BonAchat == clientId }

    products.forEach { product ->
        // Get product details
        val productDetails = repositorysModel._2_1_ProduitsDataBase_Repository.modelDatasSnapList
            .find { it.vid == product.produitAcheterID }

        // Get colors for this product
        val colors = repositorysModel._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
            .filter { it.parentProduitAchateOperationVID == product.vid }

        // For each color with quantity, create an article
        colors.forEach { color ->
            if (color.totaleQuantity > 0) {
                val colorIndex = color.couleurIndex_ParentVID
                // Get color name using the color index
                val colorName = getColorNameFromIndex(colorIndex)

                val article = ArticleImpression(
                    nomArticle = productDetails?.nom ?: "Produit",
                    quantite = color.totaleQuantity,
                    prixUnitaire = productDetails?.monPrixVent ?: 0.0,
                    couleur = colorName
                )
                articlesList.add(article)
            }
        }
    }

    return articlesList
}

// 2. Fix for: "TODO(1): refactore ce code pour que ca lence le print"
// The refactored function to launch printing directly

/**
 * Function to launch printing for a receipt
 */
fun lencePrint(
    context: Context,
    bonAchat: _1_3_BonAchat?,
    repositorysModel: _0_0_HeadOfRepositorys_Model,
    database: AppDatabase
) {
    if (bonAchat == null) return

    // Get client information
    val client = repositorysModel._3_ClientsDataBase_Repository
        .modelDatasSnapList.find { it.vid == bonAchat.clientAcheteurID }

    // Generate current date string
    val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        .format(Date())

    // Get all products in the current bon achat
    val products = repositorysModel._1_2_ProduitAcheteOperation_Repository
        .modelDatasSnapList
        .filter {
            it.parent_1_3_BonAchat == bonAchat.vid &&
                    it.etateActuellementEst == Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
        }

    // Create a list of articles to print
    val articles = mutableListOf<ArticleImpression>()

    // Total amount calculation
    var totalAmount = 0.0

    // Process each product
    products.forEach { product ->
        // Get the product details
        val productDetails = repositorysModel._2_1_ProduitsDataBase_Repository
            .modelDatasSnapList
            .find { it.vid == product.produitAcheterID }

        // Get all colors for this product with quantity > 0
        val colors = repositorysModel._1_1_CouleurAcheteOperation_Repository
            .modelDatasSnapList
            .filter {
                it.parentProduitAchateOperationVID == product.vid &&
                        it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI &&
                        it.totaleQuantity > 0
            }

        // Get color names from database
        val articlesStats = runCatching {
            database.articlesBasesStatsModelDao().getArticleById(productDetails?.vid?.toInt() ?: 0)
        }.getOrNull()

        // Process each color
        colors.forEach { color ->
            val colorName = when(color.couleurIndex_ParentVID) {
                0L -> articlesStats?.couleur1
                1L -> articlesStats?.couleur2
                2L -> articlesStats?.couleur3
                3L -> articlesStats?.couleur4
                else -> null
            } ?: "Couleur ${color.couleurIndex_ParentVID + 1}"

            // Calculate subtotal for this item
            val prixUnitaire = productDetails?.monPrixVent ?: 0.0
            val quantite = color.totaleQuantity
            val subtotal = prixUnitaire * quantite

            totalAmount += subtotal

            // Create article entry with color information
            val nomArticleComplet = if (colorName.isNotBlank()) {
                "${productDetails?.nom ?: "Produit"} ($colorName)"
            } else {
                productDetails?.nom ?: "Produit"
            }

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

    // Generate the formatted text for printing
    val (texteImprimable, totalBon) = prepareTexteToPrint(
        nomClient = client?.nom ?: "Client",
        dateString = dateString,
        articles = articles,
        ancienCredits = creditBalance
    )

    // Send to printer directly
    imprimerDonnees(context, texteImprimable.toString(), totalBon)
}

// 3. Modified BonAchatInfos.kt to use the renamed function

// Inside BonAchatInfos.kt, change:
// val articles = generateArticlesList(...)
// To:
// val articles = generateArticlesForPrinting(...)
fun handlePrinting(
    context: Context,
    scope: CoroutineScope,
    bonAchat: _1_3_BonAchat?,
    repositorysModel: _0_0_HeadOfRepositorys_Model,
    database: AppDatabase
) {
    if (bonAchat == null) return

    scope.launch {
        // Get client information
        val client = repositorysModel._3_ClientsDataBase_Repository
            .modelDatasSnapList.find { it.vid == bonAchat.clientAcheteurID }

        // Generate current date string
        val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date())

        // Get all products in the current bon achat
        val products = repositorysModel._1_2_ProduitAcheteOperation_Repository
            .modelDatasSnapList
            .filter {
                it.parent_1_3_BonAchat == bonAchat.vid &&
                        it.etateActuellementEst == Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
            }

        // Create a list of articles to print
        val articles = mutableListOf<ArticleImpression>()

        // Total amount calculation
        var totalAmount = 0.0

        // Process each product
        products.forEach { product ->
            // Get the product details
            val productDetails = repositorysModel._2_1_ProduitsDataBase_Repository
                .modelDatasSnapList
                .find { it.vid == product.produitAcheterID }

            // Get all colors for this product with quantity > 0
            val colors = repositorysModel._1_1_CouleurAcheteOperation_Repository
                .modelDatasSnapList
                .filter {
                    it.parentProduitAchateOperationVID == product.vid &&
                            it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI &&
                            it.totaleQuantity > 0
                }

            // Get color names from database
            val articlesStats = runCatching {
                database.articlesBasesStatsModelDao().getArticleById(productDetails?.vid?.toInt() ?: 0)
            }.getOrNull()

            // Process each color
            colors.forEach { color ->
                val colorName = when(color.couleurIndex_ParentVID) {
                    0L -> articlesStats?.couleur1
                    1L -> articlesStats?.couleur2
                    2L -> articlesStats?.couleur3
                    3L -> articlesStats?.couleur4
                    else -> null
                } ?: "Couleur ${color.couleurIndex_ParentVID + 1}"

                // Calculate subtotal for this item
                val prixUnitaire = productDetails?.monPrixVent ?: 0.0
                val quantite = color.totaleQuantity
                val subtotal = prixUnitaire * quantite

                totalAmount += subtotal

                // Create article entry with color information
                val nomArticleComplet = if (colorName.isNotBlank()) {
                    "${productDetails?.nom ?: "Produit"} ($colorName)"
                } else {
                    productDetails?.nom ?: "Produit"
                }

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

        // Generate the formatted text for printing
        val (texteImprimable, totalBon) = prepareTexteToPrint(
            nomClient = client?.nom ?: "Client",
            dateString = dateString,
            articles = articles,
            ancienCredits = creditBalance
        )

        // Send to printer
        imprimerDonnees(context, texteImprimable.toString(), totalBon)

        // Optional: Update the bon achat status or add other post-printing logic here
    }
}

/**
 * Extension function for database to get article stats by ID
 */
suspend fun AppDatabase.articlesBasesStatsModelDao().getArticleById(id: Int): ArticleStatsResult? {
    val allStats = this.getAll()
    return allStats.find {
        try {
            it.javaClass.getMethod("getIdArticle").invoke(it) == id
        } catch (e: Exception) {
            false
        }
    }?.let { article ->
        try {
            ArticleStatsResult(
                id = id,
                couleur1 = article.javaClass.getMethod("getCouleur1").invoke(article) as? String ?: "",
                couleur2 = article.javaClass.getMethod("getCouleur2").invoke(article) as? String ?: "",
                couleur3 = article.javaClass.getMethod("getCouleur3").invoke(article) as? String ?: "",
                couleur4 = article.javaClass.getMethod("getCouleur4").invoke(article) as? String ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Data class to store color information for a product
 */
data class ArticleStatsResult(
    val id: Int,
    val couleur1: String,
    val couleur2: String,
    val couleur3: String,
    val couleur4: String
)
// Constants
const val TAG = "PrintService"
private const val PRINT_INTENT = "pe.diegoveloper.printing"
private const val FACTURES_COLLECTION = "HistoriqueDesFactures"
private const val CLIENTS_COLLECTION = "clientsList"

/**
 * Launches the printing process with provided data
 * @param context Android context
 * @param texteImprimable The text content to print
 * @param totaleBon Total amount for the receipt
 */
fun imprimerDonnees(context: Context, texteImprimable: String, totaleBon: Double) {
    val intent = Intent(PRINT_INTENT).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, texteImprimable)
    }
    ContextCompat.startActivity(context, intent, null)
    Log.d(TAG, "Impression lancée. Total: $totaleBon")
}

/**
 * Data class for printing articles
 */
data class ArticleImpression(
    val nomArticle: String,
    val quantite: Int,
    val prixUnitaire: Double,
    val couleur: String? = null
)

/**
 * Prepares the text content for printing
 * @param nomClient Client name
 * @param dateString Date of the purchase
 * @param articles List of articles with their quantities and prices
 * @param ancienCredits Previous credit balance
 * @return Pair of prepared text and total amount
 */
fun prepareTexteToPrint(
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

/**
 * Generate a list of articles from the repositories data for printing
 */
fun generateArticlesList(
    repositorysModel: _0_0_HeadOfRepositorys_Model,
    clientId: Long?,
    totalPrice: String
): List<ArticleImpression> //->

//TODO(FIXME):Fix erreur Conflicting overloads: public fun generateArticlesList(repositorysModel: _0_0_HeadOfRepositorys_Model, clientId: Long?, totalPrice: String): List<ArticleImpression> defined in Z_CodePartageEntreApps. Proto. B. Sectiones. Fragment. A. AchatsManager. App. B. PanieAchates. APP. ViewModel in file imprission. kt, private fun generateArticlesList(repositorysModel: _0_0_HeadOfRepositorys_Model, clientId: Long?, totalPrice: String): List<[Error type: Unresolved type for ArticlesAcheteModele]> defined in Z_CodePartageEntreApps. Proto. B. Sectiones. Fragment. A. AchatsManager. App. B. PanieAchates. APP. ViewModel in file B_BonAchatInfos. kt{
    val articlesList = mutableListOf<ArticleImpression>()

    // Get all products for this client/receipt
    val products = repositorysModel._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
        .filter { it.parent_1_3_BonAchat == clientId }

    products.forEach { product ->
        // Get product details
        val productDetails = repositorysModel._2_1_ProduitsDataBase_Repository.modelDatasSnapList
            .find { it.vid == product.produitAcheterID }

        // Get colors for this product
        val colors = repositorysModel._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
            .filter { it.parentProduitAchateOperationVID == product.vid }

        // For each color with quantity, create an article
        colors.forEach { color ->
            if (color.totaleQuantity > 0) {
                val colorIndex = color.couleurIndex_ParentVID
                // Get color name using the color index
                val colorName = getColorNameFromIndex(colorIndex)

                val article = ArticleImpression(
                    nomArticle = productDetails?.nom ?: "Produit",
                    quantite = color.totaleQuantity,
                    prixUnitaire = productDetails?.monPrixVent ?: 0.0,
                    couleur = colorName
                )
                articlesList.add(article)
            }
        }
    }

    return articlesList
}

/**
 * Helper function to get color name from index
 */
private fun getColorNameFromIndex(index: Long?): String {
    return when (index) {
        0L -> "Couleur 1"
        1L -> "Couleur 2"
        2L -> "Couleur 3"
        3L -> "Couleur 4"
        else -> "Couleur inconnue"
    }
}
