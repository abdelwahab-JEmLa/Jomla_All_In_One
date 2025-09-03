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
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrintReceiptHandler_Juil(
    private val printInPdfHandler: PrintInPdf_itextpdf_Handler,
) {
    private val PRINT_INTENT = "pe.diegoveloper.printing"
    private val TAG = "PrintReceiptHandler"

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
        Log.d(TAG, "=== STARTING PRINT VENT RECEIPT ===")
        Log.d(TAG, "generatePdf: $generatePdf")
        Log.d(TAG, "operations count: ${relative_ListM10OperationVentCouleur.size}")
        Log.d(TAG, "client: ${client?.nom}")

        val printFunction = {
            val transactionId = "vent_${System.currentTimeMillis().toString().takeLast(4)}"
            val isBluetoothAvailable = isBluetoothAvailable()

            Log.d(TAG, "Transaction ID: $transactionId")
            Log.d(TAG, "Bluetooth available: $isBluetoothAvailable")

            // Print via Bluetooth if available
            if (isBluetoothAvailable) {
                Log.d(TAG, "Preparing Bluetooth text...")
                try {
                    val (texteImprimable, _) = prepareTexteToPrint(
                        relative_ListM10OperationVentCouleur,
                        client?.nom?.takeIf { it.isNotBlank() } ?: "Client",
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
                        client?.currentCreditBalance ?: 0.0,
                        repo13TarificationInfos,
                        repoM1Produit
                    )
                    Log.d(TAG, "Bluetooth text prepared successfully")
                    handleBluetoothPrint(context, texteImprimable.toString())
                    Log.d(TAG, "Bluetooth print completed")
                } catch (e: Exception) {
                    Log.e(TAG, "Error preparing Bluetooth text", e)
                }
            } else {
                Log.d(TAG, "Bluetooth offline - PDF only mode")
                Toast.makeText(context, "Bluetooth hors ligne - Impression PDF uniquement", Toast.LENGTH_LONG).show()
            }

            // Generate PDF using direct data objects (no text parsing)
            if (generatePdf || !isBluetoothAvailable) {
                Log.d(TAG, "Starting PDF generation process...")
                Log.d(TAG, "Scope is null: ${scope == null}")

                scope?.launch {
                    Log.d(TAG, "Inside coroutine scope - starting PDF generation...")

                    try {
                        // Validate input data before calling PDF generation
                        Log.d(TAG, "Validating input data...")
                        Log.d(TAG, "Operations list size: ${relative_ListM10OperationVentCouleur.size}")
                        Log.d(TAG, "Tarification repo data size: ${repo13TarificationInfos.datasValue.size}")
                        Log.d(TAG, "Produit repo data size: ${repoM1Produit.datasValue.size}")

                        // Check if operations have valid data
                        relative_ListM10OperationVentCouleur.forEachIndexed { index, operation ->
                            Log.d(TAG, "Operation $index: produitKeyId=${operation.parent_M1Produit_KeyId}, " +
                                    "tarificationKeyId=${operation.parentM13TarificationKeyID}, quantity=${operation.quantity}")
                        }

                        // Check repositories
                        if (repo13TarificationInfos.datasValue.isEmpty()) {
                            Log.w(TAG, "WARNING: Tarification repository is empty!")
                        }
                        if (repoM1Produit.datasValue.isEmpty()) {
                            Log.w(TAG, "WARNING: Produit repository is empty!")
                        }

                        Log.d(TAG, "Calling printInPdfHandler.generateVentReceiptPdf...")

                        val result = printInPdfHandler.generateVentReceiptPdf(
                            context,
                            client,
                            relative_ListM10OperationVentCouleur,
                            repo13TarificationInfos,
                            repoM1Produit,
                            transactionId
                        )

                        Log.d(TAG, "PDF generation call completed")

                        result.onSuccess { message ->
                            Log.d(TAG, "PDF generation SUCCESS")
                            Log.d(TAG, "Success message: $message")

                            try {
                                // Extract file path from success message
                                val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
                                Log.d(TAG, "Extracted file path: $filePath")

                                val pdfFile = File(filePath)
                                Log.d(TAG, "PDF file exists: ${pdfFile.exists()}")
                                Log.d(TAG, "PDF file size: ${if (pdfFile.exists()) pdfFile.length() else 0} bytes")

                                if (pdfFile.exists()) {
                                    // Save to downloads for easy access
                                    Log.d(TAG, "Saving PDF to downloads...")
                                    val downloadedFile = savePdfToDownloads(context, pdfFile)
                                    Log.d(TAG, "Downloaded file: ${downloadedFile?.absolutePath}")

                                    Toast.makeText(context, "PDF généré avec succès", Toast.LENGTH_SHORT).show()

                                    // Open PDF automatically
                                    Log.d(TAG, "Opening PDF file...")
                                    openPdfFile(context, pdfFile)

                                    // Show additional info about saved location
                                    if (downloadedFile != null) {
                                        Toast.makeText(context, "PDF sauvegardé dans Téléchargements", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    Log.e(TAG, "PDF file does not exist after generation!")
                                    Toast.makeText(context, "Erreur: Fichier PDF non trouvé", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error processing successful PDF result", e)
                                Toast.makeText(context, "Erreur lors du traitement du PDF", Toast.LENGTH_SHORT).show()
                            }

                        }.onFailure { error ->
                            Log.e(TAG, "PDF generation FAILURE")
                            Log.e(TAG, "Error type: ${error.javaClass.simpleName}")
                            Log.e(TAG, "Error message: ${error.message}")
                            Log.e(TAG, "Error cause: ${error.cause}")
                            Log.e(TAG, "Full stack trace:", error)

                            // Show more specific error message
                            val errorMessage = when {
                                error.message?.contains("font", ignoreCase = true) == true ->
                                    "Erreur de police PDF"
                                error.message?.contains("permission", ignoreCase = true) == true ->
                                    "Erreur de permission fichier"
                                error.message?.contains("storage", ignoreCase = true) == true ->
                                    "Erreur de stockage"
                                error.message?.contains("firebase", ignoreCase = true) == true ->
                                    "Erreur de téléchargement Firebase"
                                else -> "Erreur lors de la génération PDF: ${error.message}"
                            }

                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "EXCEPTION in PDF generation process", e)
                        Log.e(TAG, "Exception type: ${e.javaClass.simpleName}")
                        Log.e(TAG, "Exception message: ${e.message}")
                        Log.e(TAG, "Exception cause: ${e.cause}")
                        Log.e(TAG, "Full exception stack trace:", e)
                        Toast.makeText(context, "Erreur critique PDF: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } ?: run {
                    Log.e(TAG, "ERROR: CoroutineScope is null - cannot generate PDF")
                    Toast.makeText(context, "Erreur: Scope manquant pour PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (scope != null) {
            Log.d(TAG, "Launching print function in provided scope...")
            scope.launch {
                try {
                    printFunction()
                    Log.d(TAG, "Print function completed successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error in print function", e)
                }
            }
        } else {
            Log.d(TAG, "Executing print function directly (no scope)...")
            try {
                printFunction()
                Log.d(TAG, "Print function completed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error in direct print function execution", e)
            }
        }

        Log.d(TAG, "=== PRINT VENT RECEIPT COMPLETED ===")
    }

    /**
     * Format quantity display for both Bluetooth and PDF - FIXED VERSION
     */
    private fun formatQuantityDisplay(quantity: Int, quantiteBoitParCarton: Int): String {
        return if (quantiteBoitParCarton in 2..quantity && quantity % quantiteBoitParCarton == 0) {
            val cartons = quantity / quantiteBoitParCarton
            "$cartons X $quantiteBoitParCarton"
        } else {
            quantity.toString()
        }
    }

    fun print_Credit(
        context: Context,
        client: M2Client?,
        bonVent: M8BonVent,
        scope: CoroutineScope? = null,
        generatePdf: Boolean = false,
        previousPayments: List<Double> = emptyList(),
        showPaymentHistory: Boolean = false
    ) {
        Log.d(TAG, "=== STARTING PRINT CREDIT ===")
        Log.d(TAG, "generatePdf: $generatePdf")
        Log.d(TAG, "client: ${client?.nom}")
        Log.d(TAG, "bonVent sum: ${bonVent.sum_De_Totale_Vents}")
        Log.d(TAG, "bonVent versement: ${bonVent.versement}")
        Log.d(TAG, "showPaymentHistory: $showPaymentHistory")
        Log.d(TAG, "previousPayments: $previousPayments")

        val printFunction = {
            val transactionId = bonVent.keyID.takeLast(4)
            val isBluetoothAvailable = isBluetoothAvailable()

            Log.d(TAG, "Transaction ID: $transactionId")
            Log.d(TAG, "Bluetooth available: $isBluetoothAvailable")

            // Print via Bluetooth if available
            if (isBluetoothAvailable) {
                Log.d(TAG, "Preparing Bluetooth credit text...")
                try {
                    val bluetoothText = prepareCreditBluetoothText(
                        client, bonVent, previousPayments, showPaymentHistory, transactionId
                    )
                    Log.d(TAG, "Bluetooth credit text prepared successfully")
                    handleBluetoothPrint(context, bluetoothText)
                    Log.d(TAG, "Bluetooth credit print completed")
                } catch (e: Exception) {
                    Log.e(TAG, "Error preparing Bluetooth credit text", e)
                }
            } else {
                Log.d(TAG, "Bluetooth offline - PDF only mode")
                Toast.makeText(context, "Bluetooth hors ligne - Impression PDF uniquement", Toast.LENGTH_LONG).show()
            }

            // Generate PDF if requested OR if Bluetooth is offline
            if (generatePdf || !isBluetoothAvailable) {
                Log.d(TAG, "Starting credit PDF generation process...")
                Log.d(TAG, "Scope is null: ${scope == null}")

                scope?.launch {
                    Log.d(TAG, "Inside coroutine scope - starting credit PDF generation...")

                    try {
                        Log.d(TAG, "Creating credit receipt data...")
                        val creditData = CreditReceiptData(
                            client = client,
                            totalAmount = bonVent.sum_De_Totale_Vents,
                            currentPayment = bonVent.versement,
                            previousPayments = previousPayments,
                            transactionId = transactionId,
                            showPaymentHistory = showPaymentHistory
                        )

                        Log.d(TAG, "Credit data created - calling generateCreditReceiptPdf...")
                        Log.d(TAG, "Credit data: totalAmount=${creditData.totalAmount}, currentPayment=${creditData.currentPayment}")

                        val result = printInPdfHandler.generateCreditReceiptPdf(context, creditData)
                        Log.d(TAG, "Credit PDF generation call completed")

                        result.onSuccess { message ->
                            Log.d(TAG, "Credit PDF generation SUCCESS")
                            Log.d(TAG, "Success message: $message")

                            try {
                                // Extract file path from success message
                                val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
                                Log.d(TAG, "Extracted credit file path: $filePath")

                                val pdfFile = File(filePath)
                                Log.d(TAG, "Credit PDF file exists: ${pdfFile.exists()}")
                                Log.d(TAG, "Credit PDF file size: ${if (pdfFile.exists()) pdfFile.length() else 0} bytes")

                                if (pdfFile.exists()) {
                                    // Save to downloads for easy access
                                    Log.d(TAG, "Saving credit PDF to downloads...")
                                    savePdfToDownloads(context, pdfFile)

                                    Toast.makeText(context, "PDF généré avec succès", Toast.LENGTH_SHORT).show()

                                    // Open PDF automatically
                                    Log.d(TAG, "Opening credit PDF file...")
                                    openPdfFile(context, pdfFile)
                                } else {
                                    Log.e(TAG, "Credit PDF file does not exist after generation!")
                                    Toast.makeText(context, "Erreur: Fichier PDF non trouvé", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error processing successful credit PDF result", e)
                                Toast.makeText(context, "Erreur lors du traitement du PDF", Toast.LENGTH_SHORT).show()
                            }

                        }.onFailure { error ->
                            Log.e(TAG, "Credit PDF generation FAILURE")
                            Log.e(TAG, "Error type: ${error.javaClass.simpleName}")
                            Log.e(TAG, "Error message: ${error.message}")
                            Log.e(TAG, "Error cause: ${error.cause}")
                            Log.e(TAG, "Full stack trace:", error)

                            val errorMessage = when {
                                error.message?.contains("font", ignoreCase = true) == true ->
                                    "Erreur de police PDF"
                                error.message?.contains("permission", ignoreCase = true) == true ->
                                    "Erreur de permission fichier"
                                error.message?.contains("Invalid total amount", ignoreCase = true) == true ->
                                    "Montant invalide: ${bonVent.sum_De_Totale_Vents}"
                                else -> "Erreur lors de la génération PDF: ${error.message}"
                            }

                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "EXCEPTION in credit PDF generation process", e)
                        Log.e(TAG, "Exception type: ${e.javaClass.simpleName}")
                        Log.e(TAG, "Exception message: ${e.message}")
                        Toast.makeText(context, "Erreur critique PDF: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } ?: run {
                    Log.e(TAG, "ERROR: CoroutineScope is null - cannot generate credit PDF")
                    Toast.makeText(context, "Erreur: Scope manquant pour PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (scope != null) {
            Log.d(TAG, "Launching credit print function in provided scope...")
            scope.launch {
                try {
                    printFunction()
                    Log.d(TAG, "Credit print function completed successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error in credit print function", e)
                }
            }
        } else {
            Log.d(TAG, "Executing credit print function directly (no scope)...")
            try {
                printFunction()
                Log.d(TAG, "Credit print function completed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error in direct credit print function execution", e)
            }
        }

        Log.d(TAG, "=== PRINT CREDIT COMPLETED ===")
    }

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
     * Open PDF file using system PDF viewer or file manager
     */
    private fun openPdfFile(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Fallback: open with file manager
                val fileIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.fromFile(file), "*/*")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fileIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening PDF: ${e.message}")
            Toast.makeText(context, "Impossible d'ouvrir le PDF", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Save PDF to local Downloads folder for easy access
     */
    private fun savePdfToDownloads(context: Context, sourceFile: File): File? {
        return try {
            val downloadsDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
                ?: return null

            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            val destFile = File(downloadsDir, sourceFile.name)
            sourceFile.copyTo(destFile, overwrite = true)
            destFile
        } catch (e: Exception) {
            Log.e(TAG, "Error saving PDF to downloads: ${e.message}")
            null
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
     * Round value to 1 decimal place
     */
    private fun round(value: Double): Double = kotlin.math.round(value * 10) / 10.0
}
