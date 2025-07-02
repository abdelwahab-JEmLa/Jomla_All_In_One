package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
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
    val b1CouleurOuGoutProduitDataBaseRepository =getter.b1CouleurOuGoutProduitDataBaseRepository
    val fVentCouleurOperationRepository= getter.fVentCouleurOperationRepository
    val setter = aCentral.setter

    data class UiState_Sec1Frag3(
        val isMinimized: Boolean = true,
        val panieMode: PanieMode = PanieMode.Delivery,
        val filterNonTrouve: Boolean = true,
    )

    // Dialog states for different components
    data class DialogStates(
        val quantityDialogStates: Map<String, Boolean> = emptyMap(), // ventKey -> showDialog
        val productDialogStates: Map<String, Boolean> = emptyMap()   // productKey -> showDialog
    )

    private val _uiState = MutableStateFlow(UiState_Sec1Frag3())
    val uiState: StateFlow<UiState_Sec1Frag3> = _uiState.asStateFlow()

    private val _dialogStates = MutableStateFlow(DialogStates())
    val dialogStates: StateFlow<DialogStates> = _dialogStates.asStateFlow()

    fun toggleMinimizedState() {
        _uiState.update { currentState ->
            currentState.copy(isMinimized = !currentState.isMinimized)
        }
    }

    enum class PanieMode {
        Delivery,
        Vent;

        fun toggle(): PanieMode {
            return when (this) {
                Delivery -> Vent
                Vent -> Delivery
            }
        }
    }

    fun togglePanieMode() {
        _uiState.update { currentState ->
            currentState.copy(panieMode = currentState.panieMode.toggle())
        }
    }

    fun toggleEtateDeliveryNonTrouveVentOu(produitKey: String) {
        setter.toggleEtateDeliveryNonTrouveVentOu(produitKey)
    }

    fun toggelePanierFilterNonTrouve() {
        _uiState.update { currentState ->
            currentState.copy(filterNonTrouve = !currentState.filterNonTrouve)
        }
    }

    // Dialog management functions
    fun showQuantityDialog(ventKey: String) {
        _dialogStates.update { currentState ->
            currentState.copy(
                quantityDialogStates = currentState.quantityDialogStates + (ventKey to true)
            )
        }
    }

    fun hideQuantityDialog(ventKey: String) {
        _dialogStates.update { currentState ->
            currentState.copy(
                quantityDialogStates = currentState.quantityDialogStates + (ventKey to false)
            )
        }
    }

    fun showProductDialog(productKey: String) {
        _dialogStates.update { currentState ->
            currentState.copy(
                productDialogStates = currentState.productDialogStates + (productKey to true)
            )
        }
    }

    fun hideProductDialog(productKey: String) {
        _dialogStates.update { currentState ->
            currentState.copy(
                productDialogStates = currentState.productDialogStates + (productKey to false)
            )
        }
    }

    // Business logic functions for UI calculations
    fun isVentRemoved(vent: FCouleurVentOperationInfos?): Boolean {
        return vent?.etateActuellementEst == FCouleurVentOperationInfos.EtateActuellementEst.SUPP_AU_PANIER_FINALE
    }

    fun getItemAlpha(isRemoved: Boolean): Float {
        return if (isRemoved) 0.4f else 1.0f
    }

    fun getImageFile(nomImageFichieSansEtansion: String, extensionDisponible: String): File? {
        return if (nomImageFichieSansEtansion != "Non Dispo") {
            File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne",
                "${nomImageFichieSansEtansion}.${extensionDisponible}")
        } else null
    }

    fun getTotalQuantity(relatedVents: List<FCouleurVentOperationInfos>): Int {
        return relatedVents.sumOf { it.quantityAchete }
    }

    fun getProductName(produit: Any?, productKeyId: String): String {
        // Assuming produit has nom and nomMutable properties
        val nom = when {
            produit != null -> {
                // Use reflection or cast to appropriate type to get nom and nomMutable
                // This is a simplified version - you'll need to adapt based on your actual data structure
                try {
                    val nomField = produit.javaClass.getDeclaredField("nom")
                    nomField.isAccessible = true
                    val nom = nomField.get(produit) as? String

                    if (nom?.isNotBlank() == true) {
                        nom
                    } else {
                        val nomMutableField = produit.javaClass.getDeclaredField("nomMutable")
                        nomMutableField.isAccessible = true
                        nomMutableField.get(produit) as? String
                    }
                } catch (e: Exception) {
                    null
                }
            }
            else -> null
        }

        return nom?.takeIf { it.isNotBlank() } ?: "Product #$productKeyId"
    }

    fun getCurrentPrice(relatedVents: List<FCouleurVentOperationInfos>): Double {
        return relatedVents.firstOrNull()?.provisoireMonPrix ?: 0.0
    }

    fun hasNonTrouve(relatedVents: List<FCouleurVentOperationInfos>): Boolean {
        return relatedVents.any { it.etateDelivery == FCouleurVentOperationInfos.EtateDelivery.NonTrouve }
    }

    fun allNonTrouve(relatedVents: List<FCouleurVentOperationInfos>): Boolean {
        return relatedVents.isNotEmpty() && relatedVents.all { it.etateDelivery == FCouleurVentOperationInfos.EtateDelivery.NonTrouve }
    }

    enum class ClickUpdate {
        CouleurQua,
        TotalQua
    }
}
