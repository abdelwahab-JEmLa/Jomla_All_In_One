package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.P.Buttons.Enhanced_Affiche_MotivationAu_Vendeur_De_Plus_De_Benifices

/**
 * Returns the correct Arabic plural form for "product" based on the number
 * Following Arabic grammar rules:
 * - 1 or >=11: منتج (single form)
 * - 2: منتجين (dual form)
 * - 3-10: منتجات (plural form)
 */
fun get_BestNomArabDuPlurieul(nmbr: Int = 0): String {
    return when {
        nmbr == 1 || nmbr >= 11 -> "منتج"
        nmbr == 2 -> "منتجين"
        nmbr in 3..10 -> "منتجات"
        else -> "منتج" // fallback for 0 or negative numbers
    }
}
