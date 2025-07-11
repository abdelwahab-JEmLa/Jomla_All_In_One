package V.DiviseParSections.App.Shared.Repository.IDKeyModel11.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.centralRef
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

@Stable
class Repo11AchatOperation(
    val getterFocusedValues: FocusedValuesGetter,
    val fVentCouleurOperationRepository: Repo10OperationVentCouleur,
) {
    private val sourceDatas by derivedStateOf {
        getterFocusedValues
            .filtered_ListM10Vent_BY_Curr_M14VentPeriod_AND_travailleChezGrossisst3Ali
    }

    val datasValue by derivedStateOf { initImplimentaion() }

    sealed class FilterQuery {
        data object NO_FILTER : FilterQuery()
        data class Client(val m2Client: M2Client) : FilterQuery()
    }

    private val _filterQuery = mutableStateOf(FilterQuery.NO_FILTER)
    val filterQuery get() = _filterQuery

    val filteredDatas by derivedStateOf {
        when (val currentFilter = filterQuery.value) {
          /*  is FilterQuery.Client -> {      //->
                //TODO(FIXME):Fix erreur Incompatible types: Repo11AchatOperation.FilterQuery.Client and Repo11AchatOperation.FilterQuery.NO_FILTER
                datasValue.filter { data ->
                    data.listFCouleurVentOperation.any { operation ->
                        operation.parentClientInfosKeyID == currentFilter.m2Client.keyID
                    }
                }
            }
            FilterQuery.NO_FILTER -> datasValue      */
        }
    }

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
        fun generePushKey() =
            centralRef.push().key ?: throw IllegalStateException("Failed to generate Firebase key")
    }
}
