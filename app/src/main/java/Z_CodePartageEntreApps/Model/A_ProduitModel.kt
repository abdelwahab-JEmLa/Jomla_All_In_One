package Z_CodePartageEntreApps.Model

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class A_ProduitModel(
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


    var parentCategoryId by mutableLongStateOf(0L)
    var indexInParentCategorie by mutableIntStateOf(0)

    var statuesBase: StatuesBase by mutableStateOf(StatuesBase())
    @IgnoreExtraProperties
    class StatuesBase(
        coloursEtGoutsIds: List<Long> = emptyList(), // Changed parameter name to avoid shadowing
        var ilAUneCouleurAvecImage: Boolean = false,
        var characterProduit: CharacterProduit = CharacterProduit(),
        var infosCoutes: InfosCoutes = InfosCoutes(),
    ) {
        // Convert to mutable state list
        private val _coloursEtGoutsIds = coloursEtGoutsIds.toMutableStateList()
        var coloursEtGoutsIds: List<Long>
            get() = _coloursEtGoutsIds
            set(value) {
                _coloursEtGoutsIds.clear()
                _coloursEtGoutsIds.addAll(value)
            }

        // Function to add a new color ID
        fun addColorId(colorId: Long) {
            _coloursEtGoutsIds.add(colorId)
        }

        var naAucunImage: Boolean by mutableStateOf(false)
        var sonImageBesoinActualisation: Boolean by mutableStateOf(false)
        var imageGlidReloadTigger: Int by mutableStateOf(0)

        var prePourCameraCapture: Boolean by mutableStateOf(false)
        var seTrouveAuDernieDuCamionCarCCarton: Boolean by mutableStateOf(false)

        @IgnoreExtraProperties
        data class InfosCoutes(
            var monPrixAchat: Double = 0.0,
            var monPrixVent: Double = 0.0,
        )

        @IgnoreExtraProperties
        data class CharacterProduit(
            var emballageCartone: Boolean = false,
        )
    }

    var etatesMutable by mutableStateOf(EtatesMutable())

    @IgnoreExtraProperties
    class EtatesMutable {
        var diponibilityEtate: Boolean by mutableStateOf(false)
        var porbableNonDispo: Boolean by mutableStateOf(false)

        // Add a string property for Firebase serialization
        var nonDispoPourClientsString: String? by mutableStateOf(null)

        // Transient property for local use
        @get:Exclude
        var nonDispoPourClients: NON_DISPO_POUR_CLIENTS?
            get() = when (nonDispoPourClientsString) {
                "TOUT" -> NON_DISPO_POUR_CLIENTS.TOUT
                "NEVEAU" -> NON_DISPO_POUR_CLIENTS.NEVEAU
                "DEFINIE" -> NON_DISPO_POUR_CLIENTS.DEFINIE
                else -> null
            }
            set(value) {
                nonDispoPourClientsString = value?.name
            }

        enum class NON_DISPO_POUR_CLIENTS(val color: Color) {
            TOUT(Color(0xFFF44336)),
            NEVEAU(Color(0xFFFF9800)),
            DEFINIE(Color(0xFF2196F3));

            companion object {
                fun fromNullable(value: NON_DISPO_POUR_CLIENTS?): String {
                    return when (value) {
                        null -> "Disponible"
                        TOUT -> "Tout"
                        NEVEAU -> "Neveau"
                        DEFINIE -> "Définie"
                    }
                }
            }
        }
    }

    @get:Exclude
    var coloursEtGouts: SnapshotStateList<ColourEtGout_Model> =
        init_colours_Et_Gouts.toMutableStateList()

    var coloursEtGoutsList: List<ColourEtGout_Model>
        get() = coloursEtGouts.toList()
        set(value) {
            coloursEtGouts.clear()
            coloursEtGouts.addAll(value)
        }

    @IgnoreExtraProperties
    class ColourEtGout_Model(
        val id: Long = 1,
        var nom: String = "Non Defini",
        var imogi: String = "🎨",
        var sonImageNeExistPas: Boolean = false,
        var position_Du_Couleur_Au_Produit: Long = 0,
    )

    var bonCommendDeCetteCota by mutableStateOf<GrossistBonCommandes?>(
        init_bonCommendDeCetteCota
    )
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
    class GrossistBonCommandes(
        var vid: Long = 0,
        var idGrossistChoisi: Long = 0,
        init_coloursEtGoutsCommendee: List<ColoursGoutsCommendee> = emptyList(),
    ) {
        var mutableBasesStates: MutableBasesStates? by mutableStateOf(MutableBasesStates())
        @IgnoreExtraProperties
        class MutableBasesStates {
            var cPositionCheyCeGrossit: Boolean by mutableStateOf(false)
            var positionProduitDonGrossistChoisiPourAcheterCeProduit: Int by mutableStateOf(0)
            var dateInString by mutableStateOf("2025-01-01")
            var currentCreditBalance by mutableStateOf(0.0)
        }

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
        class ColoursGoutsCommendee(
            val id: Long = 1,
            var nom: String = "Non Defini",
            var emogi: String = "🎨",
        ) {
            var quantityAchete: Int by mutableIntStateOf(0)
        }
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
    @get:Exclude
    var historiqueBonsVents: SnapshotStateList<ClientBonVentModel> =
        init_historiqueBonsVents.toMutableStateList()

    var historiqueBonsVentsList: List<ClientBonVentModel>
        get() = historiqueBonsVents.toList()
        set(value) {
            historiqueBonsVents.clear()
            historiqueBonsVents.addAll(value)
        }
    @IgnoreExtraProperties
    class ClientBonVentModel(
        vid: Long = 0,
        var clientIdChoisi: Long = 0,
        var produitStatueDeBaseDeChezCeClient: StatueDeBase = StatueDeBase(),
        init_colours_achete: List<ColorAchatModel> = emptyList(),
    ) {
        // Basic information
        var bonStatueDeBase by mutableStateOf(BonStatueDeBase())
        @IgnoreExtraProperties
        data class StatueDeBase(
            var positionDonClientsList: Int = 0,
        )
        // Status management
        @IgnoreExtraProperties
        class BonStatueDeBase {
            var lastUpdateTimestamp: Long by mutableStateOf(System.currentTimeMillis())
        }

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
        class ColorAchatModel(
            var vidPosition: Long = 0,
            var couleurId: Long = 0,
            var nom: String = "",
            var quantity_Achete: Int = 0,
            var imogi: String = ""
        )
    }
    companion object{
        fun A_ProduitModel.ExtraiGrossistInfos(
            viewModel: ViewModelInitApp
        ) = viewModel._modelAppsFather.grossistsDataBase.find { it.id == this.bonCommendDeCetteCota?.idGrossistChoisi }

        fun syncData(
            data: A_ProduitModel? = null,
            dataSnapshot: DataSnapshot? = null
        ): Any {
            // Convert from Firebase to model
            if (dataSnapshot != null) {
                val value = dataSnapshot.getValue(A_ProduitModel::class.java) ?: A_ProduitModel().also {
                }
                return value
            }

            // Convert from model to Firebase map
            val sourceData = data ?: A_ProduitModel()
            return mapOf(
                "id" to sourceData.id,
                "itsTempProduit" to sourceData.itsTempProduit,
                "nom" to sourceData.nom,
                "besoinToBeUpdated" to sourceData.besoinToBeUpdated,
                "non_Trouve" to sourceData.non_Trouve,
                "isVisible" to sourceData.isVisible,
                "parentCategoryId" to sourceData.parentCategoryId,
                "indexInParentCategorie" to sourceData.indexInParentCategorie,
                "statuesBase" to mapOf(
                    "coloursEtGoutsIds" to sourceData.statuesBase.coloursEtGoutsIds,
                    "ilAUneCouleurAvecImage" to sourceData.statuesBase.ilAUneCouleurAvecImage,
                    "naAucunImage" to sourceData.statuesBase.naAucunImage,
                    "sonImageBesoinActualisation" to sourceData.statuesBase.sonImageBesoinActualisation,
                    "imageGlidReloadTigger" to sourceData.statuesBase.imageGlidReloadTigger,
                    "prePourCameraCapture" to sourceData.statuesBase.prePourCameraCapture,
                    "seTrouveAuDernieDuCamionCarCCarton" to sourceData.statuesBase.seTrouveAuDernieDuCamionCarCCarton,
                    "characterProduit" to mapOf(
                        "emballageCartone" to sourceData.statuesBase.characterProduit.emballageCartone
                    ),
                    "infosCoutes" to mapOf(
                        "monPrixAchat" to sourceData.statuesBase.infosCoutes.monPrixAchat,
                        "monPrixVent" to sourceData.statuesBase.infosCoutes.monPrixVent
                    )
                ),
                "etatesMutable" to mapOf(
                    "diponibilityEtate" to sourceData.etatesMutable.diponibilityEtate,
                    "porbableNonDispo" to sourceData.etatesMutable.porbableNonDispo,
                    "nonDispoPourClientsString" to sourceData.etatesMutable.nonDispoPourClientsString
                ),
                "coloursEtGoutsList" to sourceData.coloursEtGoutsList,
                "bonCommendDeCetteCota" to sourceData.bonCommendDeCetteCota,
                "historiqueBonsCommendList" to sourceData.historiqueBonsCommendList,
                "bonsVentDeCetteCotaList" to sourceData.bonsVentDeCetteCotaList,
                "historiqueBonsVentsList" to sourceData.historiqueBonsVentsList
            )
        }
    }

    constructor() : this(0)
}
