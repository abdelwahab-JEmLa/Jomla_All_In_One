package V.DiviseParSections.App.Shared.Repository

import Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.A_ProduitDataBaseProtoJuin17
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class BProduitInfosRepository(
    val ancienRepo: A_ProduitDataBaseProtoJuin17,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<ArticlesBasesStatsTable>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    val ouvertData by derivedStateOf {}

    init {
        composScope.launch {
            dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun addOrUpdateData(data: ArticlesBasesStatsTable) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ArticlesBasesStatsTable.compareEntre(ancien = ancien, newData = data)
        }
        _datas.value = if (existingIndex >= 0) {
            datasValue.toMutableList().apply {
                this[existingIndex] = this[existingIndex].copy(
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
            }
        } else {
            datasValue + data
        }

        addOrUpdatedAncienRepo(existingIndex, data)
    }

    fun deleteData(data: ArticlesBasesStatsTable) {
        _datas.value = datasValue.filter { existing ->
            !ArticlesBasesStatsTable.compareEntre(ancien = existing, newData = data)
        }
        deleteDataAncienRepo(data)
    }

    private fun addOrUpdatedAncienRepo(
        existingIndex: Int,
        data: ArticlesBasesStatsTable
    ) {
        composScope.launch {
            ancienRepo.addOrUpdatedAncienRepo(existingIndex, data)
        }
    }

    private fun deleteDataAncienRepo(
        data: ArticlesBasesStatsTable
    ) {
        composScope.launch {
            ancienRepo.deleteDataAncienRepo(data)
        }
    }

    companion object {
        fun ArticlesBasesStatsTable?.logDebugIt(nomVale: String = "") {
            Log.d(
                "ArticlesBasesStatsTable",
                infos(nomVale)
            )
        }

        private fun ArticlesBasesStatsTable?.infos(
            nomVale: String
        ) = nomVale + if (this != null) {
            keyID
            "\n id = $id "
            "\n keyID = $keyID "
        } else {
            "data is null"
        }
    }
}

