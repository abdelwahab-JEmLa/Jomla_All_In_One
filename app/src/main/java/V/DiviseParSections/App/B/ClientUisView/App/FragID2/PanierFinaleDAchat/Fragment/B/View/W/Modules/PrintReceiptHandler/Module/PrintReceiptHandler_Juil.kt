package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrintReceiptHandler_Juil() {
    private val PRINT_INTENT = "pe.diegoveloper.printing"
    private val TAG = "PrintReceiptHandler_Juil"

    data class ArticleImpression(
        val nomArticle: String,
        val quantite: Int,
        val prixUnitaire: Double,
        val couleur: String? = null
    )

    fun print_Credit(
        context: Context,
        client: M2Client?,
        bonVent: M8BonVent,
        scope: CoroutineScope? = null
    ) {
        val printFunction = {
            val dateString =
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            val clientName = client?.nom?.takeIf { it.isNotBlank() } ?: "Client"
            val totalAmount = bonVent.sum_De_Totale_Vents
            val paymentAmount = bonVent.versement
            val remainingAmount = totalAmount - paymentAmount
            val transactionId = bonVent.keyID.takeLast(4)

            val texteImprimable = StringBuilder().apply {
                append("<BIG><CENTER>Abdelwahab<BR>")
                append("<BIG><CENTER>JeMla.Com<BR>")
                append("<SMALL><CENTER>0553885037<BR>")
                append("<SMALL><CENTER>إيصال دفع - Credit Payment<BR>")
                append("<BR>")
                append("<SMALL><CENTER>$clientName                        $dateString<BR>")
                append("<SMALL><CENTER>رقم المعاملة: $transactionId<BR>")
                append("<BR>")
                append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
                append("<MEDIUM1><LEFT>تفاصيل الدفع - Payment Details<BR>")
                append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
                append("<BR>")

                // Total amount
                append("<MEDIUM1><LEFT>إجمالي المبلغ - Total Amount:<BR>")
                append("<MEDIUM2><RIGHT>${round(totalAmount)}Da<BR>")
                append("<BR>")

                // Payment made
                append("<MEDIUM1><LEFT>المبلغ المدفوع - Amount Paid:<BR>")
                append("<MEDIUM2><RIGHT>${round(paymentAmount)}Da<BR>")
                append("<BR>")

                // Remaining balance
                append("<LEFT><NORMAL><MEDIUM1>---------------------<BR>")
                if (remainingAmount > 0) {
                    append("<MEDIUM1><LEFT>المتبقي - Remaining Balance:<BR>")
                    append("<MEDIUM3><RIGHT><BOLD>${round(remainingAmount)}Da<BR>")
                } else if (remainingAmount < 0) {
                    append("<MEDIUM1><LEFT>فائض الدفع - Overpayment:<BR>")
                    append("<MEDIUM3><RIGHT><BOLD>${round(kotlin.math.abs(remainingAmount))}Da<BR>")
                } else {
                    append("<MEDIUM2><CENTER><BOLD>مدفوع بالكامل - PAID IN FULL<BR>")
                }

                append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
                append("<BR>")
                append("<SMALL><CENTER>شكراً لك - Thank You<BR>")
                append("<BR><BR><BR>>")
            }

            // Log the credit receipt content
            logCreditReceiptContent(
                clientName = clientName,
                dateString = dateString,
                transactionId = transactionId,
                totalAmount = totalAmount,
                paymentAmount = paymentAmount,
                remainingAmount = remainingAmount,
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

    fun printVentReceipt(
        context: Context,
        repoM1Produit: RepoM1Produit,
        repo3CouleurProduitInfos: Repo03CouleurProduitInfos,
        client: M2Client?,
        scope: CoroutineScope? = null,
        relative_ListM10OperationVentCouleur: List<M10OperationVentCouleur>,
        repo13TarificationInfos: Repo13TarificationInfos
    ) {
        val printFunction = {
            val dateString =
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            val productMap = mutableMapOf<String, MutableList<ArticleImpression>>()

            relative_ListM10OperationVentCouleur.forEach { vent ->
                val product = repoM1Produit.datasValue.find {
                    it.keyID == vent.parent_M1Produit_KeyId
                }

                val colorInfo = repo3CouleurProduitInfos.datasValue.find {
                    it.keyID == vent.parent_M3CouleurProduit_KeyID
                }

                val productName = product?.nom?.takeIf { it.isNotBlank() }
                    ?: product?.nomMutable?.takeIf { it.isNotBlank() }
                    ?: "Produit #${vent.parent_M1Produit_KeyId}"

                val colorName = colorInfo?.nomCouleurStrSiSonImageDispo ?: "Couleur standard"
                val articleName = if (colorName != "Couleur standard") {
                    "$productName ($colorName)"
                } else {
                    productName
                }

                val article = ArticleImpression(
                    nomArticle = articleName,
                    quantite = vent.quantity,
                    prixUnitaire = vent.provisoireMonPrix,
                    couleur = colorName
                )

                if (productMap.containsKey(productName)) {
                    productMap[productName]?.add(article)
                } else {
                    productMap[productName] = mutableListOf(article)
                }
            }

            val finalArticles = mutableListOf<ArticleImpression>()
            productMap.values.forEach { articles ->
                articles.groupBy { "${it.nomArticle}_${it.prixUnitaire}" }
                    .forEach { (_, groupedArticles) ->
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
                relative_ListM10OperationVentCouleur,
                clientName,
                dateString,
                creditBalance,
                repo13TarificationInfos,
                repoM1Produit
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
        relative_ListM10OperationVentCouleur: List<M10OperationVentCouleur>,
        nomClient: String,
        dateString: String,
        ancienCredits: Double,
        repo13TarificationInfos: Repo13TarificationInfos,
        repoM1Produit: RepoM1Produit
    ): Pair<StringBuilder, Double> {
        val groupe_Produit =
            relative_ListM10OperationVentCouleur.groupBy { it.parent_M1Produit_KeyId }.toList()

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
            append("<SMALL><BOLD>   Quantité      Tariff        <NORMAL>Sous-total<BR>")
            append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
        }

        groupe_Produit.forEachIndexed { index, produit_vent ->
            val datas_repo13TarificationInfos =
                repo13TarificationInfos.datasValue
            val standart_Vent = produit_vent.second.first()

            val relative_Tariffication =
                datas_repo13TarificationInfos.find { it.keyID == standart_Vent.parentM13TarificationKeyID }

            val relative_M1Produit =
                repoM1Produit.datasValue
                    .find { it.keyID == produit_vent.first }
            val quantite_Boit_Par_Carton = relative_M1Produit?.quantite_Boit_Par_Carton ?: 1

            val vent_quantity = produit_vent.second.sumOf { it.quantity }

            // Format quantity display based on carton packaging
            val quantityDisplay = formatQuantityDisplay(vent_quantity, quantite_Boit_Par_Carton)

            val vent_prix = relative_Tariffication!!.prixCurrency

            val subtotal = vent_prix * vent_quantity

            if (subtotal != 0.0) {
                texteImprimable.apply {
                    append("<MEDIUM1><LEFT>${relative_M1Produit?.nom}<BR>")
                    append(" <MEDIUM1><LEFT>$quantityDisplay ")
                    append("<MEDIUM1><LEFT>${vent_prix}Da ")
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
     * Formats quantity display based on carton packaging
     * Examples:
     * - If quantity = 12 and carton = 6 → "2x6(12)"
     * - If quantity = 5 and carton = 6 → "5" (normal display)
     * - If quantity = 18 and carton = 6 → "3x6(18)"
     */
    private fun formatQuantityDisplay(quantity: Int, quantiteBoitParCarton: Int): String {
        return if (quantiteBoitParCarton in 2..quantity && quantity % quantiteBoitParCarton == 0) {
            val cartons = quantity / quantiteBoitParCarton
            "${cartons}x${quantiteBoitParCarton}(${quantity})"
        } else {
            quantity.toString()
        }
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

    /**
     * Logs the credit receipt content for debugging and monitoring purposes
     */
    private fun logCreditReceiptContent(
        clientName: String,
        dateString: String,
        transactionId: String,
        totalAmount: Double,
        paymentAmount: Double,
        remainingAmount: Double,
        receiptText: String
    ) {
        Log.d(TAG, "=== CREDIT RECEIPT PRINT LOG ===")
        Log.d(TAG, "Client: $clientName")
        Log.d(TAG, "Date: $dateString")
        Log.d(TAG, "Transaction ID: $transactionId")
        Log.d(TAG, "Total Amount: ${round(totalAmount)}Da")
        Log.d(TAG, "Payment Amount: ${round(paymentAmount)}Da")
        Log.d(TAG, "Remaining Amount: ${round(remainingAmount)}Da")

        Log.d(TAG, "=== RAW CREDIT RECEIPT TEXT ===")
        // Clean the receipt text for better readability in logs
        val cleanedText = receiptText
            .replace("<BIG>", "")
            .replace("<CENTER>", "")
            .replace("<SMALL>", "")
            .replace("<LEFT>", "")
            .replace("<RIGHT>", "")
            .replace("<NORMAL>", "")
            .replace("<MEDIUM1>", "")
            .replace("<MEDIUM2>", "")
            .replace("<MEDIUM3>", "")
            .replace("<BOLD>", "")
            .replace("<BR>", "\n")

        Log.d(TAG, cleanedText)
        Log.d(TAG, "=== END CREDIT RECEIPT LOG ===")
    }

    private fun round(value: Double): Double = kotlin.math.round(value * 10) / 10
}
