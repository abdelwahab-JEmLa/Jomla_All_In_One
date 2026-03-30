package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View.DropDownItems.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import EntreApps.Shared.Models.M10OperationVentCouleur
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
fun DropDownItem_But2(
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject()
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repositorysMainGetter = aCentralFacade.repositorysMainGetter
    val repositorysMainSetter = aCentralFacade.repositorysMainSetter
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter

    DropdownMenuItem(
        text = { Text("Ajouter les achats de la période active") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.PlaylistAdd,
                contentDescription = "Add purchases from active period"
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

                    // Get the current active period
                    val currentPeriod = focusedValuesGetter.currentActiveFocuced_M14VentPeriode

                    if (currentPeriod == null) {
                        Toast.makeText(
                            context,
                            "Aucune période active",
                            Toast.LENGTH_SHORT
                        ).show()
                        onDismissDropdown()
                        return@launch
                    }

                    // Get all vent operations for the current period that need to be purchased
                    // Exclude vents that are already delivered (Livree) or not found (Non Trouve)
                    val ventOperationsForPeriod = repositorysMainGetter.repo10OperationVentCouleur.datasValue
                        .filter {
                            it.parent_M14VentPeriod_KeyId == currentPeriod.keyID &&
                                    it.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve
                        }

                    if (ventOperationsForPeriod.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Aucune vente non livrée dans la période active",
                            Toast.LENGTH_SHORT
                        ).show()
                        onDismissDropdown()
                        return@launch
                    }

                    // Generate purchase operations from the vent operations
                    val generatedAchats = repositorysMainGetter.repo11AchatOperation.genere_Achats_Depuit_M11AchatOperation_List(
                        m14VentPeriod = currentPeriod,
                        filtered_ListM10Vent_BY_Curr_M14VentPeriod = ventOperationsForPeriod,
                        produits = repositorysMainGetter.repo1ProduitInfos.datasValue,
                        bonVents = repositorysMainGetter.repo8BonVent.datasValue
                    )

                    if (generatedAchats.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Aucun achat à ajouter",
                            Toast.LENGTH_SHORT
                        ).show()
                        onDismissDropdown()
                        return@launch
                    }

                    var addedCount = 0
                    val repo13TarificationInfos = repositorysMainGetter.repo13TarificationInfos
                    val currentApp_ItsWorkChezGrossisst = focusedValuesGetter.currentApp_ItsWorkChezGrossisst

                    // For each generated purchase, create or update a vent operation in the current bon vent
                    generatedAchats.forEach { achat ->
                        // Get the color info
                        val couleur = repositorysMainGetter.repo03CouleurProduitInfos.datasValue
                            .find { it.keyID == achat.parent_M3CouleurProduit_KeyID }

                        if (couleur != null) {
                            val existingVent = repositorysMainGetter.repo10OperationVentCouleur.datasValue
                                .find {
                                    it.parent_M8BonVent_KeyId == currentBonVent.keyID &&
                                            it.parent_M3CouleurProduit_KeyID == couleur.keyID
                                }

                            val quantityToAdd = achat.sumAchatQantity

                            if (existingVent != null) {
                                // Update existing vent operation
                                val updatedVent = existingVent.copy(
                                    quantity = existingVent.quantity + quantityToAdd,
                                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                )
                                repositorysMainGetter.repo10OperationVentCouleur.addOrUpdateData(updatedVent)
                            } else {
                                // Get the product info
                                val produit = repositorysMainGetter.repo1ProduitInfos.datasValue
                                    .find { it.keyID == achat.parent_M1Produit_KeyID }

                                if (produit != null) {
                                    val existingTariff = repo13TarificationInfos.datasValue.find { tariff ->
                                        tariff.parent_M1Produit_KeyId == produit.keyID &&
                                                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros
                                    }

                                    // Determine or create the tariff first
                                    val tariffToUse = if (existingTariff != null) {
                                        existingTariff
                                    } else {
                                        // Create Tariff_ItsWorkInGrossist_SuperGros
                                        val newTariff = M13TarificationInfos(
                                            typeChoisi = M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
                                            prixCurrency = achat.prix_Achat_De_Cette_Grossist,
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
                            }

                            addedCount++
                        }
                    }

                    Toast.makeText(
                        context,
                        "Ajouté $addedCount achat(s) de la période au bon de vente",
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
