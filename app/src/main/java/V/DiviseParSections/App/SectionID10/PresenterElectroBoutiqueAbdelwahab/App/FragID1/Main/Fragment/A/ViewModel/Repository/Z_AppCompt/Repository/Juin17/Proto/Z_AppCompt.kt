package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.Z_AppCompt.Repository.Juin17.Proto

import android.os.Build
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.mongodb.kbson.BsonObjectId

@Entity
data class Z_AppCompt(
    @PrimaryKey
    var bsonObjectId: String = BsonObjectId().toHexString(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    // Section InfosDeBase
    var nom: String = "Manager Vendor",
    var deviceModelNom: String = Build.MODEL,
    var deviceModelId: String = Build.ID,

    // Section StatuesMutable
        // Section Options Personnel
    var presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId: String = "b1",

    var itsProductionModePourCeCompt: Boolean = false,
    var ceComptVendeurInsertBonsAchatAuPeriodID: Long = 0L,
    var ceComptVendeurStartAffichePeriod: Long = 0L,
    var hideAppScreen: Boolean = false,
    var migreSonDataBaseAuStart: Boolean = false,
    var cConnectAuDevelopingDataBaseAuRelodApp: Boolean = false,

    // Section Centralization Valeurs Pour Injection a TOu modules
    var idClientOuSonMarqueMapEstOuvert: Long = 0L,

    // Section Paramaters telephone
    var mainInitDataBaseProgressEtate: Float = 0f,

    ) {
    fun withDernierTimeTampsSynchronisationAvecFireBase(): Z_AppCompt {
        return this.copy(
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    companion object {
        fun logCategory(data: Z_AppCompt, TAG: String) { Log.d(TAG, "Z_AppComptEntity: ${data.bsonObjectId} - ${data.nom}") }

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
