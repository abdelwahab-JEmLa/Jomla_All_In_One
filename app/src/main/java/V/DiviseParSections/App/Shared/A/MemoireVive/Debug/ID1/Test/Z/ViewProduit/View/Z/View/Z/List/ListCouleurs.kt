package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List.UI.ViewVentCouleur_T1
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B1CouleurOuGoutProduitDataBase
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun ListCouleurs(
    produit: ArticlesBasesStatsTable?,
    relatedVents: List<FCouleurVentOperationInfos>,
    viewModel: ViewModelsProduit_T1,
    allNonTrouve: Boolean
) {
    // FIXED: Use remember with explicit type and direct derivedStateOf call
    val allAvailableColors = remember(produit?.id) {
        derivedStateOf {
            produit?.let { product ->
                viewModel.getter.b1CouleurOuGoutProduitDataBaseRepository.datasValue
                    .filter { it.parentBProduitOldID == product.id }
            } ?: emptyList()
        }
    }.value

    // Create a map of color key to existing vent for quick lookup
    val ventsByColorKey = remember(relatedVents) {
        derivedStateOf {
            relatedVents.associateBy { it.parentCouleurInfosKeyID }
        }
    }.value

    // CORRECTION: Si aucune couleur n'est disponible, créer une couleur par défaut
    val colorsToDisplay = remember(allAvailableColors, produit) {
        derivedStateOf {
            if (allAvailableColors.isEmpty() && produit != null) {
                // Créer une couleur par défaut basée sur le produit
                listOf(
                    B1CouleurOuGoutProduitDataBase(
                        key = "default_${produit.keyID}",
                        parentBProduitOldID = produit.id,
                        parentBProduitNom = produit.nom,
                        parentBProduitInfosKeyID = produit.keyID,
                        nomCouleurStrSiSonImageDispo = produit.nom,
                        aAffiche = B1CouleurOuGoutProduitDataBase.Type.Nom,
                        nomImageFichieSansEtansion = "Non Dispo"
                    )
                )
            } else {
                allAvailableColors
            }
        }
    }.value

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(colorsToDisplay, key = { it.key }) { color ->
            // Find existing vent for this color, or create a default one
            val existingVent = ventsByColorKey[color.key]
            val ventToDisplay = existingVent ?: run {
                // Create a default vent for colors that haven't been purchased yet
                val currentAppCompt = viewModel.getter.zAppComptRepositoryComposable.currentAppCompt
                val onVentData = viewModel.getter.gBonVentRepository.onVentData

                FCouleurVentOperationInfos(
                    keyID = "vent_${color.key}",
                    parentCouleurInfosKeyID = color.key,
                    parentBProduitInfosKeyId = produit?.keyID ?: "",
                    parentBProduitNomDebug = produit?.nom ?: "",
                    parentProduitInfosOldId = produit?.id ?: 0,
                    parentZAppComptID = currentAppCompt?.keyID ?: "",
                    parentClientName = currentAppCompt?.nom ?: "",
                    parentHVentPeriodKeyId = currentAppCompt?.onVentHVentPeriodKeyId ?: "",
                    parentGBonVentKeyId = currentAppCompt?.onVentGBonVentKeyId ?: "",
                    quantityAchete = 0, // Default to 0 for new colors
                    etateActuellementEst = FCouleurVentOperationInfos.EtateActuellementEst.CreeSlote,
                    // CORRECTION: S'assurer que les champs requis sont définis
                    parentDebugInfosID9AppCompt = currentAppCompt?.nom ?: "Non Definie",
                    parentDebugInfosID7VentPeriod = "Non Definie",
                    parentDebugInfosID8BonVent = onVentData.nomClientConcerned,
                    parentDebugInfosID1Produit = produit?.nom ?: "Non Definie"
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                tonalElevation = if (allNonTrouve) 1.dp else 2.dp,
                modifier = Modifier
                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
                    .graphicsLayer(
                        alpha = if (ventToDisplay.etateDelivery == FCouleurVentOperationInfos.EtateDelivery.NonTrouve) 0.5f else 1.0f
                    )
            ) {
                ViewVentCouleur_T1(
                    produit = produit,
                    modifier = Modifier.padding(4.dp),
                    ventKey = ventToDisplay.keyID,
                    size = 120.dp,
                    purchasedQuantity = ventToDisplay.quantityAchete,
                    viewModel = viewModel
                )
            }
        }
    }
}
