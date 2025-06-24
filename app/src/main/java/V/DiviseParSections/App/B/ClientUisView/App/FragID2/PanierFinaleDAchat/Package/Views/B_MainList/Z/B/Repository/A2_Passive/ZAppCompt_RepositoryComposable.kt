package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ACentralCompoRepositoryProtoJuin9
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
import java.util.Objects

@Stable
class Z_SubClassFunctionality_ZAppCompt(
    private val centralRepoLazy: Lazy<ACentralCompoRepositoryProtoJuin9>
) {
    val mainRepository = centralRepoLazy.value.zAppComptRepositoryComposable

    fun ouvrirePourCeComptCTransactionCommercial(
        id: String,
        key: String
    ) {
        mainRepository.addOrUpdateData(
            mainRepository.ouvertData!!.copy(
                cTransactionCommercialIdOuvertPourCeCompt = id,
                cTransactionCommercialKeyOuvertPourCeCompt = key
            )
        )
    }

    fun ouvrireCouleurAchatOperationPourCeCompt(
        couleurIdOuvertPourCeCompt: String,
        couleurKeyOuvertPourCeCompt: String
    ) {
        mainRepository.addOrUpdateData(
            mainRepository.ouvertData!!.copy(
                couleurAchateOperationIdOuvertPourCeCompt = couleurIdOuvertPourCeCompt,
                couleurAchateOperationKeyOuvertPourCeCompt = couleurKeyOuvertPourCeCompt
            )
        )
    }

    fun fermeProduitPourCeCompt(
    ) {
        mainRepository.addOrUpdateData(
            mainRepository.ouvertData!!.copy(
                couleurAchateOperationIdOuvertPourCeCompt = "",
                couleurAchateOperationKeyOuvertPourCeCompt = ""
            )
        )
    }
}

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
        val ouvertEPeriodVentStartDate = ouvertData!!.ouvertEPeriodVentStartDateTime

        addOrUpdateData(
            ouvertData!!.copy(
                ouvertBonVentKeyId = "$ouvertEPeriodVentStartDate -<($ouvertClientOnVentNom)",
                ouvertClientOnVentKeyId = ouvertClientOnVentKeyId,
                ouvertClientOnVentNom = ouvertClientOnVentNom
            )
        )
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

    // Section Centralization Valeurs Pour Injection add TOu modules
    var idClientOuSonMarqueMapEstOuvert: Long = 0L,

    var cTransactionCommercialIdOuvertPourCeCompt: String = BsonObjectId().toHexString(),
    var cTransactionCommercialKeyOuvertPourCeCompt: String = BsonObjectId().toHexString(),

    // Section Paramaters App telephone
    var mainInitDataBaseProgressEtate: Float = 0f,

    var couleurAchateOperationIdOuvertPourCeCompt: String = "",
    var couleurAchateOperationKeyOuvertPourCeCompt: String = "",

    //-----------------Vent Createur-----------

    //Section Parent Period Vent
    var ouvertEPeriodVentKeyId: String = "p1",
    var ouvertEPeriodVentStartDateTime: String = "Juin-24 -<(08:00 AM)",

    //Section Parent Transaction
    var ouvertBonVentKeyId: String = "",
    var ouvertClientOnVentKeyId: String = "",
    var ouvertClientOnVentNom: String = "",

    //Section ouvertProduitAncien
    var ouvertProduitOnVentKeyID: String = "",
    var ouvertProduitOnVentAncienId: Long = 0L,
    var ouvertProduitOnVentNom: String = "",

    var ouvertCouleurOnVentObjID: String = "",
    var ouvertCouleurOnVentNomImageFichie: String = "",
) {
    override fun equals(other: Any?) =
        this === other || (other is Z_AppCompt && isSameEntity(other))

    fun isSameEntity(other: Z_AppCompt) =
        bsonObjectId == other.bsonObjectId
                && nom == other.nom

    override fun hashCode() = Objects.hash(
        bsonObjectId,
        nom,
    )

    companion object {
        val caRef = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/Z_AppCompt"
        )
    }
}
