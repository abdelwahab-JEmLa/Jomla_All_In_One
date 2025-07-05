package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.B1CouleurOuGoutProduitDataBase
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import androidx.compose.ui.graphics.ColorMatrix
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class ViewModelsProduit_T1(
    val aCentral: ACentralFacade,
) : ViewModel() {
    val getter = aCentral.getter
    val focusedVarsHandlerFacade = aCentral.focusedVarsHandlerFacade
    val b1CouleurOuGoutProduitDataBaseRepository = getter.b1CouleurOuGoutProduitDataBaseRepository
    val fVentCouleurOperationRepository = getter.repo10OperationVentCouleur
    val setter = aCentral.setter

    data class UiState(
        val filterNonTrouve: Boolean = true,
        val quantityDialogStates: Map<String, Boolean> = emptyMap(),
        val productDialogStates: Map<String, Boolean> = emptyMap()
    )

    data class ViewVentUIState(
        val ventKey: String = "",
        val quantity: Int = 0,
        val showDialog: Boolean = false,
        val isRemoved: Boolean = false,
        val itemAlpha: Float = 1.0f,
        val colorMatrix: ColorMatrix? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun showQuantityDialog(ventKey: String) = _uiState.update {
        it.copy(quantityDialogStates = it.quantityDialogStates + (ventKey to true))
    }

    fun hideQuantityDialog(ventKey: String) = _uiState.update {
        it.copy(quantityDialogStates = it.quantityDialogStates + (ventKey to false))
    }

    fun showProductDialog(productKey: String) = _uiState.update {
        it.copy(productDialogStates = it.productDialogStates + (productKey to true))
    }

    fun hideProductDialog(productKey: String) = _uiState.update {
        it.copy(productDialogStates = it.productDialogStates + (productKey to false))
    }

    fun calculateUIState(existingVent: FCouleurVentOperationInfos?, uiState: UiState): ViewVentUIState {
        val ventKey = existingVent?.keyID ?: ""
        val isRemoved = existingVent?.etateActuellementEst == FCouleurVentOperationInfos.EtateActuellementEst.SUPP_AU_PANIER_FINALE
        return ViewVentUIState(
            ventKey = ventKey,
            quantity = existingVent?.quantityAchete ?: 0,
            showDialog = ventKey.isNotEmpty() && (uiState.quantityDialogStates[ventKey] ?: false),
            isRemoved = isRemoved,
            itemAlpha = if (isRemoved) 0.4f else 1.0f,
            colorMatrix = if (isRemoved) ColorMatrix().apply { setToSaturation(0f) } else null
        )
    }

    fun calculateExistingVent(produit: ArticlesBasesStatsTable?, color: B1CouleurOuGoutProduitDataBase) =
        fVentCouleurOperationRepository.datasValue.find {
            it.parentM1ProduitInfosKeyId == produit?.keyID && it.parentM3CouleurProduitInfosKeyID == color.key
        }

    fun createDefaultVent(color: B1CouleurOuGoutProduitDataBase, produit: ArticlesBasesStatsTable?, appCompt: Z_AppCompt?, onVentData: GBonVent) =
        FCouleurVentOperationInfos(
            keyID = "vent_${color.key}_${produit?.keyID}",
            parentZAppComptID = extractField(appCompt, "keyID") ?: "Non Definie",
            parentDebugInfosID9AppCompt = extractField(appCompt, "nom") ?: "Non Definie",
            parentHVentPeriodKeyId = ParametresAppComptNonSaved().keyIdId7VentPeriod,
            parentDebugInfosID7VentPeriod = ParametresAppComptNonSaved().debugNameId7VentPeriod,
            parentM8BonVentKeyId = extractField(onVentData, "keyID") ?: "",
            parentM8BonVentDebugInfos = extractField(onVentData, "nomClientConcerned") ?: "",
            parentM1ProduitInfosKeyId = produit?.keyID ?: "",
            parentM1ProduitDebugInfos = produit?.nom ?: "Non Definie",
            parentM3CouleurProduitInfosKeyID = color.key,
            parentBProduitNomDebug = produit?.nom ?: "",
            parentProduitInfosOldId = produit?.id ?: 0L,
            parentClientName = extractField(appCompt, "nom") ?: "Non Definie",
            quantityAchete = 0,
            etateActuellementEst = FCouleurVentOperationInfos.EtateActuellementEst.CreeSlote
        )

    private fun extractField(obj: Any?, fieldName: String): String? = try {
        obj?.javaClass?.getDeclaredField(fieldName)?.apply { isAccessible = true }?.get(obj) as? String
    } catch (e: Exception) { null }

    fun getImageFile(nomImageFichieSansEtansion: String, extensionDisponible: String): File? =
        if (nomImageFichieSansEtansion != "Non Dispo")
            File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne", "$nomImageFichieSansEtansion.$extensionDisponible")
        else null

    fun getTotalQuantity(relatedVents: List<FCouleurVentOperationInfos>) = relatedVents.sumOf { it.quantityAchete }

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

    fun allNonTrouve(relatedVents: List<FCouleurVentOperationInfos>) = relatedVents.isNotEmpty() && relatedVents.all { it.etateDelivery == FCouleurVentOperationInfos.EtateDelivery.NonTrouve }
}
