package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B5.View

import java.text.DecimalFormat
import java.util.Locale

/**
 * Utility class for formatting data in PDF documents
 * Handles text formatting, number formatting, and string manipulation
 */
class PdfFormatterUtils_2 {

    private val decimalFormat = DecimalFormat("#,##0.00")

    /**
     * Round a double to 2 decimal places
     */
    fun round(value: Double): Double {
        return String.format(Locale.US, "%.2f", value).toDouble()
    }

    /**
     * Format currency value with proper decimal places
     */
    fun formatCurrency(value: Double): String {
        return decimalFormat.format(round(value))
    }

    /**
     * Capitalize first letter of a string
     */
    fun capitalizeFirstLetter(text: String): String {
        if (text.isEmpty()) return text
        return text.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
            else it.toString()
        }
    }

    /**
     * Clean and capitalize product name
     * Removes extra spaces, dots, and capitalizes properly
     */
    fun cleanAndCapitalizeProductName(name: String): String {
        return name
            .trim()
            .replace(Regex("\\s+"), " ")
            .replace(".", "")
            .split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                    else it.toString()
                }
            }
    }

    /**
     * Format phone number for display
     */
    fun formatPhoneNumber(phone: String): String {
        val cleaned = phone.replace(Regex("[^0-9]"), "")
        return when {
            cleaned.length == 10 -> {
                "${cleaned.substring(0, 4)} ${cleaned.substring(4, 6)} ${cleaned.substring(6, 8)} ${cleaned.substring(8)}"
            }
            cleaned.length == 9 -> {
                "${cleaned.substring(0, 3)} ${cleaned.substring(3, 5)} ${cleaned.substring(5, 7)} ${cleaned.substring(7)}"
            }
            else -> phone
        }
    }

    /**
     * Truncate text to fit within specified width
     */
    fun truncateText(text: String, maxLength: Int): String {
        return if (text.length > maxLength) {
            text.substring(0, maxLength - 3) + "..."
        } else {
            text
        }
    }

    /**
     * Format client name - removes extensions and capitalizes
     */
    fun formatClientName(name: String): String {
        return name
            .substringBefore(".")
            .trim()
            .let { capitalizeFirstLetter(it) }
    }

    /**
     * Convert number to words in French (improved for Algerian Dinars)
     * Example: 327250.00 -> "TROIS CENT VINGT-SEPT MILLE DEUX CENT CINQUANTE DINARS ALGÉRIENS ET ZÉRO CENTIMES"
     */
    fun numberToWordsFrench(amount: Double): String {
        val integerPart = amount.toInt()
        val decimalPart = ((amount - integerPart) * 100).toInt()

        val integerWords = convertIntegerToWordsFrench(integerPart)
        val decimalWords = if (decimalPart == 0) "ZÉRO" else convertIntegerToWordsFrench(decimalPart)

        return "$integerWords DINARS ALGÉRIENS ET $decimalWords CENTIMES"
    }

    /**
     * Convert integer to French words (0-999,999,999)
     */
    private fun convertIntegerToWordsFrench(number: Int): String {
        if (number == 0) return "ZÉRO"

        val result = mutableListOf<String>()
        var remaining = number

        // Millions
        if (remaining >= 1_000_000) {
            val millions = remaining / 1_000_000
            if (millions == 1) {
                result.add("UN MILLION")
            } else {
                result.add(convertHundreds(millions) + " MILLIONS")
            }
            remaining %= 1_000_000
        }

        // Thousands
        if (remaining >= 1000) {
            val thousands = remaining / 1000
            if (thousands == 1) {
                result.add("MILLE")
            } else {
                result.add(convertHundreds(thousands) + " MILLE")
            }
            remaining %= 1000
        }

        // Hundreds, tens, units
        if (remaining > 0) {
            result.add(convertHundreds(remaining))
        }

        return result.joinToString(" ").trim()
    }

    /**
     * Convert number 0-999 to French words
     */
    private fun convertHundreds(number: Int): String {
        if (number == 0) return ""

        val units = arrayOf("", "UN", "DEUX", "TROIS", "QUATRE", "CINQ", "SIX", "SEPT", "HUIT", "NEUF")
        val teens = arrayOf("DIX", "ONZE", "DOUZE", "TREIZE", "QUATORZE", "QUINZE", "SEIZE",
            "DIX-SEPT", "DIX-HUIT", "DIX-NEUF")
        val tens = arrayOf("", "DIX", "VINGT", "TRENTE", "QUARANTE", "CINQUANTE",
            "SOIXANTE", "SOIXANTE-DIX", "QUATRE-VINGT", "QUATRE-VINGT-DIX")

        val result = mutableListOf<String>()
        var remaining = number

        // Hundreds
        if (remaining >= 100) {
            val hundreds = remaining / 100
            when {
                hundreds == 1 -> result.add("CENT")
                else -> result.add("${units[hundreds]} CENT")
            }
            remaining %= 100

            // Add "S" to CENT if it's a round hundred (200, 300, etc.)
            if (remaining == 0 && hundreds > 1) {
                result[result.size - 1] = result.last() + "S"
            }
        }

        // Tens and units
        when {
            remaining == 0 -> {}
            remaining < 10 -> result.add(units[remaining])
            remaining < 20 -> result.add(teens[remaining - 10])
            remaining < 70 -> {
                val tensDigit = remaining / 10
                val unitsDigit = remaining % 10
                when {
                    unitsDigit == 0 -> result.add(tens[tensDigit])
                    unitsDigit == 1 && tensDigit != 8 -> result.add("${tens[tensDigit]}-ET-UN")
                    else -> result.add("${tens[tensDigit]}-${units[unitsDigit]}")
                }
            }
            remaining < 80 -> {
                // 70-79: soixante-dix, soixante-et-onze, etc.
                val unitsDigit = remaining - 60
                if (unitsDigit == 11) {
                    result.add("SOIXANTE-ET-ONZE")
                } else if (unitsDigit < 20) {
                    result.add("SOIXANTE-${teens[unitsDigit - 10]}")
                } else {
                    result.add("SOIXANTE-${convertHundreds(unitsDigit)}")
                }
            }
            remaining == 80 -> result.add("QUATRE-VINGTS")
            remaining < 90 -> {
                // 81-89: quatre-vingt-un, etc.
                val unitsDigit = remaining % 10
                result.add("QUATRE-VINGT-${units[unitsDigit]}")
            }
            else -> {
                // 90-99: quatre-vingt-dix, quatre-vingt-onze, etc.
                val unitsDigit = remaining - 80
                if (unitsDigit < 20) {
                    result.add("QUATRE-VINGT-${teens[unitsDigit - 10]}")
                }
            }
        }

        return result.joinToString(" ").replace("  ", " ").trim()
    }

    /**
     * Validate and sanitize text for PDF rendering
     * Removes problematic characters that might cause rendering issues
     */
    fun sanitizeForPdf(text: String): String {
        return text
            .replace("\u0000", "")
            .replace(Regex("[\\p{C}&&[^\n\r\t]]"), "")
            .trim()
    }

    /**
     * Format address for multi-line display
     */
    fun formatAddress(address: String, maxLineLength: Int = 40): List<String> {
        val words = address.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            if ((currentLine + " " + word).length <= maxLineLength) {
                currentLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine)

        return lines
    }

    /**
     * Calculate text width (approximate) for layout purposes
     */
    fun estimateTextWidth(text: String, fontSize: Float): Float {
        return text.length * fontSize * 0.5f
    }
}
