package Y_AppsFather.Kotlin.Model

import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel.GrossistBonCommandes.Companion.updateChildren
import Y_AppsFather.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
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

        // Nouvelle implémentation avec derived state pour bonCommendDeCetteCota
        private var _bonCommendDeCetteCota by mutableStateOf<GrossistBonCommandes?>(
            init_bonCommendDeCetteCota
        )

        var bonCommendDeCetteCota: GrossistBonCommandes?
            get() {
                if (_bonCommendDeCetteCota == null && bonsVentDeCetteCota.isNotEmpty()) {
                    _bonCommendDeCetteCota = createDerivedBonCommande()
                }
                return _bonCommendDeCetteCota
            }
            set(value) {
                _bonCommendDeCetteCota = value
            }

        @get:Exclude
        var bonsVentDeCetteCota: SnapshotStateList<ClientBonVentModel> =
            initBonsVentDeCetteCota.toMutableStateList()

        var bonsVentDeCetteCotaList: List<ClientBonVentModel>
            get() = bonsVentDeCetteCota.toList()
            set(value) {
                bonsVentDeCetteCota.clear()
                bonsVentDeCetteCota.addAll(value)
                updateBonCommande() // Update derived state when sales data changes
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

        private fun createDerivedBonCommande(): GrossistBonCommandes {
            return GrossistBonCommandes().apply {
                grossistInformations = GrossistBonCommandes.GrossistInformations(
                    id = 0L,
                    nom = "Non Defini",
                    couleur = "#FF0000"
                ).apply {
                    auFilterFAB = false
                    positionInGrossistsList = 0
                }

                val processedColors = mutableListOf<GrossistBonCommandes.ColoursGoutsCommendee>()

                bonsVentDeCetteCota
                    .flatMap { it.colours_Achete }
                    .groupBy { it.couleurId }
                    .forEach { (couleurId, colorList) ->
                        colorList.firstOrNull()?.let { firstColor ->
                            val totalQuantity = colorList.sumOf { it.quantity_Achete }

                            val newCommendee = GrossistBonCommandes.ColoursGoutsCommendee(
                                id = couleurId,
                                nom = firstColor.nom,
                                emoji = firstColor.imogi
                            ).apply {
                                quantityAchete = totalQuantity
                            }

                            if (newCommendee.quantityAchete > 0) {
                                processedColors.add(newCommendee)
                            }
                        }
                    }

                coloursEtGoutsCommendee.clear()
                coloursEtGoutsCommendee.addAll(processedColors)
            }

        }

        fun updateBonCommande() {
            _bonCommendDeCetteCota = createDerivedBonCommande()
            updateChildren(_bonCommendDeCetteCota!!,this)
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
            val id: Long = 0,
            var nom: String = "Non Defini",
            var imogi: String = "🎨",
            var sonImageNeExistPas: Boolean = false,
        )

        @IgnoreExtraProperties
        open class GrossistBonCommandes(
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
            var positionProduitDonGrossistChoisiPourAcheterCeProduit: Int by mutableStateOf(0)

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
                var positionInGrossistsList: Int by mutableIntStateOf(0)
            }

            @IgnoreExtraProperties
            class ColoursGoutsCommendee(
                val id: Long = 0,
                val nom: String = "",
                val emoji: String = "",
            ) {
                var quantityAchete: Int by mutableIntStateOf(0)
            }

            companion object : ExtensionGrossistBonCommandes()
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

            companion object {
                fun updateSelf(
                    produit: ProduitModel,
                    bonVente: ClientBonVentModel,
                    viewModelProduits: ViewModelInitApp
                ) {
                    produit.bonsVentDeCetteCota.removeAll { it.clientInformations?.id == bonVente.clientInformations?.id }
                    produit.bonsVentDeCetteCota.add(bonVente)
                    val index =
                        viewModelProduits._modelAppsFather.produitsMainDataBase.indexOfFirst { it.id == produit.id }
                    if (index != -1) {
                        produit.updateBonCommande() // Update derived state after modifying sales data
                        viewModelProduits._modelAppsFather.produitsMainDataBase[index] = produit
                    }
                }
            }
        }
    }
    companion object : ExtensionProduitModel()
}
