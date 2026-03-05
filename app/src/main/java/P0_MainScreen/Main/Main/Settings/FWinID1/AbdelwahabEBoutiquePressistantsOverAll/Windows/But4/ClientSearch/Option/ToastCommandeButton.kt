package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Models.M13TarificationInfos
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
@Composable
fun ToastCommandeButton(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter
) {
    val repo8BonVent = repositorysMainGetter.repo8BonVent
    val repo10OperationVentCouleur = repositorysMainGetter.repo10OperationVentCouleur
    val repo2Client = repositorysMainGetter.repo2Client
    val repo3CouleurProduitInfos = repositorysMainGetter.repo03CouleurProduitInfos
    val repo13TarificationInfos = repositorysMainGetter.repo13TarificationInfos

    val currentActiveFocuced_M14VentPeriode =
        focusedValuesGetter.currentActiveFocuced_M14VentPeriode
    val client_JamelBel = repo2Client.datasValue.find { it.nom == "Jamel Bel" }
    val currentAppCompt = repositorysMainGetter.repo9AppCompt.currentAppCompt

    val context = LocalContext.current

    FloatingActionButton(
        modifier = Modifier.size(32.dp),
        onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Find or validate Jamel Bel client
                    val jamelBelClient = client_JamelBel
                    if (jamelBelClient == null) {
                        launch(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Client 'Jamel Bel' non trouvé",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return@launch
                    }

                    // Validate current vent period
                    val ventPeriode = currentActiveFocuced_M14VentPeriode
                    if (ventPeriode == null) {
                        launch(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Aucune période de vente active",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return@launch
                    }

                    // Create new M8BonVent for Jamel Bel
                    val newBonVent = focusedValuesGetter.getDefaultM8BonVent().copy(
                        parent_M2Client_KeyID = jamelBelClient.keyID,
                        parent_M14VentPeriod_KeyId = ventPeriode.keyID,
                        etateActuellementEst = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                        its_working_for_wholesaler = true
                    )
                    // Add the bon vent to repository
                    repo8BonVent.upsert(newBonVent)

                    // Update app compt to focus on this bon vent
                    val updatedAppCompt = currentAppCompt?.copy(
                        onVentM8BonVentKey = newBonVent.keyID,
                        onVentM8BonVentDebugInfos = newBonVent.get_DebugInfos()
                    )

                    if (updatedAppCompt != null) {
                        aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.upsert_M8BonVent_Et_Focuce_Le_Au_M9CurrCompt(
                            newBonVent,
                            updatedAppCompt
                        )
                    }

                    // Get products from current vent period that are non_places_au_depot or pas_Dispo_Pour_Aujourduit
                    val allVentsInPeriod = repo10OperationVentCouleur.datasValue.filter { vent ->
                        vent.parent_M14VentPeriod_KeyId == ventPeriode.keyID &&
                                !(vent.non_places_au_depot || vent.pas_Dispo_Pour_Aujourduit)
                                && !vent.its_created_in_working_for_wholesaler
                                && vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve
                    }

                    // FIXED: Group by product first, then generate comment for each product's colors
                    val ventsByProduct = allVentsInPeriod.groupBy { it.parent_M1Produit_KeyId }

                    ventsByProduct.forEach { (productKeyId, ventsForProduct) ->
                        // Generate comment specific to this product's colors
                        val productSpecificComment = generateCommentForProductColors(
                            ventsForProduct = ventsForProduct,
                            productKeyId = productKeyId,
                            repositorysMainGetter = repositorysMainGetter
                        )

                        // Group this product's vents by color
                        val ventsByColor = ventsForProduct.groupBy { it.parent_M3CouleurProduit_KeyID }

                        ventsByColor.forEach { (couleurKeyId, ventsForColor) ->
                            val totalQuantity = ventsForColor.sumOf { it.quantity }
                            if (totalQuantity > 0 && couleurKeyId.isNotBlank() && couleurKeyId != "null") {

                                // Get couleur info
                                val couleurInfo = repo3CouleurProduitInfos.datasValue.find {
                                    it.keyID == couleurKeyId
                                }

                                if (couleurInfo != null) {
                                    // Get the product info to find the SuperGros tariff
                                    val productInfo = repositorysMainGetter.find_M1Produit_ByKeyID(productKeyId)

                                    // Find existing SuperGros tariff for this product
                                    var superGrosTariff = repo13TarificationInfos.datasValue
                                        .filter { tariff ->
                                            tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros &&
                                                    tariff.parent_M1Produit_KeyId == productKeyId
                                        }
                                        .maxByOrNull { it.dernierTimeTampsSynchronisationAvecFireBase }

                                    // If SuperGros tariff doesn't exist, create and add it
                                    if (superGrosTariff == null && productInfo != null) {
                                        superGrosTariff = M13TarificationInfos(
                                            parent_M14VentPeriod_KeyId = ventPeriode.keyID,
                                            typeChoisi = M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
                                            prixCurrency = productInfo.prixAchat,
                                            parent_M1Produit_KeyId = productInfo.keyID,
                                            parent_M1Produit_DebugInfos = productInfo.nom,
                                            creationTimestamps = System.currentTimeMillis()
                                        )
                                        // Add the new tariff to the repository
                                        repo13TarificationInfos.add(superGrosTariff)
                                    }

                                    // Determine the price to use
                                    val priceToUse = superGrosTariff?.prixCurrency ?: productInfo?.prixAchat ?: 0.0

                                    // Create new vent operation for Jamel Bel with SuperGros pricing
                                    val newVentOperation = M10OperationVentCouleur.get_default_By_BonVentEtCouleur(
                                        newBonVent,
                                        couleurInfo
                                    ).copy(
                                        quantity = totalQuantity,
                                        etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ChoisiQuantityConfirme,
                                        etateDelivery = M10OperationVentCouleur.EtateDelivery.Trouve,
                                        type = M10OperationVentCouleur.Type.CommandeDeLui,
                                        typeTarificationEnumT2 = M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros, // Using SuperGros tariff type
                                        provisoireMonPrix = priceToUse, // Set the SuperGros price
                                        parentClientInfosKeyID = jamelBelClient.keyID,
                                        parentClientName = jamelBelClient.nom,
                                        parent_M9AppCompt_KeyID = currentAppCompt?.keyID ?: "null",
                                        parent_M9AppCompt_DebugInfos = currentAppCompt?.get_DebugInfos() ?: "null",
                                        parent_M14VentPeriod_KeyId = currentActiveFocuced_M14VentPeriode.keyID,
                                        its_created_in_working_for_wholesaler = true,
                                        commetaire = productSpecificComment // Comment specific to this product only
                                    )

                                    // Add to repository
                                    repo10OperationVentCouleur.add_New(newVentOperation)
                                }
                            }
                        }
                    }

                    // Show success message
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Bon Commande créé pour Jamel Bel avec ${ventsByProduct.size} produits",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Erreur lors de création: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        },
        containerColor = Color(0xFF4CAF50)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Créer Bon Commande",
            tint = Color.White
        )
    }
}

// FIXED: Generate comment for a specific product's colors only (without product name)
private fun generateCommentForProductColors(
    ventsForProduct: List<M10OperationVentCouleur>,
    productKeyId: String,
    repositorysMainGetter: RepositorysMainGetter
): String {
    val repo3CouleurProduitInfos = repositorysMainGetter.repo03CouleurProduitInfos
    val repo8BonVent = repositorysMainGetter.repo8BonVent
    val repo2Client = repositorysMainGetter.repo2Client

    // Group by color for this specific product
    val ventsByColor = ventsForProduct.groupBy { it.parent_M3CouleurProduit_KeyID }

    val colorComments = mutableListOf<String>()

    ventsByColor.forEach { (couleurKeyId, ventsForColor) ->
        if (couleurKeyId.isNotBlank() && couleurKeyId != "null") {
            // Get couleur info
            val couleurInfo = repo3CouleurProduitInfos.datasValue.find {
                it.keyID == couleurKeyId
            }

            if (couleurInfo != null) {
                val couleurName = couleurInfo.nomCouleurStrSiSonImageDispo
                val totalQuantityForColor = ventsForColor.sumOf { it.quantity }

                // Group by client for this color
                val clientQuantities = mutableMapOf<String, Int>()

                ventsForColor.forEach { vent ->
                    // Find the bon vent to get client info
                    val bonVent = repo8BonVent.datasValue.find {
                        it.keyID == vent.parent_M8BonVent_KeyId
                    }

                    if (bonVent != null) {
                        // Find the client
                        val client = repo2Client.datasValue.find {
                            it.keyID == bonVent.parent_M2Client_KeyID
                        }

                        if (client != null) {
                            val clientName = client.nom
                            clientQuantities[clientName] = (clientQuantities[clientName] ?: 0) + vent.quantity
                        }
                    }
                }

                // Build comment for this color
                val clientDetails = clientQuantities.entries.joinToString(" ") { (clientName, quantity) ->
                    "${clientName}($quantity)"
                }

                colorComments.add("$couleurName=$totalQuantityForColor[$clientDetails]")
            }
        }
    }

    // Return comment without product name - just the colors with line breaks
    return colorComments.joinToString("\n\n\n")
}
