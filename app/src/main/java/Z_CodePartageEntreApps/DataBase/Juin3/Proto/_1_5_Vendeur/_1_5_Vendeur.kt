package Z_CodePartageEntreApps.DataBase.Juin3.Proto._1_5_Vendeur

import Z_CodePartageEntreApps.Modules.DatesHandler
import android.os.Build
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class _1_5_Vendeur(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    // Section InfosDeBase
    var deviceModelNom: String = Build.MODEL,
    var deviceModelId: String = Build.ID,
    var nom: String = "Manager Vendor",

    // Section StatuesMutable
    var ceComptVendeurInsertBonsAchatAuPeriodID: Long = 0L,
    var ceComptVendeurStartAffichePeriod: Long = 0L,

    var itsProductionModePourCeCompt: Boolean = false,

    var hideAppScreen: Boolean = false,

    // Section Options Personele
    var migreSonDataBaseAuStart: Boolean = false,
    var cConnectAuDevelopingDataBaseAuRelodApp: Boolean = false,

    var dernierTimeTampsSynchronisationAvecFireBase: Long = DatesHandler().getCurrentTimestamps(),

    // Section Centralization Valeurs Pour Injection a TOu modules
    var idClientOuvertPoutCeCompt: Long = 0L,

    // Section keyFireBase et dernierFireBaseUpdateTimestamps
    var keyFireBase: String = "",
    var dernierFireBaseUpdateTimestamps: Long = 0,
    ) {
    companion object {
        fun getActiveComptPourCeTelephone(datas: List<_1_5_Vendeur>): _1_5_Vendeur? {
            return datas.find { it.deviceModelNom == Build.MODEL }
        }
    }
}
