package Y_AppsFather.Kotlin.Model

import Y_AppsFather.Kotlin.BonType
import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel
import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel.GrossistBonCommandes
import Y_AppsFather.Kotlin.ViewModelInitApp
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Objects

@IgnoreExtraProperties
open class _ModelAppsFather(
    initial_Produits_Main_DataBase: List<ProduitModel> = emptyList()
) {
    @get:Exclude
    var produitsMainDataBase: SnapshotStateList<ProduitModel> =
        initial_Produits_Main_DataBase.toMutableStateList()

    @IgnoreExtraProperties
    class ProduitModel(
        var id: Long = 0,
        var itsTempProduit: Boolean = false,
        init_nom: String = "",
        init_besoin_To_Be_Updated: Boolean = false,
        initialNon_Trouve: Boolean = false,
        init_colours_Et_Gouts: List<ColourEtGout_Model> = emptyList(),
        init_bonCommendDeCetteCota: GrossistBonCommandes? = null,
        initBonsVentDeCetteCota: List<ClientBonVentModel> = emptyList(),
        init_visible: Boolean = true,
        init_historiqueBonsVents: List<ClientBonVentModel> = emptyList(),
        init_historiqueBonsCommend: List<GrossistBonCommandes> = emptyList(),
    ) {
        var nom: String by mutableStateOf(init_nom)
        var besoinToBeUpdated: Boolean by mutableStateOf(init_besoin_To_Be_Updated)
        var non_Trouve: Boolean by mutableStateOf(initialNon_Trouve)
        var isVisible: Boolean by mutableStateOf(init_visible)

        var statuesBase: StatuesBase by mutableStateOf(StatuesBase())

        @get:Exclude
        var coloursEtGouts: SnapshotStateList<ColourEtGout_Model> =
            init_colours_Et_Gouts.toMutableStateList()

        var coloursEtGoutsList: List<ColourEtGout_Model>
            get() = coloursEtGouts.toList()
            set(value) {
                coloursEtGouts.clear()
                coloursEtGouts.addAll(value)
            }

        var bonCommendDeCetteCota: GrossistBonCommandes? by mutableStateOf(
            init_bonCommendDeCetteCota
        )

        @get:Exclude
        var bonsVentDeCetteCota: SnapshotStateList<ClientBonVentModel> =
            initBonsVentDeCetteCota.toMutableStateList()

        var bonsVentDeCetteCotaList: List<ClientBonVentModel>
            get() = bonsVentDeCetteCota.toList()
            set(value) {
                bonsVentDeCetteCota.clear()
                bonsVentDeCetteCota.addAll(value)
            }

        @get:Exclude
        var historiqueBonsVents: SnapshotStateList<ClientBonVentModel> =
            init_historiqueBonsVents.toMutableStateList()

        var historiqueBonsVentsList: List<ClientBonVentModel>
            get() = historiqueBonsVents.toList()
            set(value) {
                historiqueBonsVents.clear()
                historiqueBonsVents.addAll(value)
            }

        @get:Exclude
        var historiqueBonsCommend: SnapshotStateList<GrossistBonCommandes> =
            init_historiqueBonsCommend.toMutableStateList()

        var historiqueBonsCommendList: List<GrossistBonCommandes>
            get() = historiqueBonsCommend.toList()
            set(value) {
                historiqueBonsCommend.clear()
                historiqueBonsCommend.addAll(value)
            }

        @IgnoreExtraProperties
        class StatuesBase(
            var ilAUneCouleurAvecImage: Boolean = false,
        ) {
            var naAucunImage: Boolean by mutableStateOf(false)
            var sonImageBesoinActualisation: Boolean by mutableStateOf(false)
            var imageGlidReloadTigger: Int by mutableStateOf(0)
            var prePourCameraCapture: Boolean by mutableStateOf(false)
        }

        @IgnoreExtraProperties
        class ColourEtGout_Model(
            var position_Du_Couleur_Au_Produit: Long = 0,
            var nom: String = "",
            var imogi: String = "",
            var sonImageNeExistPas: Boolean = false,
        )

        @IgnoreExtraProperties
        class GrossistBonCommandes(
            var vid: Long = 0,
            init_grossistInformations: GrossistInformations? = null,
            var date: String = "",
            var date_String_Divise: String = "",
            var time_String_Divise: String = "",
            var currentCreditBalance: Double = 0.0,
            init_coloursEtGoutsCommendee: List<ColoursGoutsCommendee> = emptyList(),
        ) {
            var grossistInformations: GrossistInformations? by mutableStateOf(
                init_grossistInformations
            )

            var cPositionCheyCeGrossit: Boolean by mutableStateOf(false)
            var positionProduitDonGrossistChoisiPourAcheterCeProduit: Int
                    by mutableStateOf(0)

            @get:Exclude
            var coloursEtGoutsCommendee: SnapshotStateList<ColoursGoutsCommendee> =
                init_coloursEtGoutsCommendee.toMutableStateList()


            @IgnoreExtraProperties
            data class GrossistInformations(
                val id: Long = 0,
                val nom: String = "",
                val couleur: String = ""
            ) {
                var auFilterFAB: Boolean by mutableStateOf(false)
                var positionInGrossistsList: Int
                        by mutableIntStateOf(0)
            }

            @IgnoreExtraProperties
            class ColoursGoutsCommendee(
                val id: Long = 0,
                val nom: String = "",
                val emoji: String = "",
            ) {
                var quantityAchete: Int by mutableIntStateOf(0)
            }

            companion object {
                fun updateSelf(
                    produit: ProduitModel,
                    bonCommande: GrossistBonCommandes,
                    viewModelProduits: ViewModelInitApp
                ) {
                    produit.bonCommendDeCetteCota = bonCommande
                    val index =
                        viewModelProduits._modelAppsFather.produitsMainDataBase.indexOfFirst { it.id == produit.id }
                    if (index != -1) {
                        // Direct update of the SnapshotStateList
                        viewModelProduits._modelAppsFather.produitsMainDataBase[index] = produit
                    }
                }

                fun calculeSelf(initViewModel: ViewModelInitApp, product: ProduitModel) {
                    initViewModel._modelAppsFather.produitsMainDataBase
                        .filter { it.id == product.id }
                        .forEach { produit ->
                            try {
                                // Create new GrossistBonCommandes with proper initialization
                                val newBonCommande = GrossistBonCommandes().apply {
                                    vid = System.currentTimeMillis()
                                    grossistInformations = GrossistInformations(
                                        id = vid,
                                        nom = "Non Defini",
                                        couleur = "#FF0000"
                                    ).apply {
                                        auFilterFAB = false
                                        positionInGrossistsList = 0
                                    }

                                    // Clear existing commendee list to prevent duplicates
                                    coloursEtGoutsCommendee.clear()

                                    // Group and calculate quantities with null safety
                                    val groupedColors = produit.bonsVentDeCetteCota
                                        .flatMap { bonVente ->
                                            bonVente.colours_Achete.map { it }
                                        }
                                        .groupBy { it.nom }

                                    // Process each color group
                                    groupedColors.forEach { (colorName, colorList) ->
                                        colorList.firstOrNull()?.let { firstColor ->
                                            // Create new commendee with proper initialization
                                            val newCommendee = ColoursGoutsCommendee(
                                                id = firstColor.vidPosition,
                                                nom = colorName,
                                                emoji = firstColor.imogi
                                            ).apply {
                                                // Calculate total quantity for this color
                                                quantityAchete = colorList.sumOf { it.quantity_Achete }
                                            }

                                            // Add to the list only if quantity > 0
                                            if (newCommendee.quantityAchete > 0) {
                                                coloursEtGoutsCommendee.add(newCommendee)
                                            }
                                        }
                                    }
                                }

                                // Update the product with new bon commande
                                produit.bonCommendDeCetteCota = newBonCommande

                                // Update Firebase with error handling
                                try {
                                    produitsFireBaseRef.child(produit.id.toString())
                                        .child("bonCommendDeCetteCota")
                                        .setValue(newBonCommande)
                                        .addOnFailureListener { exception ->
                                            Log.e("calculeSelf", "Firebase update failed", exception)
                                        }
                                } catch (e: Exception) {
                                    Log.e("calculeSelf", "Firebase update error", e)
                                }

                            } catch (e: Exception) {
                                Log.e("calculeSelf", "Calculation error for product ${produit.id}", e)
                            }
                        }
                }
            }
        }

        @IgnoreExtraProperties
        class ClientBonVentModel(
            vid: Long = 0,
            init_clientInformations: ClientInformations? = null,
            init_colours_achete: List<ColorAchatModel> = emptyList(),
        ) {
            var clientInformations: ClientInformations? by mutableStateOf(init_clientInformations)

            @get:Exclude
            var colours_Achete: SnapshotStateList<ColorAchatModel> =
                init_colours_achete.toMutableStateList()

            var coloursAcheteList: List<ColorAchatModel>
                get() = colours_Achete.toList()
                set(value) {
                    colours_Achete.clear()
                    colours_Achete.addAll(value)
                }

            @IgnoreExtraProperties
            data class ClientInformations(
                var id: Long = 0,
                var nom: String = "",
                val couleur: String = ""
            ) {
                var auFilterFAB: Boolean by mutableStateOf(false)
                var positionDonClientsList: Int
                        by mutableIntStateOf(0)

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (other !is ClientInformations) return false
                    return id == other.id &&
                            nom == other.nom &&
                            couleur == other.couleur
                }

                override fun hashCode(): Int {
                    return Objects.hash(id, nom, couleur)
                }

            }

            @IgnoreExtraProperties
            class ColorAchatModel(
                var vidPosition: Long = 0,
                var nom: String = "",
                var quantity_Achete: Int = 0,
                var imogi: String = ""
            )
            companion object {
                fun updateSelf(
                    produit: ProduitModel,
                    bonVente: ClientBonVentModel ,
                    viewModelProduits: ViewModelInitApp
                ) {
                    produit.bonsVentDeCetteCota.removeAll { it.clientInformations?.id == bonVente.clientInformations?.id }
                    produit.bonsVentDeCetteCota.add(bonVente)
                    val index =
                        viewModelProduits._modelAppsFather.produitsMainDataBase.indexOfFirst { it.id == produit.id }
                    if (index != -1) {
                        // Direct update of the SnapshotStateList
                        viewModelProduits._modelAppsFather.produitsMainDataBase[index] = produit
                    }
                }
            }
        }
    }

    companion object {
        val produitsFireBaseRef = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("produits")

        val imagesProduitsFireBaseStorageRef = Firebase.storage.reference
            .child("Images Articles Data Base")
            .child("produits")

        const val imagesProduitsLocalExternalStorageBasePath =
            "/storage/emulated/0/" +
                    "Abdelwahab_jeMla.com" +
                    "/IMGs" +
                    "/BaseDonne"


        fun update_produitsAvecBonsGrossist(
            updatedProducts: List<ProduitModel>, // Change parameter type to List
            viewModelProduits: ViewModelInitApp
        ) {
            viewModelProduits.viewModelScope.launch {
                try {
                    // Update local state
                    viewModelProduits._produitsAvecBonsGrossist.clear()
                    viewModelProduits._produitsAvecBonsGrossist.addAll(updatedProducts)

                    // Then update Firebase in chunks to prevent overwhelming the connection
                    UpdateFireBase(updatedProducts)
                } catch (e: Exception) {
                    Log.e("Firebase", "Error updating products", e)
                    throw e
                }
            }
        }

        suspend fun UpdateFireBase(updatedProducts: List<ProduitModel>) {
            updatedProducts.chunked(5).forEach { chunk ->
                chunk.forEach { product ->
                    try {
                        produitsFireBaseRef.child(product.id.toString()).setValue(product)
                            .await()
                        Log.d("Firebase", "Successfully updated product ${product.id}")
                    } catch (e: Exception) {
                        Log.e("Firebase", "Failed to update product ${product.id}", e)
                    }
                }
            }
        }

        fun collectBonType(
            viewModel: ViewModelInitApp,
            scope: CoroutineScope
        ) {
            scope.launch {
                viewModel.bonTypeFlow.collect { bonType ->
                    when (bonType) {
                        is BonType.BonVente -> {
                            // Handle bon vente updates
                            val bonVente = bonType.data
                            viewModel._modelAppsFather.produitsMainDataBase
                                .filter { it.bonsVentDeCetteCota.any { bv -> bv.clientInformations?.id == bonVente.clientInformations?.id } }
                                .forEach { produit ->
                                    ClientBonVentModel.updateSelf(produit, bonVente,viewModel)
                                    GrossistBonCommandes.calculeSelf(viewModel, produit)
                                }
                        }

                        is BonType.BonCommande -> {
                            // Handle bon commande updates
                            val bonCommande = bonType.data
                            viewModel._modelAppsFather.produitsMainDataBase
                                .filter { it.bonCommendDeCetteCota?.grossistInformations?.id == bonCommande.grossistInformations?.id }
                                .forEach { produit ->
                                    GrossistBonCommandes.updateSelf(produit, bonCommande, viewModel)
                                }
                        }

                        null -> {}
                    }
                }
            }
        }




        fun updateProduit(
            product: ProduitModel,
            viewModelProduits: ViewModelInitApp
        ) {
            viewModelProduits.viewModelScope.launch {
                try {
                    // Update Firebase
                    produitsFireBaseRef.child(product.id.toString()).setValue(product).await()

                    // Update _produitsAvecBonsGrossist
                    val index =
                        viewModelProduits._modelAppsFather.produitsMainDataBase.indexOfFirst { it.id == product.id }
                    if (index != -1) {
                        // Direct update of the SnapshotStateList
                        viewModelProduits._modelAppsFather.produitsMainDataBase[index] = product
                    }

                    Log.d("ViewModelInitApp", "Successfully updated product ${product.id}")
                } catch (e: Exception) {
                    Log.e("ViewModelInitApp", "Failed to update product ${product.id}", e)
                }
            }
        }
    }
}
