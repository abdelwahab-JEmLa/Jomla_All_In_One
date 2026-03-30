package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M2Client
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Models.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Handles Bluetooth receipt printing for sales and credit payments.
 * Supports Arabic-to-Latin transliteration for thermal printer compatibility.
 */
class BluetoothPrintHandler {
    private val PRINT_INTENT = "pe.diegoveloper.printing"

    /**
     * Prints a sales receipt via Bluetooth.
     * Client name is automatically transliterated from Arabic to Latin if needed.
     */
    fun printBluetoothReceipt(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        repo13TarificationInfos: Repo13TarificationInfos,
        repoM1Produit: RepoM1Produit,
        bonVent: M8BonVent? = null,
        showCreditSection: Boolean = false,
        versement: Double = 0.0,
        companyHeader: String = "Jomla.com"
    ): Boolean {
        if (!isBluetoothAvailable()) {
            return false
        }

        if (operations.isEmpty()) {
            return false
        }

        return try {
            // Extract and transliterate client name - handles Arabic names properly
            val clientName = getClientDisplayName(client)

            val (texteImprimable, totalCalcule) = prepareTexteToPrint(
                operations,
                clientName,
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
                client?.currentCreditBalance ?: 0.0,
                repo13TarificationInfos,
                repoM1Produit,
                companyHeader
            )

            handleBluetoothPrint(context, texteImprimable.toString())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Prints a credit payment receipt via Bluetooth.
     * Client name is automatically transliterated from Arabic to Latin if needed.
     */
    fun printCreditBluetoothReceipt(
        context: Context,
        client: M2Client?,
        bonVent: M8BonVent,
        previousPayments: List<Double> = emptyList(),
        showPaymentHistory: Boolean = false,
        companyHeader: String = "Jomla.com"
    ): Boolean {
        if (!isBluetoothAvailable()) {
            return false
        }

        return try {
            val bluetoothText = prepareCreditBluetoothText(
                client, bonVent, previousPayments, showPaymentHistory, companyHeader
            )
            handleBluetoothPrint(context, bluetoothText)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Extracts and formats client name for display on receipt.
     * Handles Arabic names by transliterating them to Latin characters.
     * Extracts prefix before dot if present (e.g., "Ahmed.Boutique" -> "Ahmed").
     * Returns "Client" as default if no valid name is found.
     */
    private fun getClientDisplayName(client: M2Client?): String {
        return client?.nom
            ?.takeIf { it.isNotBlank() }
            ?.let { extractClientNamePrefix(it) }
            ?.let { transliterateClientName(it) }
            ?: "Client"
    }

    private fun extractClientNamePrefix(clientName: String): String {
        return clientName.substringBefore(".", clientName).trim()
    }

    private fun isBluetoothAvailable(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled
    }

    private fun handleBluetoothPrint(context: Context, texteImprimable: String) {
        val intent = Intent(PRINT_INTENT).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, texteImprimable)
        }
        ContextCompat.startActivity(context, intent, null)
    }

    /**
     * Transliterates Arabic text to Latin characters for thermal printer compatibility.
     * Maps Arabic characters to their closest Latin equivalents.
     */
    private fun transliterateArabicToLatin(text: String): String {
        val arabicToLatinMap = mapOf(
            'ا' to "a", 'أ' to "a", 'إ' to "i", 'آ' to "aa",
            'ب' to "b", 'ت' to "t", 'ث' to "th", 'ج' to "j",
            'ح' to "h", 'خ' to "kh", 'د' to "d", 'ذ' to "dh",
            'ر' to "r", 'ز' to "z", 'س' to "s", 'ش' to "sh",
            'ص' to "s", 'ض' to "d", 'ط' to "t", 'ظ' to "z",
            'ع' to "a", 'غ' to "gh", 'ف' to "f", 'ق' to "q",
            'ك' to "k", 'ل' to "l", 'م' to "m", 'ن' to "n",
            'ه' to "h", 'و' to "w", 'ي' to "y", 'ى' to "a",
            'ة' to "a", 'ء' to "",
            'ً' to "", 'ٌ' to "", 'ٍ' to "", 'َ' to "", 'ُ' to "",
            'ِ' to "", 'ّ' to "", 'ْ' to "", 'ـ' to ""
        )

        var result = text
        arabicToLatinMap.forEach { (arabic, latin) ->
            result = result.replace(arabic.toString(), latin)
        }

        return result
    }

    /**
     * Processes client name for printing: transliterates Arabic, normalizes whitespace.
     * Returns original name if transliteration results in empty string.
     */
    private fun transliterateClientName(clientName: String): String {
        return transliterateArabicToLatin(clientName)
            .replace(Regex("\\s+"), " ")
            .trim()
            .takeIf { it.isNotBlank() } ?: clientName
    }

    private fun prepareTexteToPrint(
        operations: List<M10OperationVentCouleur>,
        nomClient: String,
        dateString: String,
        ancienCredits: Double,
        repo13TarificationInfos: Repo13TarificationInfos,
        repoM1Produit: RepoM1Produit,
        companyHeader: String
    ): Pair<StringBuilder, Double> {
        val groupe_Produit = operations.groupBy { it.parent_M1Produit_KeyId }.toList()

        val texteImprimable = StringBuilder()
        var totaleBon = 0.0
        var pageCounter = 0
        var itemCount = 0  // FIXED: Track item count

        texteImprimable.apply {
            append("<MEDIUM1><CENTER> Belfort Gros<BR>")
            append("<SMALL><CENTER>0553885037<BR>")
            append("<SMALL><CENTER>Bon Vent<BR>")
            append("<BR>")
            append("<BIG><CENTER>$nomClient")
            append("<BR>")
            append("<SMALL><CENTER>$dateString")
            append("<BR>")
            append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
            append("<SMALL><BOLD>   Quantité      Tariff        <NORMAL>Sous-total<BR>")
            append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
        }

        groupe_Produit.forEachIndexed { index, produit_vent ->
            val productKeyId = produit_vent.first
            val operations_du_produit = produit_vent.second

            val datas_repo13TarificationInfos = repo13TarificationInfos.datasValue
            val standart_Vent = operations_du_produit.first()
            val relative_M1Produit = repoM1Produit.datasValue.find { it.keyID == productKeyId }

            val quantite_Boit_Par_Carton = relative_M1Produit?.quantite_Boit_Par_Carton ?: 1
            val vent_quantity = operations_du_produit.sumOf { it.quantity }

            val quantityDisplay = formatQuantityDisplay(vent_quantity, quantite_Boit_Par_Carton)

            val relative_Tariffication = datas_repo13TarificationInfos.find {
                it.keyID == standart_Vent.parentM13TarificationKeyID
            }

            val vent_prix = if (relative_Tariffication != null) {
                relative_Tariffication.prixCurrency
            } else {
                val superGrosTariff = datas_repo13TarificationInfos
                    .filter { tariff ->
                        tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros &&
                                tariff.parent_M1Produit_KeyId == productKeyId
                    }
                    .maxByOrNull { it.dernierTimeTampsSynchronisationAvecFireBase }

                superGrosTariff?.prixCurrency ?: (relative_M1Produit?.prixAchat ?: 0.0)
            }

            val subtotal = vent_prix * vent_quantity

            if (subtotal != 0.0) {
                texteImprimable.apply {
                    append("<MEDIUM1><LEFT>${relative_M1Produit?.nom}<BR>")
                    append(" <MEDIUM1><LEFT>${quantityDisplay}x ")
                    append("<MEDIUM1><LEFT>${vent_prix}Da ")
                    append("<SMALL>$subtotal<BR>")

                    val productComment = getProductComment(operations_du_produit)
                    if (productComment.isNotBlank()) {
                        val formattedComment = formatCommentForPrinting(productComment)
                        append("$formattedComment<BR>")
                    }

                    append("<LEFT><NORMAL><MEDIUM1>---------------------<BR>")
                }
                totaleBon += subtotal
                itemCount++  // FIXED: Increment item count

                if ((index + 1) % 15 == 0) {
                    pageCounter++
                    texteImprimable.append("<BR><CENTER>PAGE $pageCounter<BR><BR><BR>")
                }
            }
        }

        texteImprimable.apply {
            append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
            append("<BR><BR>")

            // FIXED: Add item count display like PDF
            append("<MEDIUM1><CENTER>Total ($itemCount items)<BR>")
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


    private fun getProductComment(operations: List<M10OperationVentCouleur>): String {
        val comments = operations.mapNotNull { it.commetaire }.filter { it.isNotBlank() }
        return comments.firstOrNull() ?: ""
    }

    private fun formatCommentForPrinting(comment: String): String {
        if (comment.isBlank()) return ""

        val transliteratedComment = transliterateArabicToLatin(comment)

        val cleanedComment = transliteratedComment
            .replace(Regex("\\s+"), " ")
            .trim()

        if (cleanedComment.isBlank()) return ""

        val structuredItems = parseStructuredColorComment(cleanedComment)
        if (structuredItems.isNotEmpty()) {
            return formatStructuredColorItems(structuredItems)
        }

        return formatSimpleComment(cleanedComment)
    }

    private fun parseStructuredColorComment(comment: String): List<ColorItem> {
        val items = mutableListOf<ColorItem>()

        val pattern = Regex("([^=\\[]*?)=([0-9]+)\\[([^\\]]+)\\]")
        val matches = pattern.findAll(comment)

        matches.forEach { match ->
            val nameWithEmoji = match.groups[1]?.value?.trim() ?: ""
            val quantity = match.groups[2]?.value?.toIntOrNull() ?: 0
            val details = match.groups[3]?.value?.trim() ?: ""

            if (nameWithEmoji.isNotEmpty() && quantity > 0) {
                val cleanName = extractCleanName(nameWithEmoji)
                val emoji = extractEmoji(nameWithEmoji)

                items.add(
                    ColorItem(
                        displayName = nameWithEmoji.trim(),
                        cleanName = cleanName,
                        emoji = emoji,
                        quantity = quantity,
                        clients = parseClientList(details)
                    )
                )
            }
        }

        return items
    }

    private fun extractCleanName(nameWithEmoji: String): String {
        return nameWithEmoji
            .replace(Regex("([🍓🍋🌊🍛🍬🍤])\\s*(.+?)\\s*\\1"), "$2")
            .replace(Regex("[🍓🍋🌊🍛🍬🍤]"), "")
            .replace(Regex("\\?{2,}"), "")
            .trim()
    }

    private fun extractEmoji(nameWithEmoji: String): String {
        val emojiPattern = Regex("[🍓🍋🌊🍛🍬🍤]")
        return emojiPattern.find(nameWithEmoji)?.value ?: ""
    }

    private fun parseClientList(details: String): List<String> {
        val clients = mutableListOf<String>()
        val pattern = Regex("([^\\(\\)\\s]+)\\(([0-9]+)\\)")
        val matches = pattern.findAll(details)

        matches.forEach { match ->
            val clientName = match.groups[1]?.value?.trim() ?: ""
            val clientQty = match.groups[2]?.value ?: ""

            if (clientName.isNotBlank()) {
                clients.add("$clientName($clientQty)")
            }
        }

        return clients
    }

    private fun formatStructuredColorItems(items: List<ColorItem>): String {
        val lines = mutableListOf<String>()

        items.forEach { item ->
            val mainLine = if (item.emoji.isNotEmpty()) {
                "${item.emoji} ${item.cleanName}: ${item.quantity}"
            } else {
                "${item.cleanName}: ${item.quantity}"
            }

            lines.add("<SMALL>  $mainLine")

            if (item.clients.isNotEmpty()) {
                val clientsText = item.clients.joinToString(" ")
                if (clientsText.length <= 28) {
                    lines.add("<SMALL>    $clientsText")
                } else {
                    var currentLine = "<SMALL>    "
                    item.clients.forEach { client ->
                        if ((currentLine + client + " ").length <= 32) {
                            currentLine += "$client "
                        } else {
                            lines.add(currentLine.trimEnd())
                            currentLine = "<SMALL>    $client "
                        }
                    }
                    if (currentLine.trim().length > 8) {
                        lines.add(currentLine.trimEnd())
                    }
                }
            }
        }

        return lines.joinToString("<BR>")
    }

    private fun formatSimpleComment(comment: String): String {
        val maxLineLength = 32
        val lines = mutableListOf<String>()

        if (comment.length <= maxLineLength) {
            lines.add("<SMALL>  $comment")
        } else {
            val words = comment.split(' ')
            var currentLine = "<SMALL>  "

            words.forEach { word ->
                if ((currentLine + word).length <= maxLineLength + 8) {
                    currentLine += "$word "
                } else {
                    if (currentLine.trim().length > 8) {
                        lines.add(currentLine.trimEnd())
                    }
                    currentLine = "<SMALL>  $word "
                }
            }

            if (currentLine.trim().length > 8) {
                lines.add(currentLine.trimEnd())
            }
        }

        return lines.joinToString("<BR>")
    }

    data class ColorItem(
        val displayName: String,
        val cleanName: String,
        val emoji: String,
        val quantity: Int,
        val clients: List<String>
    )

    private fun formatQuantityDisplay(quantity: Int, quantiteBoitParCarton: Int): String {
        return if (quantiteBoitParCarton in 2..quantity && quantity % quantiteBoitParCarton == 0) {
            val cartons = quantity / quantiteBoitParCarton
            "$cartons X $quantiteBoitParCarton"
        } else {
            quantity.toString()
        }
    }

    private fun prepareCreditBluetoothText(
        client: M2Client?,
        bonVent: M8BonVent,
        previousPayments: List<Double>,
        showPaymentHistory: Boolean,
        companyHeader: String
    ): String {
        val dateString = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        // Use the helper method for consistent client name handling
        val clientName = getClientDisplayName(client)

        val totalAmount = bonVent.sum_De_Totale_Vents
        val currentPayment = bonVent.versement
        val totalPaid =
            if (showPaymentHistory) previousPayments.sum() + currentPayment else currentPayment
        val remainingAmount = totalAmount - totalPaid

        return StringBuilder().apply {
            if (companyHeader != "Belfort Gros Confisserie") {
                append("<BIG><CENTER>Abdelwahab<BR>")
            }
            append("<BIG><CENTER>$companyHeader<BR>")
            append("<SMALL><CENTER>0553885037<BR>")
            append("<SMALL><CENTER> - Credit Payment${if (showPaymentHistory) " Prix_Detaille" else ""}<BR>")
            append("<BR>")
            append("<SMALL><CENTER>$clientName                        $dateString<BR>")
            append("<BR>")
            append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
            append("<BR>")

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

            append("<BR><BR><BR>>")
        }.toString()
    }

    private fun round(value: Double): Double = kotlin.math.round(value * 10) / 10.0
}
