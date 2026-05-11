package EntreApps.Shared.Models.Relative_Produits.Models.Functions

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode

fun get_filtred_m3_by_limite_period(
    ventPeriods: MutableList<M14VentPeriode>,
    colors: MutableList<M3CouleurProduitInfos>
): List<M3CouleurProduitInfos> {
    val lastVentPeriod = ventPeriods.maxByOrNull { it.creationTimestamp }
    val filteredColors = if (lastVentPeriod != null && lastVentPeriod.its_limite_active_couleurs) {
        colors.filter { it.dernier_achant_timeTamp <= lastVentPeriod.creationTimestamp }
    } else {
        colors
    }
    return filteredColors
}
