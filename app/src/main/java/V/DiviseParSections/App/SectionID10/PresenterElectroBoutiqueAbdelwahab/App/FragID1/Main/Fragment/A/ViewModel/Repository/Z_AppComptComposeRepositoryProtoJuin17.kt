package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository

import Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto.Z_AppComptRepositoryProtoJuin17
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mongodb.kbson.BsonObjectId

@Stable
class Z_AppComptComposeRepositoryProtoJuin17(
    private val ancienRepo: Z_AppComptRepositoryProtoJuin17,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<Z_AppCompt>>(emptyList())
    val datasValue by derivedStateOf {
        _datas.value
    }

    val currentAppCompt by derivedStateOf {
        datasValue.find { it.bsonObjectId == "b1" }
    }

    init {
        composScope.launch {
            dao.getAllFlow().collect {
                _datas.value = it
                // ADDED: Debug logging to track data loading
                Log.d("Z_AppComptRepository", "Data loaded: ${it.size} items")
                it.forEach { item ->
                    Log.d("Z_AppComptRepository", "Item: ${item.bsonObjectId} - ${item.nom}")
                }
            }
        }
    }

    fun addOrUpdateData(data: Z_AppCompt) {
        val dataAvecTigerUpdate = data.withDernierTimeTampsSynchronisationAvecFireBase()
        val existingIndex = datasValue.indexOfFirst { ancien ->
            Z_AppCompt.compareEntre(ancien = ancien, newData = dataAvecTigerUpdate)
        }
        _datas.value = if (existingIndex >= 0) {
            // FIXED: Replace the entire object instead of just updating timestamp
            datasValue.toMutableList().apply {
                this[existingIndex] = dataAvecTigerUpdate
            }
        } else {
            datasValue + dataAvecTigerUpdate
        }

        ancienRepo.addOrUpdatedDataBase(existingIndex, dataAvecTigerUpdate)
    }
}

@Entity
data class Z_AppCompt(
    @PrimaryKey
    var bsonObjectId: String = BsonObjectId().toHexString(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    // Section InfosDeBase
    var nom: String = "",
    var deviceModelNom: String = Build.MODEL,
    var deviceModelId: String = Build.ID,

    // Section StatuesMutable
    // Section Options Personnel
    var presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId: String = "",

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
       val caRef = Firebase.database.getReference(
        "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/Z_AppCompt"
        )
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
