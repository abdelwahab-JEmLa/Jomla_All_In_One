package Z_MasterOfApps.Kotlin.Model

import Z_MasterOfApps.Kotlin.Model.Extension.GrossistBonCommandesExtension
import Z_MasterOfApps.Kotlin.Model.Extension.ProduitModelCompanion
import Z_MasterOfApps.Kotlin.Model.Extension.ProduitModelExtension
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
        @IgnoreExtraProperties
        class StatuesBase(
            var ilAUneCouleurAvecImage: Boolean = false,
        ) {
            var naAucunImage: Boolean by mutableStateOf(false)
            var sonImageBesoinActualisation: Boolean by mutableStateOf(false)
            var imageGlidReloadTigger: Int by mutableStateOf(0)

            var prePourCameraCapture: Boolean by mutableStateOf(false)
            var seTrouveAuDernieDuCamionCarCCarton: Boolean by mutableStateOf(false)
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


        // Nouvelle implémentation avec derived state pour bonCommendDeCetteCota
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
            init_grossistInformations: GrossistInformations? = null,
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

            var grossistInformations: GrossistInformations? by mutableStateOf(
                init_grossistInformations
            )
            @IgnoreExtraProperties
            data class GrossistInformations(
                val id: Long = 1,
                val nom: String = "Non Defini",
                val couleur: String = "#FFFFFF"

            ) {
                var auFilterFAB: Boolean by mutableStateOf(false)
                var positionInGrossistsList: Int by mutableIntStateOf(0)

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
                var id: Long = 1,
                var nom: String = "Non Defini",
                val couleur: String = "#FFFFFF"
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

        companion object : ProduitModelCompanion()

        constructor() : this(0)
    }

    companion object : ProduitModelExtension()
}
