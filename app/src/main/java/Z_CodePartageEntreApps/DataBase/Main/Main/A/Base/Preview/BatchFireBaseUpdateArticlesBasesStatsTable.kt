package Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Fixed: Better error handling and logging
fun batchFireBaseUpdateArticlesBasesStatsTable(datas: List<ArticlesBasesStatsTable>) {
    if (datas.isEmpty()) {
        Log.w("batchFireBaseUpdate", "No data to update")
        return
    }

    val detachedScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    detachedScope.launch {
        try {
            val updates = mutableMapOf<String, Any>()

            datas.forEach { data ->
                updates[data.keyID] = data.toFirebaseMap()
            }

            val firebaseRef = Firebase.database.getReference(
                "00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/AncienDataBase/A_ProduitInfos/07_13/Datas"
            )

            firebaseRef.updateChildren(updates).await()
            Log.d("batchFireBaseUpdate", "Successfully updated ${datas.size} items in Firebase")

        } catch (e: Exception) {
            Log.e("batchFireBaseUpdate", "Error updating Firebase: ${e.message}", e)
            // Consider showing user-friendly error message here
        }
    }
}
