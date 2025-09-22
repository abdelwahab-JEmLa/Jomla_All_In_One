package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
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
                    }

                    // Group by couleur and create new vents for Jamel Bel
                    val ventsByColor = allVentsInPeriod.groupBy { it.parent_M3CouleurProduit_KeyID }

                    ventsByColor.forEach { (couleurKeyId, ventsForColor) ->
                        val totalQuantity = ventsForColor.sumOf { it.quantity }
                        if (totalQuantity > 0 && couleurKeyId.isNotBlank() && couleurKeyId != "null") {

                            // Get couleur info
                            val couleurInfo = repo3CouleurProduitInfos.datasValue.find {
                                it.keyID == couleurKeyId
                            }

                            if (couleurInfo != null) {
                                // Create new vent operation for Jamel Bel
                                val newVentOperation =
                                    M10OperationVentCouleur.get_default_By_BonVentEtCouleur(
                                        newBonVent,
                                        couleurInfo
                                    ).copy(
                                        quantity = totalQuantity,
                                        etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ChoisiQuantityConfirme,
                                        etateDelivery = M10OperationVentCouleur.EtateDelivery.Trouve,
                                        type = M10OperationVentCouleur.Type.CommandeDeLui,
                                        typeTarificationEnumT2 = M13TarificationInfos.TypeChoisi.Prix_Detaille,
                                        parentClientInfosKeyID = jamelBelClient.keyID,
                                        parentClientName = jamelBelClient.nom,
                                        parent_M9AppCompt_KeyID = currentAppCompt?.keyID ?: "null",
                                        parent_M9AppCompt_DebugInfos = currentAppCompt?.get_DebugInfos()
                                            ?: "null" ,
                                        parent_M14VentPeriod_KeyId =currentActiveFocuced_M14VentPeriode.keyID ,
                                        its_created_in_working_for_wholesaler=true
                                    )

                                // Add to repository
                                repo10OperationVentCouleur.add_New(newVentOperation)
                            }
                        }
                    }

                    // Show success message
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Bon Commande créé pour Jamel Bel avec ${ventsByColor.size} produits",
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
