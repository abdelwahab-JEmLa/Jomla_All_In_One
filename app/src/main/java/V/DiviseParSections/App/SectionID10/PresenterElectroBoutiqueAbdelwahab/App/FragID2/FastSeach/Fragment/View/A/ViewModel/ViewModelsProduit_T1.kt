package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.CentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.M3CouleurProduitInfos
import androidx.compose.ui.graphics.ColorMatrix
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class ViewModelsProduit_T1(
    val aCentral: CentralFacade,
) : ViewModel() {
    val getter = aCentral.get
    val focusedVarsHandlerFacade = aCentral.focusedActiveValuesFacade

    val getterFocusedVarsHandlerFacade = aCentral.focusedActiveValuesFacade.get
    val setterFocusedVarsHandlerFacade = focusedVarsHandlerFacade.set

    val b1CouleurOuGoutProduitDataBaseRepository = getter.repo3CouleurProduitInfos
    val fVentCouleurOperationRepository = getter.repo10OperationVentCouleur

    data class UiState(
        val filterNonTrouve: Boolean = true,
    )

    data class ViewVentUIState(
        val ventKey: String = "",
        val quantity: Int = 0,
        val isRemoved: Boolean = false,
        val itemAlpha: Float = 1.0f,
        val colorMatrix: ColorMatrix? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun calculateUIState(existingVent: M10OperationVentCouleur?, uiState: UiState): ViewVentUIState {
        val ventKey = existingVent?.keyID ?: ""
        val isRemoved = existingVent?.etateActuellementEst == M10OperationVentCouleur.EtateActuellementEst.SUPP_AU_PANIER_FINALE
        return ViewVentUIState(
            ventKey = ventKey,
            quantity = existingVent?.quantityAchete ?: 0,
            isRemoved = isRemoved,
            itemAlpha = if (isRemoved) 0.4f else 1.0f,
            colorMatrix = if (isRemoved) ColorMatrix().apply { setToSaturation(0f) } else null
        )
    }

    fun calculateExistingVent(produit: ArticlesBasesStatsTable?, color: M3CouleurProduitInfos) =
        fVentCouleurOperationRepository.datasValue.find {
            it.parentM1ProduitInfosKeyId == produit?.keyID && it.parentM3CouleurProduitInfosKeyID == color.key
        }


    private fun extractField(obj: Any?, fieldName: String): String? = try {
        obj?.javaClass?.getDeclaredField(fieldName)?.apply { isAccessible = true }?.get(obj) as? String
    } catch (e: Exception) { null }

    fun getImageFile(nomImageFichieSansEtansion: String, extensionDisponible: String): File? =
        if (nomImageFichieSansEtansion != "Non Dispo")
            File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne", "$nomImageFichieSansEtansion.$extensionDisponible")
        else null

    fun getTotalQuantity(relatedVents: List<M10OperationVentCouleur>) = relatedVents.sumOf { it.quantityAchete }

    fun getProductName(produit: Any?, productKeyId: String): String {
        val nom = produit?.let {
            try {
                val nomField = it.javaClass.getDeclaredField("nom").apply { isAccessible = true }
                (nomField.get(it) as? String)?.takeIf { it.isNotBlank() }
                    ?: it.javaClass.getDeclaredField("nomMutable").apply { isAccessible = true }.get(it) as? String
            } catch (e: Exception) { null }
        }
        return nom?.takeIf { it.isNotBlank() } ?: "Product #$productKeyId"
    }

    fun allNonTrouve(relatedVents: List<M10OperationVentCouleur>) = relatedVents.isNotEmpty() && relatedVents.all { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }
}
