package Y_AppsFather.Kotlin

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Objects

@IgnoreExtraProperties
open class ModelAppsFather(
    initial_Produits_Main_DataBase: List<ProduitModel> = emptyList()
) {
    @get:Exclude
    var produitsMainDataBase: SnapshotStateList<ProduitModel> =
        initial_Produits_Main_DataBase.toMutableStateList()

    val groupedProductsPatGrossist: List<Pair<ProduitModel.GrossistBonCommandes.GrossistInformations, List<ProduitModel>>>
        get() = produitsMainDataBase
            .mapNotNull { product ->
                product.bonCommendDeCetteCota?.grossistInformations?.let { grossistInfo ->
                    grossistInfo to product
                }
            }
            .groupBy(
                keySelector = { it.first },
                valueTransform = { it.second }
            )
            .toList()
            .sortedBy { (grossist, _) ->
                grossist.positionInGrossistsList
            }

    val groupedProductsParClients: List<Pair<ProduitModel.ClientBonVentModel.ClientInformations, List<ProduitModel>>>
        get() = produitsMainDataBase
            .asSequence()
            .filter { product ->
                product.bonsVentDeCetteCota.isNotEmpty() &&
                        product.bonsVentDeCetteCota.any { it.clientInformations != null }
            }
            .flatMap { product ->
                product.bonsVentDeCetteCota.mapNotNull { bonVent ->
                    bonVent.clientInformations?.let { clientInfo ->
                        clientInfo to product
                    }
                }
            }
            .groupBy(
                keySelector = { it.first },
                valueTransform = { it.second }
            )
            .toList()
            .sortedBy { (client, _) ->
                client.positionDonClientsList
            }
            .toList()

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
            init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit: Int = 0,
            init_coloursEtGoutsCommendee: List<ColoursGoutsCommendee> = emptyList(),
        ) {
            var grossistInformations: GrossistInformations? by mutableStateOf(
                init_grossistInformations
            )

            var cPositionCheyCeGrossit: Boolean by mutableStateOf(false)
            var positionProduitDonGrossistChoisiPourAcheterCeProduit: Int by mutableStateOf(
                init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit
            )


            @get:Exclude
            var coloursEtGoutsCommendee: SnapshotStateList<ColoursGoutsCommendee> =
                init_coloursEtGoutsCommendee.toMutableStateList()

            var coloursEtGoutsCommendeList: List<ColoursGoutsCommendee>
                get() = coloursEtGoutsCommendee.toList()
                set(value) {
                    coloursEtGoutsCommendee.clear()
                    coloursEtGoutsCommendee.addAll(value)
                }

            @IgnoreExtraProperties
            data class GrossistInformations(
                val id: Long = 0,
                val nom: String = "",
                val couleur: String = ""
            ) {
                var auFilterFAB: Boolean by mutableStateOf(false)
                var positionInGrossistsList: Int
                        by mutableIntStateOf(0)

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (other !is GrossistInformations) return false
                    return id == other.id &&
                            nom == other.nom &&
                            couleur == other.couleur
                }

                override fun hashCode(): Int {
                    return Objects.hash(id, nom, couleur)
                }

                companion object {
                    fun produitGroupeurParGrossistInfos(
                        produitsMainDataBase: List<ProduitModel>
                    ): List<Pair<GrossistInformations, List<ProduitModel>>> {
                        return produitsMainDataBase
                            .filter { it.bonCommendDeCetteCota?.grossistInformations != null }
                            .groupBy { it.bonCommendDeCetteCota!!.grossistInformations!! }
                            .toList()
                    }
                }
            }

            @IgnoreExtraProperties
            class ColoursGoutsCommendee(
                val id: Long = 0,
                val nom: String = "",
                val emoji: String = "",
                init_quantityAchete: Int = 0
            ) {
                var quantityAchete: Int by mutableIntStateOf(init_quantityAchete)
            }
        }

        @IgnoreExtraProperties
        class ClientBonVentModel(
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
                val id: Long = 0,
                val nom: String = "",
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

        fun updateProduct_produitsAvecBonsGrossist(
            product: ProduitModel,
            viewModelProduits: ViewModelInitApp
        ) {
            viewModelProduits.viewModelScope.launch {
                try {
                    // Update Firebase
                    produitsFireBaseRef.child(product.id.toString()).setValue(product).await()

                    // Update _produitsAvecBonsGrossist
                    val index = viewModelProduits._produitsAvecBonsGrossist.indexOfFirst { it.id == product.id }
                    if (index != -1) {
                        // Direct update of the SnapshotStateList
                        viewModelProduits._produitsAvecBonsGrossist[index] = product
                    }

                    Log.d("ViewModelInitApp", "Successfully updated product ${product.id}")
                } catch (e: Exception) {
                    Log.e("ViewModelInitApp", "Failed to update product ${product.id}", e)
                }
            }
        }
    }
}
