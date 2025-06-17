package Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto.A.Model

import android.os.Build
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.mongodb.kbson.BsonObjectId

@Entity
data class Z_AppCompt(
    @PrimaryKey
    var bsonObjectId: String = BsonObjectId().toHexString(),

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

    // Section Paramaters telephone
    var mainInitDataBaseProgressEtate: Float = 0f,

    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
) {
    fun withDernierTimeTampsSynchronisationAvecFireBase(): Z_AppCompt {
        return this.copy(
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    companion object {
        fun logCategory(data: Z_AppCompt, TAG: String) {
            Log.d(TAG, "Z_AppComptEntity: ${data.bsonObjectId} - ${data.nom}")
        }

        fun compareEntre(
            ancien: Z_AppCompt,
            newData: Z_AppCompt
        ): Boolean {
            val delimiterExistence =
                ancien.bsonObjectId == newData.bsonObjectId
            return delimiterExistence
        }
    }
}
