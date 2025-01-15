package Z_MasterOfApps.Kotlin.Model

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Objects

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
            val id: Long = 1,
            var nom: String = "Non Defini",
            var imogi: String = "🎨",
            var sonImageNeExistPas: Boolean = false,
            var position_Du_Couleur_Au_Produit: Long = 0,
        )

        // Nouvelle implémentation avec derived state pour bonCommendDeCetteCota
        var bonCommendDeCetteCota by mutableStateOf<GrossistBonCommandes?>(
            init_bonCommendDeCetteCota
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
            var mutableBasesStates: MutableBasesStates? by mutableStateOf(MutableBasesStates())
            var cPositionCheyCeGrossit: Boolean by mutableStateOf(false)
            var positionProduitDonGrossistChoisiPourAcheterCeProduit: Int by mutableStateOf(0)

            @get:Exclude
            var coloursEtGoutsCommendee: SnapshotStateList<ColoursGoutsCommendee> =
                init_coloursEtGoutsCommendee.toMutableStateList()

            var coloursEtGoutsCommendeeList: List<ColoursGoutsCommendee>
                get() = coloursEtGoutsCommendee.toList()
                set(value) {
                    coloursEtGoutsCommendee.clear()
                    coloursEtGoutsCommendee.addAll(value)
                }

            @IgnoreExtraProperties
            data class GrossistInformations(
                val id: Long = 1,
                val nom: String = "Non Defini",
                val couleur: String = "FFFFFF"
            ) {
                var auFilterFAB: Boolean by mutableStateOf(false)
                var positionInGrossistsList: Int by mutableIntStateOf(0)
            }

            @IgnoreExtraProperties
            class MutableBasesStates {
                var cPositionCheyCeGrossit: Boolean by mutableStateOf(false)
                var positionProduitDonGrossistChoisiPourAcheterCeProduit: Int by mutableStateOf(0)
            }

            @IgnoreExtraProperties
            class ColoursGoutsCommendee(
                val id: Long = 1,
                var nom: String = "Non Defini",
                var emogi: String = "🎨",
            ) {
                var quantityAchete: Int by mutableIntStateOf(0)
            }

            companion object : GrossistBonCommandesExtension()
        }

        @get:Exclude
        var bonsVentDeCetteCota: SnapshotStateList<ClientBonVentModel> =
            initBonsVentDeCetteCota.toMutableStateList()

        // Update the bonsVentDeCetteCotaList setter
        var bonsVentDeCetteCotaList: List<ClientBonVentModel>
            get() = bonsVentDeCetteCota.toList()
            set(value) {
                bonsVentDeCetteCota.clear()
                bonsVentDeCetteCota.addAll(value)

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
                var positionDonClientsList: Int by mutableIntStateOf(0)

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
                var couleurId: Long = 0,
                var nom: String = "",
                var quantity_Achete: Int = 0,
                var imogi: String = ""
            )
        }

        companion object {
            fun updateProduit(
                product: _ModelAppsFather.ProduitModel,
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

            fun ProduitModel.calculeSelfGrossistBonCommandesExtension(viewModelInitApp: ViewModelInitApp) {
                // Only proceed if there are active sales records
                if (bonsVentDeCetteCota.isNotEmpty()) {
                    // Find the most recent bon commande
                    val mostRecentBonCommande = historiqueBonsCommend.maxByOrNull { it.date }

                    // Update the current bon commande
                    bonCommendDeCetteCota = mostRecentBonCommande?.also { bonCommande ->
                        // Ensure mutableBasesStates is initialized
                        bonCommande.mutableBasesStates = bonCommande.mutableBasesStates
                            ?: GrossistBonCommandes.MutableBasesStates()

                        // Update position information
                        bonCommande.mutableBasesStates?.apply {
                            cPositionCheyCeGrossit = bonCommande.cPositionCheyCeGrossit
                            positionProduitDonGrossistChoisiPourAcheterCeProduit =
                                bonCommande.positionProduitDonGrossistChoisiPourAcheterCeProduit
                        }

                        // If there's no grossist information, create default one from the most recent record
                        if (bonCommande.grossistInformations == null) {
                            bonCommande.grossistInformations = historiqueBonsCommend
                                .lastOrNull()?.grossistInformations
                                ?: GrossistBonCommandes.GrossistInformations(
                                    id = 1,
                                    nom = "Non Defini",
                                    couleur = "#FFFFFF"
                                ).apply {
                                    auFilterFAB = false
                                    positionInGrossistsList = 0
                                }
                        }

                        // Clear existing colors before processing
                        bonCommande.coloursEtGoutsCommendee.clear()

                        // Process and aggregate colors from active sales
                        val processedColors = bonsVentDeCetteCota
                            .flatMap { it.colours_Achete }
                            .groupBy { it.couleurId }
                            .mapNotNull { (couleurId, colorList) ->
                                colorList.firstOrNull()?.let { firstColor ->
                                    val totalQuantity = colorList.sumOf { it.quantity_Achete }

                                    if (totalQuantity > 0) {
                                        GrossistBonCommandes.ColoursGoutsCommendee(
                                            id = couleurId,
                                            nom = firstColor.nom,
                                            emogi = firstColor.imogi
                                        ).apply {
                                            quantityAchete = totalQuantity
                                        }
                                    } else null
                                }
                            }

                        // Add all processed colors to the bon commande
                        bonCommande.coloursEtGoutsCommendee.addAll(processedColors)
                    }

                    // Update the product after processing
                    updateProduit(this, viewModelInitApp)
                } else {
                    // If there are no active sales, clear the current bon commande
                    bonCommendDeCetteCota = null
                }
            }
        }

    }

    companion object : ProduitModelExtension()
}
