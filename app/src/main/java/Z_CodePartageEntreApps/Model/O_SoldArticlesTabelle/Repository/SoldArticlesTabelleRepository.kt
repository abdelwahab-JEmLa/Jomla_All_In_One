package Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.Repository

import Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.SoldArticlesTabelle
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface SoldArticlesTabelleRepository {
    var modelDatas: SnapshotStateList<SoldArticlesTabelle>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<SoldArticlesTabelle>, Flow<Float>>
    fun stopDatabaseListener()
    fun checkConnectivityAndSync()
    suspend fun updateDatas(datas: SnapshotStateList<SoldArticlesTabelle>)
    fun updateData(data: SoldArticlesTabelle? = null,)

    companion object {
        val caReference = Firebase.database.getReference("O_SoldArticlesTabelle")
    }
}



