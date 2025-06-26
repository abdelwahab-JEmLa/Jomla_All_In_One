package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9.Companion.getPushFireBase
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.Z.View.DetailBonVent.View.PeriodGenerator
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
        addOrUpdateData(
            ouvertData!!.copy(
                ouvertClientOnVentKeyId = ouvertClientOnVentKeyId,
                ouvertClientOnVentNom = ouvertClientOnVentNom
            )
        )
    }

    fun ouvrireProduitEtCouleurVent(
        produit: ArticlesBasesStatsTable,
        relatedCouleur: B1CouleurOuGoutProduitDataBase,
    ): Z_AppCompt {
        val data = ouvertData!!.copy(
            ouvertF3ProduitOnVentID = produit.id.toString(),
            ouvertF4CouleurOnVentID = relatedCouleur.key,
        )
        addOrUpdateData(data)
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

    //Section Parent Period Vent - FIXED: Now using dynamic generation
    var ouvertF1PeriodVentId: String = PeriodGenerator.generateCurrentPeriodId(),
    var ouvertF1PeriodVentStartTimesTamp: Long = PeriodGenerator.getPeriodStartTimestamp(),

    //Section Parent Transaction - FIXED: Now using dynamic generation
    var ouvertF2BonVentId: String = PeriodGenerator.generateRandomBonVentId(),
    var ouvertClientOnVentKeyId: String = "",

    //Section ouvertProduitAncien
    var ouvertF3ProduitOnVentID: String = "",
    var ouvertProduitOnVentAncienId: Long = 0L,

    var ouvertF4CouleurOnVentID: String = "",
) {
    init {
        if (nomMutable.isEmpty()) {
            nomMutable = getInitCreationName()
        }

        // Ensure period and bon vent IDs are properly initialized
        if (ouvertF1PeriodVentId.isEmpty() || ouvertF1PeriodVentId == "F1_Juin24_08") {
            ouvertF1PeriodVentId = PeriodGenerator.generateCurrentPeriodId()
        }

        if (ouvertF2BonVentId.isEmpty() || ouvertF2BonVentId == "F2_3omar") {
            ouvertF2BonVentId = if (ouvertClientOnVentNom.isNotEmpty()) {
                PeriodGenerator.generateBonVentId(ouvertClientOnVentNom)
            } else {
                PeriodGenerator.generateRandomBonVentId()
            }
        }

        // Update timestamp to match period if needed
        if (ouvertF1PeriodVentStartTimesTamp == 0L) {
            ouvertF1PeriodVentStartTimesTamp = PeriodGenerator.getPeriodStartTimestamp(ouvertF1PeriodVentId)
        }
    }

    fun getInitCreationName(): String {
        return nom.ifEmpty { "DefaultCompt" }
    }

    /**
     * Create a new period for this account
     */
    fun createNewPeriod(): Z_AppCompt {
        return this.copy(
            ouvertF1PeriodVentId = PeriodGenerator.generateCurrentPeriodId(),
            ouvertF1PeriodVentStartTimesTamp = System.currentTimeMillis(),
            ouvertF2BonVentId = if (ouvertClientOnVentNom.isNotEmpty()) {
                PeriodGenerator.generateBonVentId(ouvertClientOnVentNom)
            } else {
                PeriodGenerator.generateRandomBonVentId()
            },
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    /**
     * Update client and generate new bon vent ID
     */
    fun updateClientAndGenerateNewBon(clientId: String, clientName: String): Z_AppCompt {
        return this.copy(
            ouvertClientOnVentKeyId = clientId,
            ouvertClientOnVentNom = clientName,
            ouvertF2BonVentId = PeriodGenerator.generateBonVentId(clientName),
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    companion object {
        @Deprecated("Use PeriodGenerator.getPeriodStartTimestamp() instead")
        fun creatTimeTampDepuitStr(dateString: String): Long {
            return PeriodGenerator.getPeriodStartTimestamp()
        }

        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/Z_AppCompt"
        )

        /**
         * Create a new Z_AppCompt instance with dynamic values
         */
        fun createWithDynamicValues(
            nom: String = "DefaultCompt",
            clientName: String = ""
        ): Z_AppCompt {
            return Z_AppCompt(
                nom = nom,
                ouvertF1PeriodVentId = PeriodGenerator.generateCurrentPeriodId(),
                ouvertF1PeriodVentStartTimesTamp = System.currentTimeMillis(),
                ouvertF2BonVentId = if (clientName.isNotEmpty()) {
                    PeriodGenerator.generateBonVentId(clientName)
                } else {
                    PeriodGenerator.generateRandomBonVentId()
                },
                ouvertClientOnVentNom = clientName
            )
        }
    }

    override fun equals(other: Any?) =
        this === other || (other is Z_AppCompt && isSameEntity(other))

    fun isSameEntity(other: Z_AppCompt) =
        bsonObjectId == other.bsonObjectId && nom == other.nom

    override fun hashCode() = Objects.hash(bsonObjectId, nom)
}
