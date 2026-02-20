package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Utility class for PDF formatting operations
 */
class PdfFormatterUtils(private val repositorysMainGetter: RepositorysMainGetter) {

    fun formatDateWithAmPm(date: Date): String {
        val calendar = Calendar.getInstance().apply { time = date }
        val frenchDays = arrayOf("Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam")
        val frenchMonths = arrayOf("Jan", "Fév", "Mar", "Avr", "Mai", "Jun", "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc")
        val dayOfWeek = frenchDays[calendar.get(Calendar.DAY_OF_WEEK) - 1]
        val month = frenchMonths[calendar.get(Calendar.MONTH)]
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return "$dayOfWeek $dayOfMonth/$month/$year ${String.format("%02d:%02d", hour, minute)}"
    }

    /**
     * FIXED: Now includes category type name in parentheses if not empty
     */
    fun formatProductNameWithCategory(produit: M01Produit?): String {
        val productName = cleanAndCapitalizeProductName(produit?.nom ?: "Produit")

        val productNameWithCopyright = if (!productName.contains("©")) {
            "$productName ©"
        } else {
            productName
        }

        // Add category type name in parentheses if it exists and is not empty
        val categoryType = produit?.nomMutable?.trim()
        return if (!categoryType.isNullOrEmpty()) {
            "$productNameWithCopyright ($categoryType)"
        } else {
            productNameWithCopyright
        }
    }

    fun formatQuantity(qty: Int, cartonSize: Int, produit: M01Produit?): String {
        val shouldShowUnits = produit?.afficheUniteAuPrint == true
        val nombreUniteInt = produit?.nombreUniteInt ?: 1

        return when {
            shouldShowUnits && cartonSize in 2..qty && qty % cartonSize == 0 -> {
                val cartons = qty / cartonSize
                "$cartons X $cartonSize X $nombreUniteInt"
            }
            shouldShowUnits -> "$qty X $nombreUniteInt"
            cartonSize in 2..qty && qty % cartonSize == 0 -> {
                val cartons = qty / cartonSize
                "$cartons X $cartonSize"
            }
            else -> qty.toString()
        }
    }

    fun cleanAndCapitalizeProductName(name: String): String {
        val nameWithoutHash = name.replace("#", "").trim()

        if (nameWithoutHash.length < 2) {
            return nameWithoutHash
        }

        return nameWithoutHash.split("\\s+".toRegex())
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.lowercase(Locale.getDefault())
                    .replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                        else it.toString()
                    }
            }
    }

    fun capitalizeFirstLetter(text: String): String {
        return if (text.isBlank()) text
        else text.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
            else it.toString()
        }
    }

    fun round(value: Double): Double = kotlin.math.round(value * 10) / 10.0

    private fun findRelativeCategorie(rela_produit: M01Produit): M16CategorieProduit? =
        rela_produit.idParentCategorie?.let {
            repositorysMainGetter.find_M16CategorieProduit_By_OldID(it)
        }
}
