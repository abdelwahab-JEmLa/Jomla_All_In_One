package V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.centralRef
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase11.Factory.DataBaseInitFactory_11AchatOperation
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class Repo11AchatOperation(
    private val context: Context,
    private val dataBaseCreationFactory: DataBaseInitFactory_11AchatOperation,
    val repo10OperationVentCouleur: Repo10OperationVentCouleur,
    private val repo8BonVent: Repo8BonVent,
    repo14VentPeriode: Repo14VentPeriode,
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M11AchatOperation>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }
    val currentFilterQuery by derivedStateOf { _filterQuery.value }

    sealed class FilterQuery {
        data object NO_FILTER : FilterQuery()
        data class F14VentPeriode(val m14VentPeriode: M14VentPeriode) : FilterQuery()
        data class Grossist(val m15Grossist: M15Grossist) : FilterQuery()
        data class Client(val m2Client: M2Client) : FilterQuery()

        // NEW: Combined filters for multiple criteria
        data class Combined(
            val period: M14VentPeriode? = null,
            val grossist: M15Grossist? = null,
            val client: M2Client? = null
        ) : FilterQuery()
    }

    private val _filterQuery = mutableStateOf<FilterQuery>(FilterQuery.NO_FILTER)

    init {
        scope.launch {
            dataBaseCreationFactory.dao.getAllFlow().collect { _datas.value = it }
        }
    }

    private fun isValidKey(key: String) = key.isNotBlank() && key != "null" && key.length > 5

    private fun isValidAchat(achat: M11AchatOperation) =
        isValidKey(achat.parent_M3CouleurProduit_KeyID) &&
                isValidKey(achat.parent_M1Produit_KeyID) &&
                (achat.parent_M15Grossist_KeyID.isBlank() || achat.parent_M15Grossist_KeyID == "null" || isValidKey(achat.parent_M15Grossist_KeyID)) &&
                achat.sumAchatQantity >= 0 &&
                achat.prix_Achat_De_Cette_Grossist >= 0.0

    private fun getValidatedData() = datasValue.filter { isValidAchat(it) }

    val filteredDatasValue by derivedStateOf {
        val validData = getValidatedData()
        when (val filter = _filterQuery.value) {
            FilterQuery.NO_FILTER -> validData

            is FilterQuery.F14VentPeriode -> validData.filter {
                it.parent_M14VentPeriod_KeyID == filter.m14VentPeriode.keyID
            }

            is FilterQuery.Grossist -> validData.filter {
                it.parent_M15Grossist_KeyID == filter.m15Grossist.keyID
            }

            is FilterQuery.Client -> validData.filter { achat ->
                achat.get_list_v_Depuit_joinedStringKeys(repo10OperationVentCouleur.datasValue)
                    .any { sales ->
                        repo8BonVent.datasValue.find { it.keyID == sales.parent_M8BonVent_KeyId }
                            ?.parent_M2Client_KeyID == filter.m2Client.keyID
                    }
            }

            // NEW: Handle combined filters
            is FilterQuery.Combined -> {
                var filteredData = validData

                // Apply period filter if present
                filter.period?.let { period ->
                    filteredData = filteredData.filter {
                        it.parent_M14VentPeriod_KeyID == period.keyID
                    }
                }

                // Apply grossist filter if present
                filter.grossist?.let { grossist ->
                    filteredData = filteredData.filter {
                        it.parent_M15Grossist_KeyID == grossist.keyID
                    }
                }

                // Apply client filter if present
                filter.client?.let { client ->
                    filteredData = filteredData.filter { achat ->
                        achat.get_list_v_Depuit_joinedStringKeys(repo10OperationVentCouleur.datasValue)
                            .any { sales ->
                                repo8BonVent.datasValue.find { it.keyID == sales.parent_M8BonVent_KeyId }
                                    ?.parent_M2Client_KeyID == client.keyID
                            }
                    }
                }

                filteredData
            }
        }
    }

    fun updateFilterQuery(filter: FilterQuery) {
        _filterQuery.value = filter
    }

    // NEW: Helper methods for combined filtering
    fun addPeriodFilter(period: M14VentPeriode) {
        val currentFilter = _filterQuery.value
        _filterQuery.value = when (currentFilter) {
            is FilterQuery.Combined -> currentFilter.copy(period = period)
            is FilterQuery.Grossist -> FilterQuery.Combined(period = period, grossist = currentFilter.m15Grossist)
            is FilterQuery.Client -> FilterQuery.Combined(period = period, client = currentFilter.m2Client)
            else -> FilterQuery.Combined(period = period)
        }
    }

    fun addGrossistFilter(grossist: M15Grossist) {
        val currentFilter = _filterQuery.value
        _filterQuery.value = when (currentFilter) {
            is FilterQuery.Combined -> currentFilter.copy(grossist = grossist)
            is FilterQuery.F14VentPeriode -> FilterQuery.Combined(period = currentFilter.m14VentPeriode, grossist = grossist)
            is FilterQuery.Client -> FilterQuery.Combined(grossist = grossist, client = currentFilter.m2Client)
            else -> FilterQuery.Combined(grossist = grossist)
        }
    }

    fun addClientFilter(client: M2Client) {
        val currentFilter = _filterQuery.value
        _filterQuery.value = when (currentFilter) {
            is FilterQuery.Combined -> currentFilter.copy(client = client)
            is FilterQuery.F14VentPeriode -> FilterQuery.Combined(period = currentFilter.m14VentPeriode, client = client)
            is FilterQuery.Grossist -> FilterQuery.Combined(grossist = currentFilter.m15Grossist, client = client)
            else -> FilterQuery.Combined(client = client)
        }
    }

    fun removePeriodFilter() {
        val currentFilter = _filterQuery.value
        if (currentFilter is FilterQuery.Combined) {
            val newFilter = currentFilter.copy(period = null)
            _filterQuery.value = when {
                newFilter.grossist != null && newFilter.client == null -> FilterQuery.Grossist(newFilter.grossist)
                newFilter.client != null && newFilter.grossist == null -> FilterQuery.Client(newFilter.client)
                newFilter.grossist == null && newFilter.client == null -> FilterQuery.NO_FILTER
                else -> newFilter
            }
        } else if (currentFilter is FilterQuery.F14VentPeriode) {
            _filterQuery.value = FilterQuery.NO_FILTER
        }
    }

    fun removeGrossistFilter() {
        val currentFilter = _filterQuery.value
        if (currentFilter is FilterQuery.Combined) {
            val newFilter = currentFilter.copy(grossist = null)
            _filterQuery.value = when {
                newFilter.period != null && newFilter.client == null -> FilterQuery.F14VentPeriode(newFilter.period)
                newFilter.client != null && newFilter.period == null -> FilterQuery.Client(newFilter.client)
                newFilter.period == null && newFilter.client == null -> FilterQuery.NO_FILTER
                else -> newFilter
            }
        } else if (currentFilter is FilterQuery.Grossist) {
            _filterQuery.value = FilterQuery.NO_FILTER
        }
    }

    fun removeClientFilter() {
        val currentFilter = _filterQuery.value
        if (currentFilter is FilterQuery.Combined) {
            val newFilter = currentFilter.copy(client = null)
            _filterQuery.value = when {
                newFilter.period != null && newFilter.grossist == null -> FilterQuery.F14VentPeriode(newFilter.period)
                newFilter.grossist != null && newFilter.period == null -> FilterQuery.Grossist(newFilter.grossist)
                newFilter.period == null && newFilter.grossist == null -> FilterQuery.NO_FILTER
                else -> newFilter
            }
        } else if (currentFilter is FilterQuery.Client) {
            _filterQuery.value = FilterQuery.NO_FILTER
        }
    }

    fun clearAllFilters() {
        _filterQuery.value = FilterQuery.NO_FILTER
    }

    // NEW: Helper to get current active filters
    fun getCurrentActiveFilters(): Triple<M14VentPeriode?, M15Grossist?, M2Client?> {
        return when (val filter = _filterQuery.value) {
            is FilterQuery.Combined -> Triple(filter.period, filter.grossist, filter.client)
            is FilterQuery.F14VentPeriode -> Triple(filter.m14VentPeriode, null, null)
            is FilterQuery.Grossist -> Triple(null, filter.m15Grossist, null)
            is FilterQuery.Client -> Triple(null, null, filter.m2Client)
            FilterQuery.NO_FILTER -> Triple(null, null, null)
        }
    }

    fun genere_Achats_Depuit_M11AchatOperation_List(
        m14VentPeriod: M14VentPeriode?,
        filtered_ListM10Vent_BY_Curr_M14VentPeriod: List<M10OperationVentCouleur>,
        produits: List<ArticlesBasesStatsTable>
    ): List<M11AchatOperation> {
        return filtered_ListM10Vent_BY_Curr_M14VentPeriod
            .groupBy { it.parent_M3CouleurProduit_KeyID }
            .mapNotNull { (couleurId, vents) ->
                if (!isValidKey(couleurId)) return@mapNotNull null

                val quantity = vents.sumOf { it.quantity }
                if (quantity <= 0) return@mapNotNull null

                val lastAchat = datasValue.filter { it.parent_M3CouleurProduit_KeyID == couleurId }
                    .maxByOrNull { it.creationTimestamp }

                val produitId = vents.firstOrNull()?.parent_M1Produit_KeyId
                if (!isValidKey(produitId ?: "")) return@mapNotNull null

                val produit = produits.find { it.keyID == produitId }

                M11AchatOperation.get_default().first.copy(
                    prix_Achat_De_Cette_Grossist = produit?.prixAchat ?: 0.0,
                    parent_M15Grossist_DebugInfos = lastAchat?.parent_M15Grossist_DebugInfos ?: "Non Defini Gros",
                    parent_M15Grossist_KeyID = lastAchat?.parent_M15Grossist_KeyID ?: "-OUzoKE1ANl4Kt2ESAB6",
                    parent_M14VentPeriod_KeyID = m14VentPeriod?.keyID ?: "null",
                    parent_M1Produit_DebugInfos = vents.firstOrNull()?.parent_M1Produit_DebugInfos ?: "Unknown Product",
                    parent_M1Produit_KeyID = produitId ?: "null",
                    parent_M3CouleurProduit_DebugInfos = vents.firstOrNull()?.parent_M3CouleurProduit_DebugInfos ?: "Unknown Color",
                    parent_M3CouleurProduit_KeyID = couleurId,
                    sumAchatQantity = quantity,
                    joined_Str_keys_De_Relatives_FCouleurVentOperation = vents.joinToString(",") { it.keyID } ,
                    joined_Str_keys_List_M10Vent_NonDispo_Que_Parent_Non_Trouve =vents
                        .filter { it.its_Linked_To_Autre_Vent_Si_NonDispo }
                        .joinToString(",") { it.keyID },
                )
            }
    }

    fun add_New(data: M11AchatOperation) {
        val updated = data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
        scope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value += updated
            }
            dataBaseCreationFactory.addOrUpdatedAncienRepo(-1, updated)
        }
    }

    fun update_If_Exist(data: M11AchatOperation) {
        val index = datasValue.indexOfFirst { it.keyID == data.keyID }
        if (index < 0) {
            scope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Item not found, cannot update", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }

        val updated = data.copy(
            keyID = datasValue[index].keyID,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )

        scope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = datasValue.toMutableList().apply { this[index] = updated }
            }
            dataBaseCreationFactory.addOrUpdatedAncienRepo(index, updated)
        }
    }

    fun deleteMulti(data: List<M11AchatOperation>) {
        scope.launch {
            val keyIds = data.map { it.keyID }.toSet()
            _datas.value = datasValue.filter { it.keyID !in keyIds }
            data.forEach { dataBaseCreationFactory.delete(it) }
        }
    }

    fun delete(data: M11AchatOperation) {
        scope.launch {
            _datas.value = datasValue.filter { it.keyID != data.keyID }
            dataBaseCreationFactory.delete(data)
        }
    }
}

@Entity
data class M11AchatOperation(
    @PrimaryKey
    val keyID: String = generePushKey(),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    //---------------------------------ForgingKeys.----------------------------------------------------------------------------------------------------------------------------------
    val parent_M14VentPeriod_DebugInfos: String = "null",
    val parent_M14VentPeriod_KeyID: String = "null",
    //---------------------------------ForgingKeys.----------------------------------------------------------------------------------------------------------------------------------
    val parent_M1Produit_DebugInfos: String = "null",
    val parent_M1Produit_KeyID: String = "null",
    //---------------------------------ForgingKeys.----------------------------------------------------------------------------------------------------------------------------------
    val parent_M3CouleurProduit_DebugInfos: String = "null",
    val parent_M3CouleurProduit_KeyID: String = "null",
    //---------------------------------ForgingKeys.----------------------------------------------------------------------------------------------------------------------------------
    val parent_M15Grossist_DebugInfos: String = "null",
    val parent_M15Grossist_KeyID: String = "null",
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------

    val prix_Achat_De_Cette_Grossist: Double = 0.0,
    val sumAchatQantity: Int = 0,
    val joined_Str_keys_De_Relatives_FCouleurVentOperation: String = "",
    val joined_Str_keys_List_M10Vent_NonDispo_Que_Parent_Non_Trouve :String = "",
) {
    fun get_DebugInfos(): String = "(M11=[${keyID.takeLast(3).uppercase()}])"

    fun get_list_v_Depuit_joinedStringKeys(repo10datas: List<M10OperationVentCouleur>): List<M10OperationVentCouleur> {
        return if (joined_Str_keys_De_Relatives_FCouleurVentOperation.isBlank()) {
            emptyList()
        } else {
            val keyIds = joined_Str_keys_De_Relatives_FCouleurVentOperation
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() && it != "null" }

            repo10datas.filter { it.keyID in keyIds }
        }
    }

    fun get_Vents_Depuit_joined_Str_keys_List_M10Vent_NonDispo_Que_Parent_Non_Trouve(repo10datas: List<M10OperationVentCouleur>): List<M10OperationVentCouleur> {
        return if (joined_Str_keys_List_M10Vent_NonDispo_Que_Parent_Non_Trouve.isBlank()) {
            emptyList()
        } else {
            val keyIds = joined_Str_keys_List_M10Vent_NonDispo_Que_Parent_Non_Trouve
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() && it != "null" }

            repo10datas.filter { it.keyID in keyIds }
        }
    }

    companion object {
        val ref = centralRef.child("Datas_11AchatOperation")

        fun generePushKey() = ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")

        fun get_default(): Pair<M11AchatOperation, Modifier> {
            val data = M11AchatOperation()
            val modifier = Modifier.getSemanticsTag(nomVal = "m11AchatOperation", data = data)
            return Pair(data, modifier)
        }

        fun find_depuit_DB(data_List: List<M11AchatOperation>, data_A_Cherche: M11AchatOperation) =
            data_List.find { it.keyID == data_A_Cherche.keyID }
    }
}
