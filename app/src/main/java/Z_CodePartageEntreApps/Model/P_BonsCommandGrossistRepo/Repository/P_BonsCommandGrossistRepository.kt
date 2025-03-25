package Z_CodePartageEntreApps.Model.P_BonsCommandGrossistRepo.Repository

import Z_CodePartageEntreApps.Model.P_BonsCommandGrossist
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface P_BonsCommandGrossistRepository {
    var modelDatas: SnapshotStateList<P_BonsCommandGrossist>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<P_BonsCommandGrossist>, Flow<Float>>
    fun stopDatabaseListener()
    fun checkConnectivityAndSync()
    suspend fun updateDatas(datas: SnapshotStateList<P_BonsCommandGrossist>)
    fun updateData(data: P_BonsCommandGrossist? = null,)

    companion object {
        val caReference = Firebase.database.getReference("P_BonsCommandGrossist")
    }
}



