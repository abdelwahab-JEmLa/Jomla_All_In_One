package Z_CodePartageEntreApps.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class _1_1_CouleurAcheteOperation(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    // Section Related Parents Foreign Key IDs
    var couleurId_ParentVID : Long = 0L,
    var produitId_ParentVID : Long = 0L,

    // Section InfosDeBase
    var totaleQuantity: Int = 1,

    // Section StatuesMutable
    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.AFFICHE_MAIS_PAS_CONFIRME,

    ) {
    enum class EtateActuellementEst {
        AFFICHE_MAIS_PAS_CONFIRME,
        QUANTITY_CHOSI_MAIS_PAS_DE_CONFIRMATION_PRODUIT,
        SUPPRIME_AU_PREMIER_PICK,
        SUPP_AU_PANIER_FINALE
    }
}
