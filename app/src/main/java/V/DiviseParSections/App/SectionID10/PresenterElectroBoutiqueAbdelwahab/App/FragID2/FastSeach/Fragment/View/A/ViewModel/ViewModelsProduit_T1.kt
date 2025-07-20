package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import androidx.compose.ui.graphics.ColorMatrix
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class ViewModelsProduit_T1(
    val aCentralFacade: ACentralFacade,
) : ViewModel() {
    val getter = aCentralFacade.repositorysMainGetter
    val focusedVarsHandlerFacade = aCentralFacade.focusedActiveValuesFacade

    val getterFocusedVarsHandlerFacade =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val setterFocusedVarsHandlerFacade = focusedVarsHandlerFacade.focusedValuesSetter

    val b1CouleurOuGoutProduitDataBaseRepository = getter.repo03CouleurProduitInfos
    val fVentCouleurOperationRepository = getter.repo10OperationVentCouleur

    data class UiState(
        val filterNonTrouve: Boolean = true,
    )


    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    data class ViewVentUIState(
        val ventKey: String = "",
        val quantity: Int = 0,
        val isRemoved: Boolean = false,
        val itemAlpha: Float = 1.0f,
        val colorMatrix: ColorMatrix? = null
    )
    fun calculateUIState(
        produit: ArticlesBasesStatsTable,
        existingVent: M10OperationVentCouleur?,
        uiState: UiState
    ): ViewVentUIState {
        val ventKey = existingVent?.keyID ?: ""
        val isRemoved =
            existingVent?.etateActuellementEst == M10OperationVentCouleur.EtateActuellementEst.SUPP_AU_PANIER_FINALE

        val existingVentQ = existingVent?.quantity ?: 0
        val quantity = if (produit.setIN_Vent_Its_Quantity_Represent ==
            M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton
        ) {
            existingVentQ * produit.quantite_Boit_Par_Carton
        } else {
            existingVentQ
        }

        return ViewVentUIState(
            ventKey = ventKey,
            quantity = quantity,
            isRemoved = isRemoved,
            itemAlpha = if (isRemoved) 0.4f else 1.0f,
            colorMatrix = if (isRemoved) ColorMatrix().apply { setToSaturation(0f) } else null
        )
    }

    fun calculateExistingVent(produit: ArticlesBasesStatsTable?, color: M3CouleurProduitInfos) =
        fVentCouleurOperationRepository.datasValue.find {
            it.parent_M1Produit_KeyId == produit?.keyID && it.parent_M3CouleurProduit_KeyID == color.keyID
        }


    private fun extractField(obj: Any?, fieldName: String): String? = try {
        obj?.javaClass?.getDeclaredField(fieldName)?.apply { isAccessible = true }
            ?.get(obj) as? String
    } catch (e: Exception) {
        null
    }

    fun getImageFile(nomImageFichieSansEtansion: String, extensionDisponible: String): File? =
        if (nomImageFichieSansEtansion != "Non Dispo")
            File(
                "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne",
                "$nomImageFichieSansEtansion.$extensionDisponible"
            )
        else null

    fun getTotalQuantity(relatedVents: List<M10OperationVentCouleur>) =
        relatedVents.sumOf { it.quantity }

    fun getProductName(produit: Any?, productKeyId: String): String {
        val nom = produit?.let {
            try {
                val nomField = it.javaClass.getDeclaredField("nom").apply { isAccessible = true }
                (nomField.get(it) as? String)?.takeIf { it.isNotBlank() }
                    ?: it.javaClass.getDeclaredField("nomMutable").apply { isAccessible = true }
                        .get(it) as? String
            } catch (e: Exception) {
                null
            }
        }
        return nom?.takeIf { it.isNotBlank() } ?: "Product #$productKeyId"
    }

    fun allNonTrouve(relatedVents: List<M10OperationVentCouleur>) =
        relatedVents.isNotEmpty() && relatedVents.all { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }
}
