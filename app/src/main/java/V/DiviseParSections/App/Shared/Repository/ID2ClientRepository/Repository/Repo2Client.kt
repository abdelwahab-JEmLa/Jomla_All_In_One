package V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter.Companion.withOutFireBaseInvalidCharacters
import V.DiviseParSections.App.Shared.Repository.A.Base.MainSetterFacade.Companion.getListDesParentKeys
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.dataBaseCreationFactoryMID2ClientRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Fonctions.Main.getKeyFireBase
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

@Stable
class Repo2Client(
    val dataBaseCreationFactory: dataBaseCreationFactoryMID2ClientRepository,
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    val zAppComptRepositoryComposable: Repo9AppCompt,
    val id8BonVentRepository: Repo8BonVent,
) {
    val TAG_REPO = "Repo2Client"
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<HClientInfos>>(emptyList())
    val datasState: State<List<HClientInfos>> = this._datas
    val datasValue by derivedStateOf { this._datas.value }

    val onVentId2ClientInfos by derivedStateOf {
        datasValue.find {
            it.keyID ==
                    id8BonVentRepository.onVentId8BonVent?.parentM2ClientInfosKey
        }
    }

    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: State<Float> = _loadingProgress
    val isLoading: Boolean by derivedStateOf { this._datas.value.isEmpty() && !_isInitialized.value }
    private val _isInitialized = mutableStateOf(false)
    val isInitialized: State<Boolean> = _isInitialized

    val isEmpty: Boolean by derivedStateOf { this._datas.value.isEmpty() }
    val size: Int by derivedStateOf { this._datas.value.size }
    val maxId: Long by derivedStateOf {
        this._datas.value.maxOfOrNull { it.id } ?: 0L
    }

    init {
        composScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    val clients =
                        model.b_ClientInfosProtoJuin3Repository?.modelListFlow ?: emptyList()
                    updateClients(clients)
                }
            }
        }

    }

    fun findHClientInfos(id: Long) = datasValue.find { it.id == id }


    fun removeClient(clientId: Long) {
        this._datas.value = this._datas.value.filter { it.id != clientId }
    }

    fun updateClients(newClients: List<HClientInfos>) {
        this._datas.value = newClients
        _isInitialized.value = true
    }

    fun addClient(client: HClientInfos) {
        this._datas.value += client
    }

    fun upsertData(data: HClientInfos) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
        val existingIndex = datasValue.indexOfFirst { it.keyID == dataUpdate.keyID }

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    if (existingIndex >= 0) {
                        this[existingIndex] = dataUpdate
                    } else {
                        add(dataUpdate)
                    }
                }
            }
        }
        ancienRepoUpsertUneDataEtReturnVID(dataUpdate)
    }

    private fun ancienRepoUpsertUneDataEtReturnVID(dataUpdate: HClientInfos) {
        dataBaseCreationFactory.set(dataUpdate)
    }

    fun updateClient(updatedClient: HClientInfos) {
        this._datas.value = this._datas.value.map { client ->
            if (client.id == updatedClient.id)
                updatedClient.withProperKeyFireBaseAndTimeTamp()
            else client
        }
    }

    fun findHClientInfosByKeyDeClient(parentID2ClientKeyByParent: String): HClientInfos {
        return datasValue.find { it.getTempKeyByParent() == parentID2ClientKeyByParent }
            ?: throw IllegalArgumentException("Client not found with keyByParent: $parentID2ClientKeyByParent")
    }

    fun findHClientInfosByKey(key: String): HClientInfos {
        val parentID2ClientKeyByParent = getListDesParentKeys(key)[HClientInfos.keyModel]
        return datasValue.find { it.getTempKeyByParent() == parentID2ClientKeyByParent }
            ?: throw IllegalArgumentException("Client not found with keyByParent: $parentID2ClientKeyByParent")
    }

}

@Entity
data class HClientInfos(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var keyID: String = getPushFireBase(ref),

    var keyByParent: String = "",
    var bsonObjectId: String = BsonObjectId().toHexString(),

    //Infos De Base
    var nom: String = "Non Defini",
    var cretionTimestamps: Long = DatesHandler().getCurrentTimestamps(),
    //Forging Keys

    // Section Etates Mutable
    var numTelephone: String = "",
    var couleur: String = "#FFFFFF",
    var bonDuClientsSu: String = "",
    var currentCreditBalance: Double = 0.0,
    var positionDonClientsList: Int = 0,
    var cUnClientTemporaire: Boolean = true,
    var auFilterFAB: Boolean = false,
    var typeDeSonMagasine: TypeDeSonMagasine = TypeDeSonMagasine.ATAYAT_MOUKASSARAT,
    var clientTypeMode: ClientTypeMode = ClientTypeMode.NEVEAU,

    var caMarqueGpsEstOuvert: Boolean = false,
    var latitude: Double = getCurrentDefaultLatitude(),
    var longitude: Double = getCurrentDefaultLongitude(),
    var title: String = "",
    var snippet: String = "",
    var actuelleEtat: DernierEtatAAffiche = DernierEtatAAffiche.NON_DEFINI,
    //Etates Mutable

    // Section Centralization Valeurs Pour Injection add TOu modules
    var tagCeBonEstOuvertPourComptsIds: String = "",

    // Section keyFireBase et dernierFireBaseUpdateTimestamps
    var keyFireBase: String = "",
    var dernierTimeTampsSynchronisationAvecFireBase: Long = 0,
) {
    fun getTempKeyByParent(): String {
        return this.nom.withOutFireBaseInvalidCharacters()
    }


    enum class DernierEtatAAffiche(val color: Int, val nomArabe: String) {
        NON_DEFINI(android.R.color.holo_orange_light, "غير محدد"),
        ON_MODE_COMMEND_ACTUELLEMENT(android.R.color.holo_green_light, "نشط / متصل"),
        VENDU_A_LUI(android.R.color.holo_purple, ""),
        Cible(android.R.color.holo_red_light, "Cible"),
        CIBLE_PRIORITE_2(android.R.color.holo_orange_dark, "CIBLE_PRIORITE_2"),
        CIBLE_PRIORITE_3(android.R.color.holo_green_light, "CIBLE_PRIORITE_3"),
        CIBLE_POUR_2(android.R.color.holo_blue_dark, "CIBLE_POUR_2"),
        ACHETEUR_NON_DISPO(android.R.color.darker_gray, "الشاري غائب"),
        AVEC_MARCHANDISE(android.R.color.holo_blue_light, "عندو سلعة"),
        FERME(android.R.color.darker_gray, "مغلق"),
        A_EVITE(android.R.color.black, "يتجنب"),
        CLIENT_ABSENT(android.R.color.darker_gray, "عميل غائب"),
    }

    enum class TypeDeSonMagasine(val color: Int, val nomArabe: String) {
        ATAYAT_MOUKASSARAT(android.R.color.holo_green_light, "عطارة ومكسرات"),
        AlIMENTATION_GENERALE(android.R.color.holo_purple, "مواد غذائية")
    }

    enum class ClientTypeMode(
        val icon: ImageVector,
        val color: Color
    ) {
        NEVEAU(
            icon = Icons.Default.Add,
            color = Color.Red
        ),
        ANCIEN(
            icon = Icons.Default.MonetizationOn,
            color = Color.Blue
        ),
        EVITE(
            icon = Icons.Default.Lock,
            color = Color.Gray
        )
    }

    fun withProperKeyFireBaseAndTimeTamp(): HClientInfos {
        val safeKey = keyFireBase.ifEmpty { getKeyFireBase(id, nom) }
        return this.copy(
            keyFireBase = safeKey,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    companion object {
        const val keyModel = "ID2"

        // Helper functions for default location values
        fun getCurrentDefaultLatitude(): Double {
            // Returns 0.0 as default, but can be updated using updateLocationFromCurrentPosition()
            return 0.0
        }

        fun getCurrentDefaultLongitude(): Double {
            // Returns 0.0 as default, but can be updated using updateLocationFromCurrentPosition()
            return 0.0
        }

        fun extractSonKeyByParent(stringAExtractDepuit: String) =
            stringAExtractDepuit.split("--").find { it.startsWith("$keyModel-") }
                ?.removePrefix("$keyModel-")
                ?: if (stringAExtractDepuit.startsWith("$keyModel-")) stringAExtractDepuit.removePrefix(
                    "$keyModel-"
                ).split("--").first() else null

        fun createTestInstance(): List<HClientInfos> {
            return emptyList()
        }

        val parent = Firebase.database.getReference(
            "00_DataPrototype-04-02" +
                    "/_1_developingRef" +
                    "/C_InfosSqlDataBases"
        )

        val ref = parent.child("B_ClientInfosProtoJuin3")

        fun safeRemoveRef(): Unit {
            ref.removeValue()
        }

        fun removeRef(
            preparedData: HClientInfos
        ) {
            ref.child(preparedData.keyFireBase).removeValue()
        }
    }
}
