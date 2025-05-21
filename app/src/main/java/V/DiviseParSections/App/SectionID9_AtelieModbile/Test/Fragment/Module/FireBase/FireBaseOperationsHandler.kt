package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Fragment.Module.FireBase

import V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.Module.SQl.RoomOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.DataBasesInfosSql
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FireBaseOperationsHandler(
     val roomOperationsHandler: RoomOperationsHandler
) {
    companion object {
        const val TAG = "FireBaseOperationsHandler"
    }

    val ref: DatabaseReference = _0_0_HeadOfRepositorys_Model
        .getHeadSqlDataBaseRef().child("C_InfosSqlDataBases")

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    var needUpdateListener: ValueEventListener? = null

    private fun isDatabaseEmpty(onResult: (Boolean) -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isEmpty = !snapshot.exists() || !snapshot.hasChildren()
                onResult(isEmpty)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(false)
            }
        })
    }

    suspend fun isDatabaseEmptyAsync(): Boolean = suspendCancellableCoroutine { continuation ->
        isDatabaseEmpty { isEmpty ->
            continuation.resume(isEmpty)
        }
    }

    fun addToFirebaseAsync(dataBasesInfosSql: DataBasesInfosSql, onSuccess: () -> Unit = {}) {
        val firebaseData = mapToFirebaseFormat(dataBasesInfosSql)
        val updates = mutableMapOf<String, Any>()

        if (firebaseData.containsKey(dataBasesInfosSql.refFireBaseA_ProduitInfos)) {
            updates[dataBasesInfosSql.refFireBaseA_ProduitInfos] = firebaseData[dataBasesInfosSql.refFireBaseA_ProduitInfos] as Any
        }

        if (firebaseData.containsKey(dataBasesInfosSql.refFireBaseB_ClientInfos)) {
            updates[dataBasesInfosSql.refFireBaseB_ClientInfos] = firebaseData[dataBasesInfosSql.refFireBaseB_ClientInfos] as Any
        }

        if (firebaseData.containsKey(dataBasesInfosSql.refFireBaseC_TypeTarificationInfos)) {
            updates[dataBasesInfosSql.refFireBaseC_TypeTarificationInfos] = firebaseData[dataBasesInfosSql.refFireBaseC_TypeTarificationInfos] as Any
        }

        if (firebaseData.containsKey(dataBasesInfosSql.refFireBaseD_TarificationInfos)) {
            updates[dataBasesInfosSql.refFireBaseD_TarificationInfos] = firebaseData[dataBasesInfosSql.refFireBaseD_TarificationInfos] as Any
        }

        if (updates.isEmpty()) {
            onSuccess()
            return
        }

        ref.updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { error ->
                ref.root.child(".info/connected").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {}
                    override fun onCancelled(error: DatabaseError) {}
                })
                onSuccess()
            }
    }
}
