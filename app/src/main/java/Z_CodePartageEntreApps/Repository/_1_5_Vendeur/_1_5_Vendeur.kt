package Z_CodePartageEntreApps.Repository._1_5_Vendeur

import android.os.Build
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class _1_5_Vendeur(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    // Section InfosDeBase
    var deviceModelNom: String = Build.MODEL,
    var deviceModelId: String =Build.ID,
    var nom: String = "Manager Vendor",

    // Section StatuesMutable
    var ceComptVendeurInsertBonsAchatAuPeriodID: Long = 0L,
    var ceComptVendeurStartAffichePeriod: Long = 0L,

    var itsProductionModePourCeCompt : Boolean = false,

    var hideAppScreen: Boolean = false,

    // Section Options Personele
    var migreSonDataBaseAuStart: Boolean = false,
    var cConnectAuDevelopingDataBaseAuRelodApp: Boolean = false,
    )
