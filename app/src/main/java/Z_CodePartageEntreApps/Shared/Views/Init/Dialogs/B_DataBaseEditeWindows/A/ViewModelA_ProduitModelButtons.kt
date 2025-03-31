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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewModelA_ProduitModelButtons(
    private val a_ProduitModelRepository: A_ProduitModelRepository
) : ViewModel() {
    val a_ProduitModel = a_ProduitModelRepository.modelDatas
    var produitsAncienDataBaseMains: List<ProduitsAncienDataBaseMain> by mutableStateOf(emptyList())
    private val backupTriggerRef = Firebase.database.getReference("Z_BakupksModels/A_ProduitModel/TiggersBakups")
    private val backupBaseRef = Firebase.database.getReference("Z_BakupksModels/A_ProduitModel/Bakup")

    init {
        viewModelScope.launch {
            setupBackupTriggerListener()
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

    fun toggleBackupTrigger(triggerValue: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                backupTriggerRef.setValue(triggerValue)
            } catch (e: Exception) {
            }
        }
    }

    private fun setupBackupTriggerListener() {
        backupTriggerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val triggerValue = snapshot.getValue(Boolean::class.java) ?: false
                if (triggerValue) {
                    viewModelScope.launch {
                        val shouldPerformBackup = checkIfBackupNeededForToday()
                        if (shouldPerformBackup) {
                            performBackup()
                        }
                        backupTriggerRef.setValue(false)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private suspend fun checkIfBackupNeededForToday(): Boolean = withContext(Dispatchers.IO) {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            val snapshot = backupBaseRef.child(currentDate).get().await()
            return@withContext !snapshot.exists() || !snapshot.hasChildren()
        } catch (e: Exception) {
            return@withContext true
        }
    }

    private suspend fun performBackup() = withContext(Dispatchers.IO) {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            val backupRef = backupBaseRef.child(currentDate)
            val dataToBackup = a_ProduitModel.map { produit ->
                val produitMap = A_ProduitModel.syncData(produit, null)
                produit.id.toString() to produitMap
            }.toMap()
            backupRef.setValue(dataToBackup).await()
        } catch (e: Exception) {
        }
    }

    companion object {
        private const val TAG = "ViewModelA_ProduitModelButtons"
    }
}
