package Z_CodePartageEntreApps.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class _1_2_ProduitAcheteOperation(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,
    var produitAcheterID: Long = 0L,
    // Section Related Parents Foreign Key IDs
    var parent_1_3_BonAchat: Long = 0L,

    // Section InfosDeBase

    // Section StatuesMutable

    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.PRESENTATION,

    ) {
    enum class EtateActuellementEst {
        PRESENTATION,
        CONFIRME,
        SUPPRIME_AU_PREMIER_PICK,
        SUPP_AU_PANIER_FINALE
    }
}
