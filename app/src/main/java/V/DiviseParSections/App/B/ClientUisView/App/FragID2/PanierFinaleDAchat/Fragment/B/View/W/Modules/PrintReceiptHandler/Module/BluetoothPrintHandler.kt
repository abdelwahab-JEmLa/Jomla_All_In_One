package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BluetoothPrintHandler {
    private val PRINT_INTENT = "pe.diegoveloper.printing"
    private val TAG = "BluetoothPrintHandler"

    fun printBluetoothReceipt(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        repo13TarificationInfos: Repo13TarificationInfos,
        repoM1Produit: RepoM1Produit,
        bonVent: M8BonVent? = null,
        showCreditSection: Boolean = false,
        versement: Double = 0.0
    ): Boolean {
        Log.d(TAG, "=== DEBUT IMPRESSION BLUETOOTH ===")
        Log.d(TAG, "Nombre total d'opérations reçues: ${operations.size}")

        if (!isBluetoothAvailable()) {
            Log.e(TAG, "Bluetooth non disponible ou désactivé")
            return false
        }

        if (operations.isEmpty()) {
            Log.e(TAG, "Aucune opération à imprimer")
            return false
        }

        return try {
            val transactionId = "vent_${System.currentTimeMillis().toString().takeLast(4)}"

            // Transliterate client name from Arabic to Latin
            val clientName = client?.nom?.takeIf { it.isNotBlank() }?.let {
                transliterateClientName(it)
            } ?: "Client"

            Log.d(TAG, "Nom client (après translittération): $clientName")

            val (texteImprimable, totalCalcule) = prepareTexteToPrint(
                operations,
                clientName,
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
                client?.currentCreditBalance ?: 0.0,
                repo13TarificationInfos,
                repoM1Produit
            )

            Log.d(TAG, "Total calculé: $totalCalcule Da")
            Log.d(TAG, "Longueur du texte à imprimer: ${texteImprimable.length}")

            val finalBluetoothText = if (showCreditSection && bonVent != null && false) {
                addCreditSectionToBluetoothText(
                    texteImprimable.toString(),
                    client,
                    bonVent,
                    versement,
                    transactionId
                )
            } else {
                texteImprimable.toString()
            }

            handleBluetoothPrint(context, finalBluetoothText)
            Log.d(TAG, "=== IMPRESSION ENVOYÉE AVEC SUCCÈS ===")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de l'impression: ${e.message}", e)
            e.printStackTrace()
            false
        }
    }

    fun printCreditBluetoothReceipt(
        context: Context,
        client: M2Client?,
        bonVent: M8BonVent,
        previousPayments: List<Double> = emptyList(),
        showPaymentHistory: Boolean = false
    ): Boolean {
        if (!isBluetoothAvailable()) {
            return false
        }

        return try {
            val transactionId = bonVent.keyID.takeLast(4)
            val bluetoothText = prepareCreditBluetoothText(
                client, bonVent, previousPayments, showPaymentHistory, transactionId
            )
            handleBluetoothPrint(context, bluetoothText)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
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
     * Transliterate Arabic text to Latin characters for thermal printer compatibility
     */
    private fun transliterateArabicToLatin(text: String): String {
        val arabicToLatinMap = mapOf(
            // Arabic letters
            'ا' to "a", 'أ' to "a", 'إ' to "i", 'آ' to "aa",
            'ب' to "b", 'ت' to "t", 'ث' to "th", 'ج' to "j",
            'ح' to "h", 'خ' to "kh", 'د' to "d", 'ذ' to "dh",
            'ر' to "r", 'ز' to "z", 'س' to "s", 'ش' to "sh",
            'ص' to "s", 'ض' to "d", 'ط' to "t", 'ظ' to "z",
            'ع' to "a", 'غ' to "gh", 'ف' to "f", 'ق' to "q",
            'ك' to "k", 'ل' to "l", 'م' to "m", 'ن' to "n",
            'ه' to "h", 'و' to "w", 'ي' to "y", 'ى' to "a",
            'ة' to "a", 'ء' to "",

            // Arabic diacritics (tashkeel) - remove them
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
     * Transliterate client names from Arabic to Latin
     */
    private fun transliterateClientName(clientName: String): String {
        return transliterateArabicToLatin(clientName)
            .replace(Regex("\\s+"), " ") // Replace multiple spaces with single space
            .trim()
            .takeIf { it.isNotBlank() } ?: clientName // Fallback to original if empty after transliteration
    }

    private fun prepareTexteToPrint(
        operations: List<M10OperationVentCouleur>,
        nomClient: String,
        dateString: String,
        ancienCredits: Double,
        repo13TarificationInfos: Repo13TarificationInfos,
        repoM1Produit: RepoM1Produit
    ): Pair<StringBuilder, Double> {
        Log.d(TAG, "=== DÉBUT PRÉPARATION TEXTE D'IMPRESSION ===")

        val groupe_Produit = operations.groupBy { it.parent_M1Produit_KeyId }.toList()
        Log.d(TAG, "Nombre de groupes de produits: ${groupe_Produit.size}")

        val texteImprimable = StringBuilder()
        var totaleBon = 0.0
        var pageCounter = 0
        var produitsImprimes = 0
        var produitsIgnores = 0

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
            val productKeyId = produit_vent.first
            val operations_du_produit = produit_vent.second

            Log.d(TAG, "--- PRODUIT ${index + 1}/${ groupe_Produit.size} ---")
            Log.d(TAG, "ProductKeyId: $productKeyId")
            Log.d(TAG, "Nombre d'opérations pour ce produit: ${operations_du_produit.size}")

            val datas_repo13TarificationInfos = repo13TarificationInfos.datasValue
            val standart_Vent = operations_du_produit.first()
            val relative_M1Produit = repoM1Produit.datasValue.find { it.keyID == productKeyId }

            Log.d(TAG, "Produit trouvé dans repo: ${relative_M1Produit?.nom ?: "INTROUVABLE"}")

            val quantite_Boit_Par_Carton = relative_M1Produit?.quantite_Boit_Par_Carton ?: 1
            val vent_quantity = operations_du_produit.sumOf { it.quantity }

            Log.d(TAG, "Quantité totale: $vent_quantity")
            Log.d(TAG, "Quantité par carton: $quantite_Boit_Par_Carton")

            operations_du_produit.forEach { operation ->
                Log.d(TAG, "  - Operation: quantity=${operation.quantity}, couleur=${operation.parent_M1Produit_KeyId}, commentaire='${operation.commetaire}'")
            }

            val quantityDisplay = formatQuantityDisplay(vent_quantity, quantite_Boit_Par_Carton)
            Log.d(TAG, "Affichage quantité: $quantityDisplay")

            // FIXED: Handle null tariff case - find SuperGros or use product purchase price
            val relative_Tariffication = datas_repo13TarificationInfos.find {
                it.keyID == standart_Vent.parentM13TarificationKeyID
            }

            Log.d(TAG, "Tarification trouvée: ${relative_Tariffication != null}")
            Log.d(TAG, "parentM13TarificationKeyID: ${standart_Vent.parentM13TarificationKeyID}")

            val vent_prix = if (relative_Tariffication != null) {
                Log.d(TAG, "Utilisation prix tarification: ${relative_Tariffication.prixCurrency}")
                relative_Tariffication.prixCurrency
            } else {
                // First try to find SuperGros tariff for this product
                val superGrosTariff = datas_repo13TarificationInfos
                    .filter { tariff ->
                        tariff.typeChoisi == V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros &&
                                tariff.parent_M1Produit_KeyId == productKeyId
                    }
                    .maxByOrNull { it.dernierTimeTampsSynchronisationAvecFireBase }

                Log.d(TAG, "SuperGros tariff trouvé: ${superGrosTariff != null}")

                // If SuperGros tariff exists, use it; otherwise use product purchase price
                val prix = superGrosTariff?.prixCurrency ?: (relative_M1Produit?.prixAchat ?: 0.0)
                Log.d(TAG, "Prix final utilisé: $prix (SuperGros: ${superGrosTariff?.prixCurrency}, PrixAchat: ${relative_M1Produit?.prixAchat})")

                // Additional check for zero price
                if (prix == 0.0) {
                    Log.w(TAG, "⚠️ ATTENTION: Prix = 0.0 détecté!")
                    Log.w(TAG, "  - Tarification normale: ${relative_Tariffication != null}")
                    Log.w(TAG, "  - SuperGros disponible: ${superGrosTariff != null}")
                    Log.w(TAG, "  - Prix d'achat produit: ${relative_M1Produit?.prixAchat}")
                    Log.w(TAG, "  - Nom produit: ${relative_M1Produit?.nom}")
                    Log.w(TAG, "  --> Ce produit ne sera PAS imprimé car prix = 0")
                }

                prix
            }

            val subtotal = vent_prix * vent_quantity
            Log.d(TAG, "Sous-total calculé: $subtotal (prix: $vent_prix × quantité: $vent_quantity)")

            if (subtotal != 0.0) {
                Log.d(TAG, "✅ PRODUIT AJOUTÉ À L'IMPRESSION")
                produitsImprimes++

                texteImprimable.apply {
                    append("<MEDIUM1><LEFT>${relative_M1Produit?.nom}<BR>")
                    append(" <MEDIUM1><LEFT>$quantityDisplay ")
                    append("<MEDIUM1><LEFT>${vent_prix}Da ")
                    append("<SMALL>$subtotal<BR>")

                    // Add product comment if it exists and is not empty
                    val productComment = getProductComment(operations_du_produit)
                    if (productComment.isNotBlank()) {
                        Log.d(TAG, "Commentaire trouvé: '$productComment'")
                        // Format comment for better readability on thermal printer
                        val formattedComment = formatCommentForPrinting(productComment)
                        append("$formattedComment<BR>")
                    } else {
                        Log.d(TAG, "Aucun commentaire pour ce produit")
                    }

                    append("<LEFT><NORMAL><MEDIUM1>---------------------<BR>")
                }
                totaleBon += subtotal
                if ((index + 1) % 15 == 0) {
                    pageCounter++
                    texteImprimable.append("<BR><CENTER>PAGE $pageCounter<BR><BR><BR>")
                }
            } else {
                Log.w(TAG, "❌ PRODUIT IGNORÉ (subtotal = 0)")
                Log.w(TAG, "Raisons possibles: prix=0, quantité=0, ou calcul incorrect")
                produitsIgnores++
            }

            Log.d(TAG, "--- FIN PRODUIT ${index + 1} ---")
        }

        Log.d(TAG, "=== RÉSUMÉ TRAITEMENT ===")
        Log.d(TAG, "Produits imprimés: $produitsImprimes")
        Log.d(TAG, "Produits ignorés: $produitsIgnores")
        Log.d(TAG, "Total bon: $totaleBon Da")

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

        Log.d(TAG, "=== FIN PRÉPARATION TEXTE D'IMPRESSION ===")
        return Pair(texteImprimable, totaleBon)
    }

    /**
     * Extract comment from the product's operations
     * Uses the first non-empty comment found in the operations for this product
     */
    private fun getProductComment(operations: List<M10OperationVentCouleur>): String {
        val comments = operations.mapNotNull { it.commetaire }.filter { it.isNotBlank() }
        Log.d(TAG, "Commentaires trouvés pour ce produit: ${comments.size}")
        comments.forEachIndexed { index, comment ->
            Log.d(TAG, "  Commentaire $index: '$comment'")
        }
        return comments.firstOrNull() ?: ""
    }

    /**
     * Format comment for thermal printer display
     * RÈGLES D'AFFICHAGE:
     * 1. GARDER LES EMOJIS pour identifier visuellement les couleurs/saveurs
     * 2. Translittérer seulement l'arabe en latin
     * 3. Format structuré: "🍓 Fraise 🍓=2[clients] -> Fraise: 2 (clients)"
     * 4. Ligne par couleur/saveur pour la lisibilité
     */
    private fun formatCommentForPrinting(comment: String): String {
        if (comment.isBlank()) return ""

        Log.d(TAG, "Formatage commentaire original: '$comment'")

        // Première étape: translittérer l'arabe mais GARDER les emojis
        val transliteratedComment = transliterateArabicToLatin(comment)
        Log.d(TAG, "Après translittération: '$transliteratedComment'")

        // Nettoyer les espaces multiples et les retours à la ligne
        val cleanedComment = transliteratedComment
            .replace(Regex("\\s+"), " ") // Multiple spaces -> single space
            .trim()

        Log.d(TAG, "Après nettoyage: '$cleanedComment'")

        if (cleanedComment.isBlank()) return ""

        // Essayer de parser le format structuré
        val structuredItems = parseStructuredColorComment(cleanedComment)
        if (structuredItems.isNotEmpty()) {
            Log.d(TAG, "Format structuré détecté: ${structuredItems.size} couleurs")
            return formatStructuredColorItems(structuredItems)
        }

        // Fallback: formatage simple
        return formatSimpleComment(cleanedComment)
    }

    /**
     * Parse les commentaires au format: "🍓 Fraise 🍓=2[abdelhamid(1) kqssi(1)] 🍋 Citron 🍋=1[kqssi(1)]"
     */
    private fun parseStructuredColorComment(comment: String): List<ColorItem> {
        val items = mutableListOf<ColorItem>()

        // Pattern pour capturer: [emoji optionnel] Nom [emoji optionnel]=quantité[détails]
        val pattern = Regex("([^=\\[]*?)=([0-9]+)\\[([^\\]]+)\\]")
        val matches = pattern.findAll(comment)

        matches.forEach { match ->
            val nameWithEmoji = match.groups[1]?.value?.trim() ?: ""
            val quantity = match.groups[2]?.value?.toIntOrNull() ?: 0
            val details = match.groups[3]?.value?.trim() ?: ""

            if (nameWithEmoji.isNotEmpty() && quantity > 0) {
                // Extraire le nom propre (enlever les emojis en double)
                val cleanName = extractCleanName(nameWithEmoji)
                val emoji = extractEmoji(nameWithEmoji)

                items.add(ColorItem(
                    displayName = nameWithEmoji.trim(),
                    cleanName = cleanName,
                    emoji = emoji,
                    quantity = quantity,
                    clients = parseClientList(details)
                ))

                Log.d(TAG, "Couleur parsée: '$cleanName' ($emoji) = $quantity [${details}]")
            }
        }

        return items
    }

    /**
     * Extraire le nom propre en supprimant les emojis redondants
     */
    private fun extractCleanName(nameWithEmoji: String): String {
        return nameWithEmoji
            .replace(Regex("([🍓🍋🍌🐛🍬🤏])\\s*(.+?)\\s*\\1"), "$2") // Remove duplicate emojis: 🍓 Fraise 🍓 -> Fraise
            .replace(Regex("[🍓🍋🍌🐛🍬🤏]"), "") // Remove remaining emojis
            .replace(Regex("\\?{2,}"), "") // Remove ??
            .trim()
    }

    /**
     * Extraire l'emoji principal pour l'affichage
     */
    private fun extractEmoji(nameWithEmoji: String): String {
        val emojiPattern = Regex("[🍓🍋🍌🐛🍬🤏]")
        return emojiPattern.find(nameWithEmoji)?.value ?: ""
    }

    /**
     * Parser la liste des clients: "abdelhamid(1) kqssi(1)" -> ["abdelhamid(1)", "kqssi(1)"]
     */
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

    /**
     * Formater les items de couleur de manière structurée
     */
    private fun formatStructuredColorItems(items: List<ColorItem>): String {
        val lines = mutableListOf<String>()

        items.forEach { item ->
            // Ligne principale: "🍓 Fraise: 2"
            val mainLine = if (item.emoji.isNotEmpty()) {
                "${item.emoji} ${item.cleanName}: ${item.quantity}"
            } else {
                "${item.cleanName}: ${item.quantity}"
            }

            lines.add("<SMALL>  $mainLine")

            // Ligne des clients si pas trop nombreux
            if (item.clients.isNotEmpty()) {
                val clientsText = item.clients.joinToString(" ")
                if (clientsText.length <= 28) { // Fit on one line
                    lines.add("<SMALL>    $clientsText")
                } else {
                    // Split clients sur plusieurs lignes
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

        val result = lines.joinToString("<BR>")
        Log.d(TAG, "Commentaire couleurs formaté: '$result'")
        return result
    }

    /**
     * Formatage simple pour les commentaires non-structurés
     */
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

    /**
     * Data class pour les items de couleur/saveur
     */
    data class ColorItem(
        val displayName: String,    // Nom original avec emojis
        val cleanName: String,      // Nom propre sans emojis
        val emoji: String,          // Emoji principal
        val quantity: Int,          // Quantité
        val clients: List<String>   // Liste des clients
    )

    private fun formatQuantityDisplay(quantity: Int, quantiteBoitParCarton: Int): String {
        return if (quantiteBoitParCarton in 2..quantity && quantity % quantiteBoitParCarton == 0) {
            val cartons = quantity / quantiteBoitParCarton
            "$cartons X $quantiteBoitParCarton"
        } else {
            quantity.toString()
        }
    }

    private fun addCreditSectionToBluetoothText(
        originalText: String,
        client: M2Client?,
        bonVent: M8BonVent,
        versement: Double,
        transactionId: String
    ): String {
        val oldBalance = client?.currentCreditBalance ?: 0.0
        val currentBill = bonVent.sum_De_Totale_Vents
        val newBalance = oldBalance + currentBill - versement
        val baseText = originalText.replace("<BR><BR><BR>>", "")

        return StringBuilder().apply {
            append(baseText)
            append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
            append("<BR>")
            append("<MEDIUM1><CENTER>SECTION CREDIT<BR>")
            append("<BR>")

            append("<MEDIUM1><LEFT>Ancien Solde :<BR>")
            append("<MEDIUM2><CENTER>${round(oldBalance)}Da<BR>")
            append("<BR>")

            append("<MEDIUM1><LEFT>Bon actuel :<BR>")
            append("<MEDIUM2><CENTER>${round(currentBill)}Da<BR>")
            append("<BR>")

            append("<MEDIUM1><LEFT>Versement :<BR>")
            append("<MEDIUM2><CENTER>${round(versement)}Da<BR>")
            append("<BR>")

            append("<LEFT><NORMAL><MEDIUM1>---------------------<BR>")
            append("<MEDIUM1><LEFT>Nouv. Solde :<BR>")

            when {
                newBalance > 0 -> {
                    append("<MEDIUM3><RIGHT><BOLD>${round(newBalance)}Da<BR>")
                    append("<SMALL><CENTER>(Reste a payer)<BR>")
                }
                newBalance < 0 -> {
                    append("<MEDIUM3><RIGHT><BOLD>${round(newBalance)}Da<BR>")
                    append("<SMALL><CENTER>(Credit client)<BR>")
                }
                else -> {
                    append("<MEDIUM2><CENTER>0.00 Da<BR>")
                    append("<MEDIUM1><CENTER> ✓ SOLDE ✓<BR>")
                }
            }

            append("<BR>")
            append("<SMALL><CENTER>Transaction: #$transactionId<BR>")
            append("<BR><BR><BR>>")
        }.toString()
    }

    private fun prepareCreditBluetoothText(
        client: M2Client?,
        bonVent: M8BonVent,
        previousPayments: List<Double>,
        showPaymentHistory: Boolean,
        transactionId: String
    ): String {
        val dateString = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        // Transliterate client name from Arabic to Latin
        val clientName = client?.nom?.takeIf { it.isNotBlank() }?.let {
            transliterateClientName(it)
        } ?: "Client"

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

    private fun round(value: Double): Double = kotlin.math.round(value * 10) / 10.0
}
