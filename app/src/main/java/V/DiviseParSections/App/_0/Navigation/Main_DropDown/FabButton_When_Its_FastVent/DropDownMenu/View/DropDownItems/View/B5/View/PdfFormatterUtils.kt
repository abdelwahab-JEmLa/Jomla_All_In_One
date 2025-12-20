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
            .replace(Regex("\\s+"), " ") // Replace multiple spaces with single space
            .replace(".", "") // Remove dots
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
     * Convert number to words in French (for invoice amounts)
     */
    fun numberToWordsFrench(number: Int): String {
        if (number == 0) return "zéro"
        
        val units = arrayOf("", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf")
        val teens = arrayOf("dix", "onze", "douze", "treize", "quatorze", "quinze", "seize", 
                           "dix-sept", "dix-huit", "dix-neuf")
        val tens = arrayOf("", "", "vingt", "trente", "quarante", "cinquante", 
                          "soixante", "soixante-dix", "quatre-vingt", "quatre-vingt-dix")
        
        return when {
            number < 10 -> units[number]
            number < 20 -> teens[number - 10]
            number < 100 -> {
                val ten = number / 10
                val unit = number % 10
                if (unit == 0) tens[ten] else "${tens[ten]}-${units[unit]}"
            }
            number < 1000 -> {
                val hundred = number / 100
                val rest = number % 100
                val hundredWord = if (hundred == 1) "cent" else "${units[hundred]} cent"
                if (rest == 0) hundredWord else "$hundredWord ${numberToWordsFrench(rest)}"
            }
            else -> {
                val thousand = number / 1000
                val rest = number % 1000
                val thousandWord = if (thousand == 1) "mille" else "${numberToWordsFrench(thousand)} mille"
                if (rest == 0) thousandWord else "$thousandWord ${numberToWordsFrench(rest)}"
            }
        }
    }
    
    /**
     * Validate and sanitize text for PDF rendering
     * Removes problematic characters that might cause rendering issues
     */
    fun sanitizeForPdf(text: String): String {
        return text
            .replace("\u0000", "") // Remove null characters
            .replace(Regex("[\\p{C}&&[^\n\r\t]]"), "") // Remove control characters except newlines and tabs
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
        // Rough estimation: average character width is about 0.5 * fontSize
        return text.length * fontSize * 0.5f
    }
}
