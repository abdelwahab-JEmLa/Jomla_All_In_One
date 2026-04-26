package Application5.App.Repository

/**
 * Enum class for absence justification shortcuts
 * Maps single character inputs to full Arabic justification text
 */
enum class AbsenceJustificationShortcuts(
    val shortcut: String,
    val fullText: String,
    val arabicDescription: String
) {
    MUBARRAR(
        shortcut = "م",
        fullText = "مبرر",
        arabicDescription = "مبرر"
    ),
    IJAZA_MIN_MADRASA(
        shortcut = "ا",
        fullText = "مجاز من المدرسة",
        arabicDescription = "اجازة من المدرسة"
    );

    companion object {
        /**
         * Process input and return full text if shortcut matches
         * Returns original input if no match found
         */
        fun processInput(input: String): String {
            return values().find { it.shortcut == input }?.fullText ?: input
        }

        /**
         * Get helper text for UI display
         * Shows available shortcuts and their meanings
         */
        fun getHelperText(): String {
            return values().joinToString(" | ") { "${it.shortcut} = ${it.arabicDescription}" }
        }

        /**
         * Check if input is a valid shortcut
         */
        fun isShortcut(input: String): Boolean {
            return values().any { it.shortcut == input }
        }
    }
}
