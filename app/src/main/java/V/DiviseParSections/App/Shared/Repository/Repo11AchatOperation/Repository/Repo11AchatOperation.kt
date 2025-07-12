package V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.centralRef
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.Repo15.Repository.M15Grossist
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase15.Factory.DataBaseInitFactory_15Grossist
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class Repo11AchatOperation(
    val context: Context,
    val dataBaseCreationFactory: DataBaseInitFactory_15Grossist,

    val getterFocusedValues: FocusedValuesGetter,
    val fVentCouleurOperationRepository: Repo10OperationVentCouleur,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M15Grossist>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    init {
        composScope.launch {
            dataBaseCreationFactory.dao.getAllFlow().collect { _datas.value = it }
        }
    }


    fun add_New(data: M15Grossist) {
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

    fun update_If_Exist(data: M15Grossist) {
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

    fun delete(data: M15Grossist) {
        composScope.launch {
            try {
                _datas.value = datasValue.filter { it.keyID != data.keyID }
                dataBaseCreationFactory.delete(data)
            } catch (e: Exception) {
            }
        }
    }


     val sourceDatas by derivedStateOf {
        getterFocusedValues
            .filtered_ListM10Vent_BY_Curr_M14VentPeriod
    }


    sealed class FilterQuery {
        data object NO_FILTER : FilterQuery()
        data class Client(val m2Client: M2Client) : FilterQuery()
    }

    private val _filterQuery = mutableStateOf(FilterQuery.NO_FILTER)
    val filterQuery get() = _filterQuery

    val bProduitKeyIDToListKAchatCouleurOperation by derivedStateOf {
        datasValue.groupBy {
            it.listFCouleurVentOperation.first()
                .parentM1ProduitInfosKeyId
        }
    }

    private fun initImplimentaion(): List<M11AchatOperation> {
        val operations = sourceDatas.groupBy { it.parentM3CouleurProduitInfosKeyID }

        return operations.map { (couleurKeyId, ventOperations) ->
            val totalQuantity = ventOperations.sumOf { it.quantity }

            M11AchatOperation(
                parentCouleurInfosKeyID = couleurKeyId,
                sumAchatQantity = totalQuantity,
                listFCouleurVentOperation = ventOperations
            )
        }
    }
}

data class M11AchatOperation(
    val keyID: String = generePushKey(),
    val parentCouleurInfosKeyID: String,
    val parentGrossistKeyID: String = generePushKey(),
    val sumAchatQantity: Int,
    val listFCouleurVentOperation: List<M10OperationVentCouleur>
) {
    companion object {
        val ref = centralRef.child("M11AchatOperation")

        fun generePushKey() = ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")

    }
}
