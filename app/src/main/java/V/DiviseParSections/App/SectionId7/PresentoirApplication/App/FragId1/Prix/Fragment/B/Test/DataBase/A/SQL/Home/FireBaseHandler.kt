package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.DataBasesInfosSql
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FireBaseHandler {
    val TAG = "FireBaseHandler"
    val ref: DatabaseReference = _0_0_HeadOfRepositorys_Model.getHeadSqlDataBaseRef().child("C_InfosSqlDataBases")

    fun getRefPath(): String = ref.toString()

    fun addToFirebaseAsync(dataBasesInfosSql: DataBasesInfosSql, onSuccess: () -> Unit={}) {
        val firebaseData = mapToFirebaseFormat(dataBasesInfosSql)
        val updates = mutableMapOf<String, Any>()

        if (firebaseData.containsKey("produits")) {
            updates["produits"] = firebaseData["produits"] as Any
        }

        if (firebaseData.containsKey("clients")) {
            updates["clients"] = firebaseData["clients"] as Any
        }

        if (firebaseData.containsKey("typeTarifications")) {
            updates["typeTarifications"] = firebaseData["typeTarifications"] as Any
        }

        if (firebaseData.containsKey("tarifications")) {
            updates["tarifications"] = firebaseData["tarifications"] as Any
        }

        if (updates.isEmpty()) {
            onSuccess()
            return
        }

        ref.updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                ref.root.child(".info/connected").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {}
                    override fun onCancelled(error: DatabaseError) {}
                })
                onSuccess()
            }
    }


    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result -> continuation.resume(result) }
        addOnFailureListener { exception -> continuation.resumeWithException(exception) }
        continuation.invokeOnCancellation {
            if (isComplete.not()) {
                cancel()
            }
        }
    }
}
