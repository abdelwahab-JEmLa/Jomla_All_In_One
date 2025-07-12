package V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.centralRef
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
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
    val context: Context,
    val dataBaseCreationFactory: DataBaseInitFactory_11AchatOperation,
    val repo10OperationVentCouleur: Repo10OperationVentCouleur,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M11AchatOperation>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    init {
        composScope.launch {
            dataBaseCreationFactory.dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun add_New(data: M11AchatOperation) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    add(dataUpdate)
                }
            }
        }

        dataBaseCreationFactory.addOrUpdatedAncienRepo(-1, data)
    }

    fun update_If_Exist(data: M11AchatOperation) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ancien.keyID == data.keyID
        }

        if (existingIndex < 0) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Item not found, cannot update", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            return
        }

        val updatedItem = data.copy(
            keyID = datasValue[existingIndex].keyID,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = datasValue.toMutableList().apply {
                    this[existingIndex] = updatedItem
                }
            }
        }

        dataBaseCreationFactory.addOrUpdatedAncienRepo(existingIndex, data)
    }

    fun delete(data: M11AchatOperation) {
        composScope.launch {
            try {
                _datas.value = datasValue.filter { it.keyID != data.keyID }
                dataBaseCreationFactory.delete(data)
            } catch (e: Exception) {
            }
        }
    }

    sealed class FilterQuery {
        data object NO_FILTER : FilterQuery()
        data class Client(val m2Client: M2Client) : FilterQuery()
    }

    private val _filterQuery = mutableStateOf(FilterQuery.NO_FILTER)
    val filterQuery get() = _filterQuery

    val bProduitKeyID_To_List_KAchatCouleurOperation by derivedStateOf {
        datasValue.groupBy {
            it.get_list_v_Depuit_joinedStringKeys(repo10OperationVentCouleur.datasValue).first()
                .parentM1ProduitInfosKeyId
        }
    }


    fun genere_Achats_Depuit_M11AchatOperation_List(
        M14VentPeriod: M14VentPeriode?,
        filtered_ListM10Vent_BY_Curr_M14VentPeriod: List<M10OperationVentCouleur>
    ): List<M11AchatOperation> {
        val operations = filtered_ListM10Vent_BY_Curr_M14VentPeriod.groupBy {
            it.parentM3CouleurProduitInfosKeyID
        }

        val newAchatOperations = operations.map { (couleurKeyId, ventOperations) ->
            val totalQuantity = ventOperations.sumOf { it.quantity }

            M11AchatOperation.get_default().first.copy(
                parent_M14VentPeriod_KeyID = M14VentPeriod?.keyID ?: "null",
                parent_M1Produit_DebugInfos = ventOperations.first().parentM1ProduitDebugInfos,
                parent_M1Produit_KeyID = ventOperations.first().parentM1ProduitInfosKeyId,
                parent_M3CouleurProduit_DebugInfos = ventOperations.first().parentM3CouleurProduitDebugInfos,
                parent_M3CouleurProduit_KeyID = couleurKeyId,
                sumAchatQantity = totalQuantity,
                joinedStrkeys_De_Relatives_FCouleurVentOperation = ventOperations.joinToString(",") { it.keyID }
            )
        }

        return newAchatOperations
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

    val sumAchatQantity: Int = 0,
    val joinedStrkeys_De_Relatives_FCouleurVentOperation: String = ","
) {
    fun get_DebugInfos(): String {
        return buildString {
            append("(M11=")
            append("")
            append("[")
            append(keyID.takeLast(3).uppercase())
            append("])")
        }
    }

    fun get_list_v_Depuit_joinedStringKeys(repo10datas: List<M10OperationVentCouleur>): List<M10OperationVentCouleur> {
        return if (joinedStrkeys_De_Relatives_FCouleurVentOperation.isBlank() ||
            joinedStrkeys_De_Relatives_FCouleurVentOperation == ","
        ) {
            emptyList()
        } else {
            val keyIds = joinedStrkeys_De_Relatives_FCouleurVentOperation
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            repo10datas.filter { operation ->
                operation.keyID in keyIds
            }
        }
    }

    companion object {
        val ref = centralRef.child("Datas_11AchatOperation")

        fun generePushKey() =
            ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")

        fun get_default(
        ): Pair<M11AchatOperation, Modifier> {
            val data = M11AchatOperation()
            val modifier = Modifier.getSemanticsTag(
                nomVal = "m11AchatOperation",
                data = data
            )
            return Pair(data, modifier)
        }

        fun find_depuit_DB(
            data_List: List<M11AchatOperation>,
            data_A_Cherche: M11AchatOperation,
        ) = data_List
            .find { data ->
                data.keyID == data_A_Cherche.keyID
            }

    }
}
