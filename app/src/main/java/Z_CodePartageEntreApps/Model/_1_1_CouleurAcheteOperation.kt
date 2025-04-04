package Z_CodePartageEntreApps.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class _1_1_CouleurAcheteOperation(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,
    var couleurId: Long = 0L,
    // Section Related Parents Foreign Key IDs
    var parent_1_2_ProduitAcheteOperationID: Long = 0L,

    // Section InfosDeBase
    var totaleQuantity: Int = 0,

    // Section StatuesMutable
    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.CHOISI_UNE_QUANTITY,

    ) {
    enum class EtateActuellementEst {
        CHOISI_UNE_QUANTITY,
        CONFIRME,
        SUPPRIME_AU_PREMIER_PICK,
        SUPP_AU_PANIER_FINALE
    }
}
