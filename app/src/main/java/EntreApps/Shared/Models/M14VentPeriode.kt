package EntreApps.Shared.Models

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Entity
data class M14VentPeriode(
    @PrimaryKey
    var keyID: String = generePushKey(),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    val abdelmounen_Doit_Etre_Ici: Boolean = false,


    //---------------------------------Forging Keys.Parent VentPeriod----------------------------------------------------------------------------------------------------------------------------------
    var parent_M9AppCompt_KeyID: String = "",
    var parent_M9AppCompt_DebugInfos: String = "",
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------

    val son_verification_entre_vent_et_achat_est_fait: Boolean = true,
    // Section StatuesMutable
    val credit_Vents_Totale: Double = 0.0,
    val cash_Vents_Totale: Double = 0.0,

    val credit_achats_Totale: Double = 0.0,
    val cash_achats_Totale: Double = 0.0,

    val credit_produitsAuDepot: Double = 0.0,
    val valeur_Produits_depuit_Ancien_Vent_Period: Double = 0.0,
    val acheter_produitsAuDepot: Double = 0.0,

    val pre_fraits_voiture_essance_marche_et_paprasse: Double = 0.0,

    val saved_balance: Double = 0.0,

    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.SoquetteNonDefinie,
) {
    fun get_DebugInfos(): String {
        return buildString {
            append(keyID.takeLast(3))
        }
    }

    enum class EtateActuellementEst {
        SoquetteNonDefinie,
        CONFIRME,
    }

    companion object {
        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/DatasM14VentPeriode"
        )
        fun remove_ref(){
            ref.removeValue()
        }

        fun generePushKey() = RepositorysMainSetter.Companion.genereUnPushKeyFireBase(ref)

        fun get_Default() = M14VentPeriode()
    }
}
