package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrintReceiptHandler_Juil(
    private val printInPdfHandler: PrintInPdf_itextpdf_Handler,
) {
    private val PRINT_INTENT = "pe.diegoveloper.printing"
    private val TAG = "PrintReceiptHandler"

    data class ArticleImpression(
        val nomArticle: String,
        val quantite: Int,
        val prixUnitaire: Double,
        val couleur: String? = null
    )

    /**
     * Check if Bluetooth is available and enabled
     */
    private fun isBluetoothAvailable(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled
    }

    /**
     * Handle printing with Bluetooth check - for text-based printing only
     * (Used for Bluetooth thermal printer compatibility)
     */
    private fun handleBluetoothPrint(context: Context, texteImprimable: String) {
        val isBluetoothAvailable = isBluetoothAvailable()

        if (isBluetoothAvailable) {
            val intent = Intent(PRINT_INTENT).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, texteImprimable)
            }
            ContextCompat.startActivity(context, intent, null)
        } else {
            Toast.makeText(context, "Bluetooth hors ligne", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Print credit receipt with optional PDF generation using direct data objects
     */
    fun print_Credit(
        context: Context,
        client: M2Client?,
        bonVent: M8BonVent,
        scope: CoroutineScope? = null,
        generatePdf: Boolean = false,
        previousPayments: List<Double> = emptyList(),
        showPaymentHistory: Boolean = false
    ) {
        val printFunction = {
            val transactionId = bonVent.keyID.takeLast(4)
            val isBluetoothAvailable = isBluetoothAvailable()

            // Print via Bluetooth if available (using formatted text for thermal printer compatibility)
            if (isBluetoothAvailable) {
                val bluetoothText = prepareCreditBluetoothText(
                    client, bonVent, previousPayments, showPaymentHistory, transactionId
                )
                handleBluetoothPrint(context, bluetoothText)
            } else {
                Toast.makeText(context, "Bluetooth hors ligne - Impression PDF uniquement", Toast.LENGTH_LONG).show()
            }

            // Generate PDF if requested OR if Bluetooth is offline
            if (generatePdf || !isBluetoothAvailable) {
                scope?.launch {
                    try {
                        val creditData = CreditReceiptData(
                            client = client,
                            totalAmount = bonVent.sum_De_Totale_Vents,
                            currentPayment = bonVent.versement,
                            previousPayments = previousPayments,
                            transactionId = transactionId,
                            showPaymentHistory = showPaymentHistory
                        )

                        val result = printInPdfHandler.generateCreditReceiptPdf(context, creditData)
                        result.onSuccess { message ->
                            Log.d(TAG, "PDF generated successfully: $message")
                            Toast.makeText(context, "PDF généré avec succès", Toast.LENGTH_SHORT).show()
                        }.onFailure { error ->
                            Log.e(TAG, "Failed to generate PDF: ${error.message}")
                            Toast.makeText(context, "Erreur lors de la génération PDF", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error generating PDF: ${e.message}")
                        Toast.makeText(context, "Erreur lors de la génération PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        if (scope != null) {
            scope.launch { printFunction() }
        } else {
            printFunction()
        }
    }

    /**
     * Print vent receipt using direct PDF generation from data objects
     */
    fun printVentReceiptWithDirectPdf(
        context: Context,
        repoM1Produit: RepoM1Produit,
        repo3CouleurProduitInfos: Repo03CouleurProduitInfos,
        client: M2Client?,
        scope: CoroutineScope? = null,
        relative_ListM10OperationVentCouleur: List<M10OperationVentCouleur>,
        repo13TarificationInfos: Repo13TarificationInfos,
        generatePdf: Boolean = false
    ) {
        val printFunction = {
            val transactionId = "vent_${System.currentTimeMillis().toString().takeLast(4)}"
            val isBluetoothAvailable = isBluetoothAvailable()

            // Print via Bluetooth if available (using formatted text for thermal printer compatibility)
            if (isBluetoothAvailable) {
                val (texteImprimable, _) = prepareTexteToPrint(
                    relative_ListM10OperationVentCouleur,
                    client?.nom?.takeIf { it.isNotBlank() } ?: "Client",
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
                    client?.currentCreditBalance ?: 0.0,
                    repo13TarificationInfos,
                    repoM1Produit
                )

                handleBluetoothPrint(context, texteImprimable.toString())
            } else {
                Toast.makeText(context, "Bluetooth hors ligne - Impression PDF uniquement", Toast.LENGTH_LONG).show()
            }

            // Generate PDF using direct data objects (no text parsing)
            if (generatePdf || !isBluetoothAvailable) {
                scope?.launch {
                    try {
                        val result = printInPdfHandler.generateVentReceiptPdf(
                            context,
                            client,
                            relative_ListM10OperationVentCouleur,
                            repo13TarificationInfos,
                            repoM1Produit,
                            transactionId
                        )
                        result.onSuccess { message ->
                            Log.d(TAG, "PDF generated successfully: $message")
                            Toast.makeText(context, "PDF généré avec succès", Toast.LENGTH_SHORT).show()
                        }.onFailure { error ->
                            Log.e(TAG, "Failed to generate PDF: ${error.message}")
                            Toast.makeText(context, "Erreur lors de la génération PDF", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error generating PDF: ${e.message}")
                        Toast.makeText(context, "Erreur lors de la génération PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        if (scope != null) {
            scope.launch { printFunction() }
        } else {
            printFunction()
        }
    }

    /**
     * Prepare credit receipt text for Bluetooth thermal printer
     * (Kept for thermal printer compatibility - uses formatting tags)
     */
    private fun prepareCreditBluetoothText(
        client: M2Client?,
        bonVent: M8BonVent,
        previousPayments: List<Double>,
        showPaymentHistory: Boolean,
        transactionId: String
    ): String {
        val dateString = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val clientName = client?.nom?.takeIf { it.isNotBlank() } ?: "Client"
        val totalAmount = bonVent.sum_De_Totale_Vents
        val currentPayment = bonVent.versement
        val totalPaid = if (showPaymentHistory) previousPayments.sum() + currentPayment else currentPayment
        val remainingAmount = totalAmount - totalPaid

        return StringBuilder().apply {
            append("<BIG><CENTER>Abdelwahab<BR>")
            append("<BIG><CENTER>JeMla.Com<BR>")
            append("<SMALL><CENTER>0553885037<BR>")
            append("<SMALL><CENTER> - Credit Payment${if (showPaymentHistory) " Prix_Detaille" else ""}<BR>")
            append("<BR>")
            append("<SMALL><CENTER>$clientName                        $dateString<BR>")
            append("<BR>")
            append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
            append("<BR>")

            if (showPaymentHistory) {
                append("<SMALL><LEFT>Transaction: #$transactionId<BR>")
                append("<BR>")
            }

            append("<MEDIUM1><LEFT>${if (showPaymentHistory) "Montant Total" else "Total a Payer"} :<BR>")
            append("<MEDIUM2><CENTER>${round(totalAmount)}Da<BR>")
            append("<BR>")

            if (showPaymentHistory && previousPayments.isNotEmpty()) {
                append("<SMALL><LEFT>Paiements Precedents:<BR>")
                previousPayments.forEachIndexed { index, payment ->
                    append("<SMALL><LEFT>  ${index + 1}. ${round(payment)}Da<BR>")
                }
                append("<SMALL><LEFT>Sous-total: ${round(previousPayments.sum())}Da<BR>")
                append("<BR>")
                append("<MEDIUM1><LEFT>Paiement Actuel:<BR>")
            } else {
                append("<MEDIUM1><LEFT>Versement Effectue:<BR>")
            }

            append("<MEDIUM2><CENTER>${round(currentPayment)}Da<BR>")
            append("<BR>")

            if (showPaymentHistory) {
                append("<SMALL><LEFT>Total Paye: ${round(totalPaid)}Da<BR>")
                append("<BR>")
            }

            append("<LEFT><NORMAL><MEDIUM1>---------------------<BR>")
            when {
                remainingAmount > 0 -> {
                    append("<MEDIUM1><LEFT> ${if (showPaymentHistory) "Reste a Payer" else "Credit Restant"} :<BR>")
                    append("<MEDIUM3><RIGHT><BOLD>${round(remainingAmount)}Da<BR>")
                }
                remainingAmount < 0 -> {
                    append("<MEDIUM1><LEFT> ${if (showPaymentHistory) "Trop Paye" else "Surplus Paye"} :<BR>")
                    append("<MEDIUM3><RIGHT><BOLD>${round(-remainingAmount)}Da<BR>")
                }
                else -> {
                    if (showPaymentHistory) {
                        append("<MEDIUM1><CENTER> ✓ PAYE COMPLETEMENT ✓<BR>")
                        append("<MEDIUM2><CENTER>Merci pour votre confiance<BR>")
                    } else {
                        append("<MEDIUM1><CENTER> PAYE INTEGRALEMENT<BR>")
                        append("<MEDIUM2><CENTER> ✓ SOLDE<BR>")
                    }
                }
            }

            if (!showPaymentHistory) {
                append("<BR>")
                append("<SMALL><CENTER>Transaction: #$transactionId<BR>")
            }

            append("<BR><BR><BR>>")
        }.toString()
    }

    /**
     * Prepare sales receipt text for Bluetooth thermal printer
     * (Kept for thermal printer compatibility - uses formatting tags)
     */
    private fun prepareTexteToPrint(
        relative_ListM10OperationVentCouleur: List<M10OperationVentCouleur>,
        nomClient: String,
        dateString: String,
        ancienCredits: Double,
        repo13TarificationInfos: Repo13TarificationInfos,
        repoM1Produit: RepoM1Produit
    ): Pair<StringBuilder, Double> {
        val groupe_Produit = relative_ListM10OperationVentCouleur.groupBy { it.parent_M1Produit_KeyId }.toList()
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
            val datas_repo13TarificationInfos = repo13TarificationInfos.datasValue
            val standart_Vent = produit_vent.second.first()
            val relative_Tariffication = datas_repo13TarificationInfos.find { it.keyID == standart_Vent.parentM13TarificationKeyID }
            val relative_M1Produit = repoM1Produit.datasValue.find { it.keyID == produit_vent.first }
            val quantite_Boit_Par_Carton = relative_M1Produit?.quantite_Boit_Par_Carton ?: 1
            val vent_quantity = produit_vent.second.sumOf { it.quantity }
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
     * Format quantity display for both Bluetooth and PDF
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
     * Round value to 1 decimal place
     */
    private fun round(value: Double): Double = kotlin.math.round(value * 10) / 10.0
}
