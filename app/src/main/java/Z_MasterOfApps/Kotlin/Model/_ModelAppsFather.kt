package Z_MasterOfApps.Kotlin.Model

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee.Companion.derivedStateDeBColoursGoutsCommende
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
            val id: Long = 1,
            var nom: String = "Non Defini",
            var imogi: String = "🎨",
            var sonImageNeExistPas: Boolean = false,
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
            var grossistInformations: GrossistInformations? by mutableStateOf(init_grossistInformations)
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

                companion object {
                    fun List<ClientBonVentModel>.derivedStateDeBColoursGoutsCommende(): SnapshotStateList<ColoursGoutsCommendee> {
                        val processedColors = mutableMapOf<Long, ColoursGoutsCommendee>()

                        this.forEach { bonVent ->
                            bonVent.colours_Achete.forEach { colorAchat ->
                                val existingColor = processedColors.getOrPut(colorAchat.couleurId) {
                                    ColoursGoutsCommendee(
                                        id = colorAchat.couleurId,
                                        nom = colorAchat.nom,
                                        emoji = colorAchat.imogi
                                    )
                                }
                                existingColor.quantityAchete += colorAchat.quantity_Achete
                            }
                        }

                        return processedColors.values
                            .filter { it.quantityAchete > 0 }
                            .toMutableStateList()
                    }
                }
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
                // Update the bon commend if it exists
                bonCommendDeCetteCota?.let { bonCommend ->
                    bonCommend.coloursEtGoutsCommendeeList = value.derivedStateDeBColoursGoutsCommende()
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
        // Add extension function to handle the derived state update

    }
    companion object : ProduitModelExtension()
}
