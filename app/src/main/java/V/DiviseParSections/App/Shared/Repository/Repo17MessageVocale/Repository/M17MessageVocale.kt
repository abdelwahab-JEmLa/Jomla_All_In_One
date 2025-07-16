package V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.centralRef
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class M17MessageVocale(
    @PrimaryKey var keyID: String = getPushFireBase(ref),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var creationTimestamps: Long = 0,

    //Forging Keys
    var relativeAuDataBase: RelativeAuDataBase = RelativeAuDataBase.NONE,

    val parentMessageVID: Long = 0,

    //Infos De Base
    var etate: Etate = Etate.EN_COURT_ENREGESTREMENT,
    var timestamps: Long = DatesHandler().getCurrentTimestamps(),

    //---------------------------------ForgingKeys.M9AppCompt----------------------------------------------------------------------------------------------------------------------------------
    val parent_M9AppCompt_KeyID: String = "null",
    val parent_M9AppCompt_DebugInfos: String = "null",

    //---------------------------------ForgingKeys.M9AppCompt----------------------------------------------------------------------------------------------------------------------------------
    val parent_M8BonVent_KeyID: String = "null",
    val parent_M8BonVent_DebugInfos: String = "null",

    //---------------------------------ForgingKeys.M9AppCompt----------------------------------------------------------------------------------------------------------------------------------

    //Etates Mutable
    val nomDeSonOriginaleFichie: String = "",

    // Section keyFireBase et dernierFireBaseUpdateTimestamps
) {
    fun getDebugInfos(): String {
        return buildString {
            append("KeyID: ${keyID.takeLast(4).uppercase()}\n")
            append("etate:{$etate}\n")
        }
    }

    enum class RelativeAuDataBase() {
        NONE,
        C3_BonAchate,
    }

    enum class Etate(val nomArabe: String? = null) {
        EN_COURT_ENREGESTREMENT,
        ENVOYER,
        VUE,
        ECOUTE,
    }

    companion object {
        val ref = centralRef.child("Datas17MessageVocale")
        fun generePushKey() = ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")

        fun createTestInstance(): List<M17MessageVocale> { return emptyList() }


        fun removeRef(preparedData: M17MessageVocale) { ArticlesBasesStatsTable.ref.child(preparedData.keyID).removeValue() }

    }
}
