package Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class _1_1_CouleurAcheteOperation(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    // Section Related Parents Foreign Key IDs
    var couleurId_ParentVID: Long = 0L,
    var parentProduitAchateOperationVID: Long? =null,

    // Section InfosDeBase
    var totaleQuantity: Int = 0,

    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.VUE,

    ) {
    enum class EtateActuellementEst {
        VUE,
        QUANTITY_CHOISI,
        SUPPRIME_AU_PREMIER_PICK,
        SUPP_AU_PANIER_FINALE
    }
}
