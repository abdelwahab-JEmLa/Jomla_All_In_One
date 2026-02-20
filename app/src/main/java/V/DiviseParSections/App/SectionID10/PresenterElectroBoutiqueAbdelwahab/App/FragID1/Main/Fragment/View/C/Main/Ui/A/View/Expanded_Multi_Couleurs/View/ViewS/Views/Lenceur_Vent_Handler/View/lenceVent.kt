package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.ViewS.Views.Lenceur_Vent_Handler.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import kotlin.math.abs

fun lenceVent(
    relative_M10OperationVentCouleur: M10OperationVentCouleur?,
    defaultM10Vent: M10OperationVentCouleur,
    finale_Tariff: M13TarificationInfos,
    relative_M3CouleurInfos: M3CouleurProduitInfos,
    aCentralFacade: ACentralFacade,
    onDepotUpdateFailed: (DepotUpdateResult) -> Unit
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val focusedValuesSetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
    val repositorysMainSetter = aCentralFacade.repositorysMainSetter

    relative_M10OperationVentCouleur?.let { findVent ->
        // Existing vent operation - just save tariff and open dialog
        repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
            finale_Tariff,
            buildList { add(findVent) },
            aCentralFacade
        )
        focusedValuesSetter.active_M3Couleur_pour_ouvrire_son_Dialog_choixQuantity(findVent)
    } ?: run {
        // New vent operation - create it, save tariff, and update depot
        focusedValuesSetter.ajoute_New_M10OperationVentCouleur(defaultM10Vent)
        repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
            finale_Tariff,
            buildList { add(defaultM10Vent) },
            aCentralFacade
        )

        // Update depot count if not working in wholesale mode
        if (!focusedValuesGetter.currentApp_ItsWorkChezGrossisst) {
            val result = update_countDepot(
                aCentralFacade,
                relative_M3CouleurInfos,
                -1,
                active = focusedValuesGetter.currentApp_ItsWorkChezGrossisst
            )
            if (!result.success) {
                onDepotUpdateFailed(result)
            }
        }
    }

    // Set the focused tariff for the current product
    focusedValuesGetter.currentActive_M9AppCompt?.let { appCompt ->
        repositorysMainSetter.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
            aCentralFacade.repositorysMainGetter.repo1ProduitInfos
                .datasValue.find { it.keyID == relative_M3CouleurInfos.parentBProduitInfosKeyID }
                ?: return@let,
            appCompt
        )
    }
}

/**
 * Data class to hold depot update result information
 */
data class DepotUpdateResult(
    val success: Boolean,
    val message: String = "",
    val currentCount: Int = 0,
    val requestedChange: Int = 0
)

/**
 * Updates the depot count for a color
 * Returns a result indicating success/failure with relevant information
 */
fun update_countDepot(
    aCentralFacade: ACentralFacade,
    couleur: M3CouleurProduitInfos,
    quantityChange: Int,
    active: Boolean
): DepotUpdateResult {
    if (active) {
        return DepotUpdateResult(
            success = true,
            message = "Depot update skipped in wholesale mode"
        )
    }

    val newCount = couleur.count_Don_Depot + quantityChange

    if (newCount < 0) {
        return DepotUpdateResult(
            success = false,
            message = "Stock insuffisant au dépôt",
            currentCount = couleur.count_Don_Depot,
            requestedChange = quantityChange
        )
    }

    val updatedCouleur = couleur.copy(
        count_Don_Depot = newCount,
        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
    )

    aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.addOrUpdateData(updatedCouleur)

    return DepotUpdateResult(
        success = true,
        message = "Dépôt mis à jour avec succès",
        currentCount = newCount,
        requestedChange = quantityChange
    )
}

/**
 * Composable to display depot alert dialog
 */
@Composable
fun DepotAlertInfo(
    alertInfo: DepotUpdateResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Alerte Stock",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text = buildString {
                    append(alertInfo.message)
                    append("\n\n")
                    append("Stock actuel: ${alertInfo.currentCount}")
                    append("\n")
                    append("Quantité demandée: ${abs(alertInfo.requestedChange)}")
                }
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
