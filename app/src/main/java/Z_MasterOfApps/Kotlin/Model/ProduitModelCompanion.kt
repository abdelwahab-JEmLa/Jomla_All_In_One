package Z_MasterOfApps.Kotlin.Model

import java.time.LocalDateTime

open class ProduitModelCompanion {
    fun _ModelAppsFather.ProduitModel.calculeSelfGrossistBonCommandesExtension(): _ModelAppsFather.ProduitModel {

        // Early return if no active sales
        if (bonsVentDeCetteCota.isEmpty()) {
            bonCommendDeCetteCota = null
            return this
        }

        // Get grossist info from current or history
        val grossistInfo = bonCommendDeCetteCota?.grossistInformations
            ?: historiqueBonsCommend.lastOrNull()?.grossistInformations

        // Aggregate colors and quantities
        val aggregatedColors = bonsVentDeCetteCota
            .flatMap { it.colours_Achete }
            .groupBy { it.couleurId }
            .mapNotNull { (couleurId, colors) ->
                colors.firstOrNull()?.let { color ->
                    val totalQuantity = colors.sumOf { it.quantity_Achete }
                    if (totalQuantity > 0) {
                        _ModelAppsFather.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee(
                            id = couleurId,
                            nom = color.nom,
                            emogi = color.imogi
                        ).apply { quantityAchete = totalQuantity }
                    } else null
                }
            }

        // Create new bon commande
        val newBonCommande = _ModelAppsFather.ProduitModel.GrossistBonCommandes(
            vid = System.currentTimeMillis(),
            date = LocalDateTime.now().toString(),
            init_grossistInformations = grossistInfo,
            init_coloursEtGoutsCommendee = aggregatedColors
        ).apply {
            mutableBasesStates = bonCommendDeCetteCota?.mutableBasesStates
                ?: _ModelAppsFather.ProduitModel.GrossistBonCommandes.MutableBasesStates()
        }

        // Update only if there are changes
        val hasChanges = bonCommendDeCetteCota?.let { old ->
            old.coloursEtGoutsCommendee.sortedBy { it.id } !=
                    newBonCommande.coloursEtGoutsCommendee.sortedBy { it.id }
        } ?: true

        if (hasChanges) {
            bonCommendDeCetteCota = newBonCommande
            if (!historiqueBonsCommend.any { it.vid == newBonCommande.vid }) {
                historiqueBonsCommend.add(newBonCommande)
            }
        }

        return this
    }
}
