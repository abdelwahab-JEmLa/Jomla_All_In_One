package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.DataBasesInfosSql
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FireBaseHandler(private val database: AppDatabase? = null) {
    val TAG = "FireBaseHandler"
    val ref: DatabaseReference = _0_0_HeadOfRepositorys_Model
        .getHeadSqlDataBaseRef().child("C_InfosSqlDataBases")

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var needUpdateListener: ValueEventListener? = null


    fun addToFirebaseAsync(dataBasesInfosSql: DataBasesInfosSql, onSuccess: () -> Unit = {}) {
        val firebaseData = mapToFirebaseFormat(dataBasesInfosSql)
        val updates = mutableMapOf<String, Any>()

        // Use the refFireBase fields from DataBasesInfosSql model to ensure consistency
        if (firebaseData.containsKey("produits")) {
            updates[dataBasesInfosSql.refFireBaseA_ProduitInfos] = firebaseData["produits"] as Any
        }

        if (firebaseData.containsKey("clients")) {
            updates[dataBasesInfosSql.refFireBaseB_ClientInfos] = firebaseData["clients"] as Any
        }

        if (firebaseData.containsKey("typeTarifications")) {
            updates[dataBasesInfosSql.refFireBaseC_TypeTarificationInfos] = firebaseData["typeTarifications"] as Any
        }

        if (firebaseData.containsKey("tarifications")) {
            updates[dataBasesInfosSql.refFireBaseD_TarificationInfos] = firebaseData["tarifications"] as Any
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

    fun startNeedUpdateListener() {
        if (needUpdateListener != null || database == null) {
            return // Already listening or database not provided
        }

        Log.d(TAG, "Starting needUpdate listener for Firebase data")

        needUpdateListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    return
                }

                coroutineScope.launch {
                    try {
                        val needsUpdate = checkIfNeedsUpdate(snapshot)

                        if (needsUpdate) {
                            Log.d(TAG, "Detected data with needUpdate=true, syncing from Firebase to Room")
                            val firebaseData = mapFromFirebaseSnapshot(snapshot)
                            updateRoomFromFirebase(firebaseData)
                            resetNeedUpdateFlags(firebaseData)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in needUpdate listener: ${e.message}", e)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase needUpdate listener cancelled: ${error.message}")
            }
        }

        ref.addValueEventListener(needUpdateListener!!)
    }

    fun stopNeedUpdateListener() {
        needUpdateListener?.let {
            ref.removeEventListener(it)
            needUpdateListener = null
            Log.d(TAG, "Stopped needUpdate listener")
        }
    }

    private fun checkIfNeedsUpdate(snapshot: DataSnapshot): Boolean {
        // Create a default DataBasesInfosSql to get references
        val defaultModel = DataBasesInfosSql()

        // Check produits
        val produitsSnapshot = snapshot.child(defaultModel.refFireBaseA_ProduitInfos)
        if (produitsSnapshot.exists()) {
            for (productSnap in produitsSnapshot.children) {
                val needUpdateSnapshot = productSnap.child("needUpdate")
                if (needUpdateSnapshot.exists() && needUpdateSnapshot.getValue(Boolean::class.java) == true) {
                    return true
                }
            }
        }

        // Check clients
        val clientsSnapshot = snapshot.child(defaultModel.refFireBaseB_ClientInfos)
        if (clientsSnapshot.exists()) {
            for (clientSnap in clientsSnapshot.children) {
                val needUpdateSnapshot = clientSnap.child("needUpdate")
                if (needUpdateSnapshot.exists() && needUpdateSnapshot.getValue(Boolean::class.java) == true) {
                    return true
                }
            }
        }

        // Check typeTarifications
        val typeTarifsSnapshot = snapshot.child(defaultModel.refFireBaseC_TypeTarificationInfos)
        if (typeTarifsSnapshot.exists()) {
            for (typeSnap in typeTarifsSnapshot.children) {
                val needUpdateSnapshot = typeSnap.child("needUpdate")
                if (needUpdateSnapshot.exists() && needUpdateSnapshot.getValue(Boolean::class.java) == true) {
                    return true
                }
            }
        }

        // Check tarifications
        val tarifsSnapshot = snapshot.child(defaultModel.refFireBaseD_TarificationInfos)
        if (tarifsSnapshot.exists()) {
            for (tarifSnap in tarifsSnapshot.children) {
                val needUpdateSnapshot = tarifSnap.child("needUpdate")
                if (needUpdateSnapshot.exists() && needUpdateSnapshot.getValue(Boolean::class.java) == true) {
                    return true
                }
            }
        }

        return false
    }

    private suspend fun updateRoomFromFirebase(data: DataBasesInfosSql) {
        withContext(Dispatchers.IO) {
            try {
                // Using the AppDatabase instance to insert data
                database?.a_ProduitInfosDao()?.insertAll(data.a_ProduitInfos)
                database?.b_ClientInfosDao()?.insertAll(data.b_ClientInfos)
                database?.c_TypeTarificationInfosDao()?.insertAll(data.c_TypeTarificationInfos)
                database?.dTarificationInfosDao()?.insertAll(data.d_TarificationInfos)
                Log.d(TAG, "Room database updated with Firebase data successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating Room from Firebase: ${e.message}", e)
            }
        }
    }

    private fun resetNeedUpdateFlags(data: DataBasesInfosSql) {
        val updatedData = DataBasesInfosSql(
            a_ProduitInfos = data.a_ProduitInfos.map { it.copy(needUpdate = false) }.toMutableList(),
            b_ClientInfos = data.b_ClientInfos.map { it.copy(needUpdate = false) }.toMutableList(),
            c_TypeTarificationInfos = data.c_TypeTarificationInfos.map { it.copy(needUpdate = false) }.toMutableList(),
            d_TarificationInfos = data.d_TarificationInfos.map { it.copy(needUpdate = false) }.toMutableList()
        )

        addToFirebaseAsync(updatedData) {
            Log.d(TAG, "Successfully reset needUpdate flags in Firebase")
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
