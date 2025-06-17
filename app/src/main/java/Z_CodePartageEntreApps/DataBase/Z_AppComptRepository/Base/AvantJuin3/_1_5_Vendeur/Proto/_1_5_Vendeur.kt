package Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.AvantJuin3._1_5_Vendeur.Proto

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Fonctions.Main.getKeyFireBase
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


    // Section Centralization Valeurs Pour Injection a TOu modules
    var idClientOuSonMarqueMapEstOuvert: Long = 0L,

    // Section keyFireBase et dernierFireBaseUpdateTimestamps
    var keyFireBase: String = "",
    var dernierTimeTampsSynchronisationAvecFireBase: Long = DatesHandler().getCurrentTimestamps(),
    ) {
    fun withProperKeyFireBaseAndTimeTamp(): _1_5_Vendeur {
        val safeKey = keyFireBase.ifEmpty { getKeyFireBase(vid, nom) }
        return this.copy(
            keyFireBase = safeKey,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }
}
