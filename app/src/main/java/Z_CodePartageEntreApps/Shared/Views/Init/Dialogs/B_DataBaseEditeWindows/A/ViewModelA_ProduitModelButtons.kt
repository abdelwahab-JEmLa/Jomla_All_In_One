package Z_CodePartageEntreApps.Shared.Views.Init.Dialogs.B_DataBaseEditeWindows.A

import Z_CodePartageEntreApps.Model.A_ProduitModel
import Z_CodePartageEntreApps.Model.A_ProduitModelNewProto.Repository.A_ProduitModelRepository
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.Parent.ProduitsAncienDataBaseMain
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ViewModelA_ProduitModelButtons(
    private val a_ProduitModelRepository: A_ProduitModelRepository
) : ViewModel() {
    val a_ProduitModel = a_ProduitModelRepository.modelDatas
    var produitsAncienDataBaseMains: List<ProduitsAncienDataBaseMain> by mutableStateOf(emptyList())

    init {
        viewModelScope.launch {
            produitsAncienDataBaseMains = implimentProduitsAncienDataBaseMains()
        }
    }

    private suspend fun implimentProduitsAncienDataBaseMains(): List<ProduitsAncienDataBaseMain> = withContext(Dispatchers.IO) {
        return@withContext try {
            Firebase.database
                .getReference("e_DBJetPackExport")
                .get()
                .await()
                .children
                .mapNotNull {
                    it.getValue(ProduitsAncienDataBaseMain::class.java)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving ancien produits: ${e.message}", e)
            emptyList()
        }
    }

    fun updateData(produit: A_ProduitModel) {
        a_ProduitModelRepository.updateData(produit)
    }

    suspend fun updateMultiDatas(a_Produitsl: SnapshotStateList<A_ProduitModel>) {
        a_ProduitModelRepository.updateDatas(a_Produitsl)
    }
     //<--
     //TODO(1): cree une fun qui add au fire base Z_BakupksModels /TiggersBakups/"A_ProduitModel" bolean
     //au tiggere ca ce change son etate
    //-->
    //TODO(2): cre une autre fun si Z_BakupksModels /TiggersBakups/"A_ProduitModel = true


}
