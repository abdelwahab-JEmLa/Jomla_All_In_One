package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBase.mapFromFirebaseSnapshot
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.getKeyFireBase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.full.memberProperties

class FireBaseOperationsHandler(
    val roomOperationsHandler: RoomOperationsHandler
) {
    val ref: DatabaseReference = _0_0_HeadOfRepositorys_Model
        .getHeadSqlDataBaseRef().child("C_InfosSqlDataBases")

    private val childD_TarificationInfos = ref.child("D_TarificationInfos")

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    var needUpdateListener: ValueEventListener? = null


    suspend fun isDatabaseEmpty(onDataEstEmpty: () -> Unit) =
        suspendCancellableCoroutine { continuation ->
            Log.d("FireBaseOperationsHandler", "Checking if Firebase database is empty")
            isDatabaseEmpty { isEmpty ->
                continuation.resume(isEmpty)
                onDataEstEmpty()
            }
        }

    private fun isDatabaseEmpty(onResult: (Boolean) -> Unit) {
        childD_TarificationInfos.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isEmpty = !snapshot.exists() || !snapshot.hasChildren()
                onResult(isEmpty)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(false)
            }
        })
    }

    suspend fun getDataFromFirebase(onAddSuccess: (List<D_TarificationInfos>) -> Unit): DataBasesInfosSql? {
        return suspendCancellableCoroutine { continuation ->
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        try {
                            val infosSqlDataBases = mapFromFirebaseSnapshot(snapshot)
                            continuation.resume(infosSqlDataBases)
                            onAddSuccess(infosSqlDataBases.d_TarificationInfos)
                        } catch (e: Exception) {
                            continuation.resumeWithException(e)
                        }
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(Exception("Firebase data retrieval cancelled: ${error.message}"))
                }
            })
        }
    }

    suspend fun upsertAllAndReturnListIdToData(
        mapData: Map<Long, D_TarificationInfos>,
        onAddSuccess: (Map<String, D_TarificationInfos>) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            val tariffsMap = mutableMapOf<String, Any>()
            val resultMap = mutableMapOf<String, D_TarificationInfos>()

            mapData.values.forEach { tariff ->
                try {
                    val tariffMap = mutableMapOf<String, Any>()
                    tariff::class.memberProperties.forEach { prop ->
                        val value = prop.getter.call(tariff)
                        if (value != null) {
                            if (value::class.java.isEnum) {
                                tariffMap[prop.name] = value.toString()
                            } else {
                                tariffMap[prop.name] = value
                            }
                        } else {
                            tariffMap[prop.name] = "null"
                        }
                    }

                    val key = tariff.keyFireBase.takeIf { it.isNotEmpty() }
                        ?: getKeyFireBase(tariff.id, tariff.nom)

                    tariffsMap[key] = tariffMap
                    resultMap[key] = tariff
                } catch (e: Exception) {
                    Log.e("FireBaseOperationsHandler", "Error processing tariff: ${e.message}")
                }
            }

            // Perform the batch update to Firebase
            if (tariffsMap.isNotEmpty()) {
                childD_TarificationInfos.updateChildren(tariffsMap).await()
                Log.d(
                    "FireBaseOperationsHandler",
                    "Successfully uploaded ${tariffsMap.size} tarifications to Firebase"
                )
            }

            // Call the success callback with the result map
            onAddSuccess(resultMap)

        } catch (e: Exception) {
            Log.e(
                "FireBaseOperationsHandler",
                "Error in upsertAllAndReturnListIdToData: ${e.message}"
            )
            onAddSuccess(emptyMap()) // Return empty map on error
        }
    }

    fun deleteTarificationInfosNode(onComplete: (Boolean) -> Unit = {}) {
        ref.child("D_TarificationInfos").removeValue()
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }


    fun startNeedUpdateListener() {
        Log.d("FireBaseOperationsHandler", "Starting Firebase update listener")
        needUpdateListener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FireBaseOperationsHandler", "Firebase data changed, syncing with Room")
                //  coroutineScope.startNeedUpdateListener(roomOperationsHandler, ref)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FireBaseOperationsHandler", "Firebase listener cancelled: ${error.message}")
            }
        })
    }

    fun stopNeedUpdateListener() {
        Log.d("FireBaseOperationsHandler", "Stopping Firebase update listener")
        needUpdateListener?.let {
            ref.removeEventListener(it)
            needUpdateListener = null
        }
    }
}
