package V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.withOutFireBaseInvalidCharacters
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter.Companion.genereUnPushKeyFireBase
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter.Companion.getListDesParentKeys
import V.DiviseParSections.App.Shared.Repository.A.Base.functions_central.runtime_throw_Erreur_Pour_Regle_Le_Real_Bug
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Init.initializeDataReturn
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.dataBaseCreationFactoryMID2ClientRepository
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase02.Factory.DataBaseInitFactory_2ClientProtoJuil28
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
    val dataBaseCreationFactoryProtoJuil28: DataBaseInitFactory_2ClientProtoJuil28,
    val dataBaseCreationFactory: dataBaseCreationFactoryMID2ClientRepository,
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    val zAppComptRepositoryComposable: Repo9AppCompt,
) {
    val TAG = "Repo2Client"
    private val repoScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M2Client>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    init {
        repoScope.launch {
            dataBaseCreationFactoryProtoJuil28.dao.getAllFlow().collect { _datas.value = it }
        }
    }
    fun refresh_Datas() {
        repoScope.launch {
            try {
                dataBaseCreationFactory.dao.deleteAll()

                withContext(Dispatchers.Main.immediate) {
                    _datas.value = emptyList()
                }

                val freshDataFromFirebase = dataBaseCreationFactory.initializeDataReturn()

                dataBaseCreationFactory.dao.insertAll(freshDataFromFirebase)

                withContext(Dispatchers.Main.immediate) {
                    _datas.value = freshDataFromFirebase
                }

            } catch (e: Exception) {
            }
        }
    }


    fun addNew(data: M2Client) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        repoScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    add(dataUpdate)
                }
            }
        }

        dataBaseCreationFactory.set(dataUpdate)
    }

    fun updateIfExist(data: M2Client) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ancien.keyID == data.keyID
        }

        if (existingIndex < 0) {
            repoScope.launch {
                withContext(Dispatchers.Main) {
                    runtime_throw_Erreur_Pour_Regle_Le_Real_Bug("updateIfExist")
                }
            }
            return
        }

        val updatedItem = data.copy(
            keyID = datasValue[existingIndex].keyID,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )

        repoScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = datasValue.toMutableList().apply {
                    this[existingIndex] = updatedItem
                }
            }
        }

        dataBaseCreationFactory.set(updatedItem)
    }

    //--------------------------------------------------------------------------
    fun removeClient(clientId: Long) {
        this._datas.value = this._datas.value.filter { it.id != clientId }
    }

    // In Repo2Client class (first file)
    fun delete_M2Client(data: M2Client) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ancien.keyID == data.keyID
        }

        if (existingIndex < 0) {
            repoScope.launch {
                withContext(Dispatchers.Main) {
                    runtime_throw_Erreur_Pour_Regle_Le_Real_Bug("delete_M2Client: Client not found")
                }
            }
            return
        }

        repoScope.launch {
            // Remove from UI state
            withContext(Dispatchers.Main.immediate) {
                _datas.value = datasValue.toMutableList().apply {
                    removeAt(existingIndex)
                }
            }
        }

        // Delete from database
        dataBaseCreationFactory.delete(data)
    }
    
    fun updateClients(newClients: List<M2Client>) {
        this._datas.value = newClients
        _isInitialized.value = true
    }

    fun addClient(client: M2Client) {
        this._datas.value += client
    }

    fun upsert(data: M2Client) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        val existingIndex = datasValue.indexOfFirst { it.keyID == dataUpdate.keyID }

        repoScope.launch {
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

    private fun ancienRepoUpsertUneDataEtReturnVID(dataUpdate: M2Client) {
        dataBaseCreationFactory.set(dataUpdate)
    }

    fun updateClient(updatedClient: M2Client) {
        this._datas.value = this._datas.value.map { client ->
            if (client.id == updatedClient.id)
                updatedClient.with_Trigger_RealTime()
                    .copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
            else client
        }
    }

    fun findHClientInfosByKeyDeClient(parentID2ClientKeyByParent: String): M2Client {
        return datasValue.find { it.getTempKeyByParent() == parentID2ClientKeyByParent }
            ?: throw IllegalArgumentException("Client not found with keyByParent: $parentID2ClientKeyByParent")
    }

    fun findHClientInfosByKey(key: String): M2Client {
        val parentID2ClientKeyByParent = getListDesParentKeys(key)[M2Client.keyModel]
        return datasValue.find { it.getTempKeyByParent() == parentID2ClientKeyByParent }
            ?: throw IllegalArgumentException("Client not found with keyByParent: $parentID2ClientKeyByParent")
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
    val datasState: State<List<M2Client>> = _datas
}
@Entity
data class M2Client(
    @PrimaryKey
    var keyID: String = generePushKey(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = 0,
    var creationTimestamps: Long = System.currentTimeMillis(),
    //Infos De Base
    var nom: String = "Non Defini",
    var cretionTimestamps: Long = DatesHandler().getCurrentTimestamps(),
    //Forging Keys
    var its_Fournisseur: Boolean = false,
    var parentComptCreateurKEyID: String = "",
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
    var edite_Exact_Gps_est_fait: Boolean = false,
    // Section Centralization Valeurs Pour Injection add_New TOu modules
    var tagCeBonEstOuvertPourComptsIds: String = "",
    // Section keyFireBase et dernierFireBaseUpdateTimestamps
    var id: Long = 0L,
    var keyByParent: String = "",
    var bsonObjectId: String = BsonObjectId().toHexString(),

    val nomPrenomArabe: String = "حمنيش عبد الوهاب",
    val register_Commerce_Nm: String = "16/00 – 5138424 D20",
    val nif_Num: String = "16291403036"
) {
    /**
     * Get Arabic name with fallback to French name
     */
    fun getNomAffichage(): String {
        return nomPrenomArabe.takeIf { it.isNotBlank() } ?: nom
    }

    /**
     * Get full display name with both French and Arabic if available
     */
    fun getNomComplet(): String {
        return if (nomPrenomArabe.isBlank()) {
            nom
        } else {
            "$nom ($nomPrenomArabe)"
        }
    }


    fun get_DebugInfos(): String {
        return buildString {
            append("(M2=")
            append(nom)
            append("[")
            append(keyID.takeLast(3).uppercase())
            append("])")
        }
    }

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

    fun with_Trigger_RealTime(): M2Client {
        return this.copy(
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    companion object {
        fun generePushKey() = genereUnPushKeyFireBase(ref)
        const val keyModel = "ID2"

        fun getCurrentDefaultLatitude(): Double {
            return 0.0
        }

        fun extractClientNamePrefix(clientName: String): String {
            return clientName.substringBefore(".", clientName).trim()
        }

        fun getCurrentDefaultLongitude(): Double {
            return 0.0
        }

        val parent = Firebase.database.getReference(
            "00_DataPrototype-04-02" +
                    "/_1_developingRef" +
                    "/C_InfosSqlDataBases"
        )
        val ref = parent.child("B_ClientInfosProtoJuin3")

        fun safe_Remove_MainDatas_Ref(onDone: () -> Unit = {}) {
            ref.removeValue().addOnSuccessListener { onDone() }
        }

        fun removeRef(preparedData: M2Client) {
            ref.child(preparedData.keyID).removeValue()
        }

        fun get_default(): M2Client {
            return M2Client()
        }
    }
}
