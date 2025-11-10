package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View.DropDownItems.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
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
                    
                    // For each color with pending orders, create or update a vent operation
                    colorsWithPendingOrders.forEach { couleur ->
                        // Get the product info
                        val produit = repositorysMainGetter.repo1ProduitInfos.datasValue
                            .find { it.keyID == couleur.parentBProduitInfosKeyID }

                        if (produit != null) {
                            // Check if this color already has a vent operation in the current bon
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
                                // Create new vent operation
                                val newVent = M10OperationVentCouleur.get_default_By_BonVentEtCouleur(
                                    currentBonVent,
                                    couleur
                                ).copy(
                                    quantity = quantityToAdd,
                                    setIN_Vent_Its_Quantity_Represent = produit.setIN_Vent_Its_Quantity_Represent,
                                    quantite_Boit_Par_Carton = produit.quantite_Boit_Par_Carton,
                                    etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ChoisiQuantityConfirme,
                                    type = M10OperationVentCouleur.Type.CommandeDeLui,
                                    creationTimestamps = System.currentTimeMillis(),
                                    its_created_in_working_for_wholesaler = focusedValuesGetter.currentApp_ItsWorkChezGrossisst
                                )
                                repositorysMainGetter.repo10OperationVentCouleur.add_New(newVent)
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
