package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View.DropDownItems.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import EntreApps.Shared.Models.M13TarificationInfos
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun DropDownItem_WhenItsAchatsFragment_AddAllPendingOrders(
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repositorysMainGetter = aCentralFacade.repositorysMainGetter
    val repositorysMainSetter = aCentralFacade.repositorysMainSetter
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter

    DropdownMenuItem(
        text = { Text("Ajouter toutes les commandes en attente") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.PlaylistAdd,
                contentDescription = "Add all pending orders"
            )
        },
        onClick = {
            scope.launch {
                try {
                    val currentBonVent = focusedValuesGetter.activeOnVent_M8BonVent

                    if (currentBonVent == null) {
                        Toast.makeText(
                            context,
                            "Aucun bon de vente actif",
                            Toast.LENGTH_SHORT
                        ).show()
                        onDismissDropdown()
                        return@launch
                    }

                    // Get all colors with pending orders
                    val colorsWithPendingOrders = repositorysMainGetter.repo03CouleurProduitInfos.datasValue
                        .filter { it.a_cammende_depuit_grossist > 0 }

                    if (colorsWithPendingOrders.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Aucune commande en attente",
                            Toast.LENGTH_SHORT
                        ).show()
                        onDismissDropdown()
                        return@launch
                    }

                    var addedCount = 0
                    val repo13TarificationInfos = repositorysMainGetter.repo13TarificationInfos
                    val currentApp_ItsWorkChezGrossisst = focusedValuesGetter.currentApp_ItsWorkChezGrossisst

                    // For each color with pending orders, create or update a vent operation
                    colorsWithPendingOrders.forEach { couleur ->
                        // Get the product info
                        val produit = repositorysMainGetter.repo1ProduitInfos.datasValue
                            .find { it.keyID == couleur.parentBProduitInfosKeyID }

                        if (produit != null) {
                            val existingVent = repositorysMainGetter.repo10OperationVentCouleur.datasValue
                                .find {
                                    it.parent_M8BonVent_KeyId == currentBonVent.keyID &&
                                            it.parent_M3CouleurProduit_KeyID == couleur.keyID
                                }

                            val quantityToAdd = couleur.a_cammende_depuit_grossist

                            if (existingVent != null) {
                                // Update existing vent operation
                                val updatedVent = existingVent.copy(
                                    quantity = existingVent.quantity + quantityToAdd,
                                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                )
                                repositorysMainGetter.repo10OperationVentCouleur.addOrUpdateData(updatedVent)
                            } else {
                                val existingTariff = repo13TarificationInfos.datasValue.find { tariff ->
                                    tariff.parent_M1Produit_KeyId == produit.keyID &&
                                            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros
                                }

                                // Determine or create the tariff first
                                val tariffToUse = if (existingTariff != null) {
                                    existingTariff
                                } else {
                                    // Always create Tariff_ItsWorkInGrossist_SuperGros
                                    val newTariff = M13TarificationInfos(
                                        typeChoisi = M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
                                        prixCurrency = produit.prixAchat,
                                        parent_M1Produit_KeyId = produit.keyID,
                                        parent_M1Produit_DebugInfos = produit.nom
                                    )

                                    repo13TarificationInfos.add(newTariff)
                                    newTariff
                                }

                                // Create new vent operation with proper tariff reference
                                val newVent = M10OperationVentCouleur.get_default_By_BonVentEtCouleur(
                                    currentBonVent,
                                    couleur
                                ).copy(
                                    quantity = quantityToAdd,
                                    setIN_Vent_Its_Quantity_Represent = produit.setIN_Vent_Its_Quantity_Represent,
                                    quantite_Boit_Par_Carton = produit.quantite_Boit_Par_Carton,
                                    parentM13TarificationKeyID = tariffToUse.keyID,
                                    parentM13TarificationDebugInfos = tariffToUse.getDebugInfos(),
                                    etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ChoisiQuantityConfirme,
                                    type = M10OperationVentCouleur.Type.CommandeDeLui,
                                    creationTimestamps = System.currentTimeMillis(),
                                    its_created_in_working_for_wholesaler = currentApp_ItsWorkChezGrossisst,
                                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                )
                                repositorysMainGetter.repo10OperationVentCouleur.add_New(newVent)

                                // Link tariff to vent if it was newly created
                                if (existingTariff == null) {
                                    repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                                        m13TarificationInfos_Pour_Produit = tariffToUse,
                                        m10OperationVentCouleurs = listOf(newVent),
                                        aCentralFacade = aCentralFacade
                                    )
                                }
                            }

                            addedCount++
                        }
                    }

                    Toast.makeText(
                        context,
                        "Ajouté $addedCount commande(s) au bon de vente",
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Erreur: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                onDismissDropdown()
            }
        }
    )
}
