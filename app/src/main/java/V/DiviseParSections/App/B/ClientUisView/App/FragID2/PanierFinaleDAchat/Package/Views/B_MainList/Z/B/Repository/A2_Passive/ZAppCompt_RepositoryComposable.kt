package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ACentralCompoRepositoryProtoJuin9.Companion.getPushFireBase
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.Z_AppComptRepositoryProtoJuin17
import android.os.Build
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
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import java.util.Date
import java.util.Objects

@Stable
class ZAppCompt_RepositoryComposable(
    private val ancienRepo: Z_AppComptRepositoryProtoJuin17
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<Z_AppCompt>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    val ouvertData by derivedStateOf {
        datasValue.firstOrNull { it.bsonObjectId == "b1" }
    }

    init {
        composScope.launch {
            dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun ouvrireUnBonVent(
        ouvertClientOnVentKeyId: String,
        ouvertClientOnVentNom: String,
    ): Unit {
        val ouvertEPeriodVentStartDate = ouvertData!!.ouvertEPeriodVentStartTimesTamp

        addOrUpdateData(
            ouvertData!!.copy(
                ouvertBonVentId = "____Child_ID${ouvertEPeriodVentStartDate}__Nom_$ouvertClientOnVentNom",
                ouvertClientOnVentKeyId = ouvertClientOnVentKeyId,
                ouvertClientOnVentNom = ouvertClientOnVentNom
            )
        )
    }

    fun ouvrireProduitEtCouleurVent(
        produit: ArticlesBasesStatsTable,
        baseFileName: String,
    ): Z_AppCompt {
       val data =ouvertData!!.copy(
           ouvertProduitOnVentID = produit.id.toString(),
           ouvertCouleurOnVentID = baseFileName,
       )
        addOrUpdateData(
            data
        )
        return data
    }

    fun addOrUpdateData(data: Z_AppCompt) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
        val existingIndex = datasValue.indexOfFirst { it.isSameEntity(dataUpdate) }

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = if (existingIndex >= 0) {
                    datasValue.toMutableList().apply {
                        this[existingIndex] = this[existingIndex].copy(
                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                        )
                    }
                } else datasValue + dataUpdate
            }
        }
        ancienRepo.addOrUpdatedDataBase(existingIndex, dataUpdate)
    }
}


@Entity
data class Z_AppCompt(
    @PrimaryKey
    var bsonObjectId: String = getPushFireBase(ref),
    var id: String = "",
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    // Section InfosDeBase
    var nom: String = "",
    var nomMutable: String = "",

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

    // Section Centralization Valeurs Pour Injection add TOu modules
    var idClientOuSonMarqueMapEstOuvert: Long = 0L,

    var cTransactionCommercialIdOuvertPourCeCompt: String = getPushFireBase(ref),
    var cTransactionCommercialKeyOuvertPourCeCompt: String = getPushFireBase(ref),

    // Section Paramaters App telephone
    var mainInitDataBaseProgressEtate: Float = 0f,

    var couleurAchateOperationIdOuvertPourCeCompt: String = "",
    var couleurAchateOperationKeyOuvertPourCeCompt: String = "",
    var ouvertProduitOnVentNom: String = "",
    var ouvertClientOnVentNom: String = "",

    //-----------------Vent Createur-----------

    //Section Parent Period Vent
    var ouvertEPeriodVentId: String = " ____Child_ID${BsonObjectId()}__Nom_Juin24_08_00_AM",
    var ouvertEPeriodVentStartTimesTamp: Long = creatTimeTampDepuitStr("(Juin-24 -<(08:00 AM)"),

    //Section Parent Transaction
    var ouvertBonVentId: String = "",
    var ouvertClientOnVentKeyId: String = "",

    //Section ouvertProduitAncien
    var ouvertProduitOnVentID: String = "ProduitKeyID",
    var ouvertProduitOnVentAncienId: Long = 0L,

    var ouvertCouleurOnVentID: String = "baseFileName",
) {

    init {
        if (id.isEmpty()) {
            id = getKeyID()
        }
        if (nomMutable.isEmpty()) {
            nomMutable = getInitCreationName()
        }
    }

    fun getInitCreationName(): String {
        return nom.ifEmpty { "DefaultCompt" }
    }

    fun getKeyID(): String {
        return "____Child_id${bsonObjectId}_nom_$nom"
    }

    companion object {
        fun creatTimeTampDepuitStr(dateString: String): Long {
            return try {
                val currentDate = Date()
                currentDate.time
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
        }

        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/Z_AppCompt"
        )
    }

    override fun equals(other: Any?) =
        this === other || (other is Z_AppCompt && isSameEntity(other))

    fun isSameEntity(other: Z_AppCompt) =
        bsonObjectId == other.bsonObjectId
                && nom == other.nom

    override fun hashCode() = Objects.hash(
        bsonObjectId,
        nom,
    )
}
