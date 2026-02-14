package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option

// ─────────────────────────────────────────────────────────────────────────────
// PhoneDisplayUtils.kt
// Shared phone display helper — used by:
//   • ID4ClientSearchButton  (label bar)
//   • ClientSearchItem       (dropdown list card)
//
// Output format:  " 📞OR 37"  |  " 📞DJ 56"  |  " 📞MO 77"
// Operators (from 2nd digit after leading 0):
//   5 → Or  (Ooredoo)
//   7 → Dj  (Djezzy)
//   6 → Mo  (Mobilis)
//
// Hidden when:
//   • numTelephone is empty
//   • full 9-digit core matches Abdelwahab's number  (+213 553 88 50 37)
//   • last 4 significant digits match Abdelwahab's   (5037)
// ─────────────────────────────────────────────────────────────────────────────

/** Strip every non-digit character (spaces, dashes, +). */
private fun digitsOnly(phone: String): String =
    phone.filter { it.isDigit() }

/**
 * Return the 9 significant digits of a DZ mobile number,
 * dropping the leading country code (213) or trunk prefix (0).
 *
 * "+213 553 88 50 37" → "553885037"
 * "0553885037"        → "553885037"
 * "213553885037"      → "553885037"
 * "553885037"         → "553885037"
 */
private fun dzSignificantDigits(phone: String): String {
    val d = digitsOnly(phone)
    return when {
        d.startsWith("213") -> d.removePrefix("213")
        d.startsWith("0")   -> d.removePrefix("0")
        else                -> d
    }
}

// Abdelwahab Osstad — Utilisateur enum: num_telephone = "+213 553 88 50 37"
private val ABDELWAHAB_SIG  = dzSignificantDigits("+213 553 88 50 37") // "553885037"
private val ABDELWAHAB_LAST4 = ABDELWAHAB_SIG.takeLast(4)              // "5037"

/**
 * Build a short phone hint to append to a client's display name.
 *
 * Returns an empty string (no display) when:
 *  - [numTelephone] is blank
 *  - the number belongs to Abdelwahab (full match OR last-4 match → "5037")
 *  - the number cannot be recognised as a valid DZ mobile number
 *
 * @param numTelephone  raw phone string as stored in M2Client (any format)
 * @param nomClient     client display name — unused for the guard but kept for
 *                      call-site symmetry and future name-based rules
 */
fun formatPhoneDisplay(numTelephone: String, nomClient: String = ""): String {
    if (numTelephone.isBlank()) return ""

    val sig = dzSignificantDigits(numTelephone)

    // Guard 1 — full number matches Abdelwahab's
    if (sig == ABDELWAHAB_SIG) return ""

    // Guard 2 — last 4 digits match Abdelwahab's (5037)
    if (sig.takeLast(4) == ABDELWAHAB_LAST4) return ""

    // Rebuild a canonical local form (0XXXXXXXXX) for operator detection
    val normalized = ("0$sig").filter { it.isDigit() }

    if (!normalized.startsWith("0") || normalized.length < 9) return ""

    val operatorLabel = when (normalized.getOrNull(1)) {
        '5' -> "Ned"  // Ooredoo
        '7' -> "Dj"  // Djezzy
        '6' -> "Mo"  // Mobilis
        else -> ""   // Unknown operator — show icon only, no letter
    }

    val lastTwo = normalized.takeLast(2)
    return " 📞${operatorLabel}:$lastTwo"
}
