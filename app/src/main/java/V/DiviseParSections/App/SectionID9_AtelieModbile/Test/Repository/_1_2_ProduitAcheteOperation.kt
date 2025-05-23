package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository

import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class _1_2_ProduitAcheteOperation(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,
    var produitAcheterID: Long = 0L,
    var nomProduitConcerned: Long = 0L,
    // Section Related Parents Foreign Key IDs
    var parentIdClient: Long = 0L,
    var parent_1_3_TransactionCommercial: Long = 0L,
    var nomParentMainValue: String = "Non Defini",

    // Section InfosDeBase
    var timestamps: Long = DatesHandler().getCurrentTimestamps(),

    // Section StatuesMutable
    var provisoireMonPrix: Double = 0.0,

    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.PRESENTATION,

    ) {

    val fireBaseKeyID: String
        get() {
            val parent = "${parent_1_3_TransactionCommercial}_$nomParentMainValue"
            val thisVal = "${vid}_$nomProduitConcerned"
            return "($parent)->($thisVal))"
        }

    enum class EtateActuellementEst {
        PRESENTATION,
        CONFIRME,
        SUPPRIME_AU_PREMIER_PICK,
        SUPP_AU_PANIER_FINALE
    }
}
