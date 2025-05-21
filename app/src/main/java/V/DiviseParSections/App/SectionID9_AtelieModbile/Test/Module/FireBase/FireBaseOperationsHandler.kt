package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBase

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.SQl.RoomOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.getKeyFireBase
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
import kotlin.reflect.full.memberProperties

class FireBaseOperationsHandler(
    val roomOperationsHandler: RoomOperationsHandler
) {
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

    suspend fun upsertAllAndReturnListIdToData(
        mapData: Map<Long, D_TarificationInfos>,
        onAddSuccess: (Map<String, D_TarificationInfos>) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            // Create a batch update for Firebase
            val tarifsMap = mutableMapOf<String, Any>()
            val resultMap = mutableMapOf<String, D_TarificationInfos>()

            // Process each tarification object and prepare it for Firebase
            mapData.values.forEach { tarif ->
                try {
                    val tarifMap = mutableMapOf<String, Any>()
                    tarif::class.memberProperties.forEach { prop ->
                        val value = prop.getter.call(tarif)
                        if (value != null) {
                            // Handle enums by using their name
                            if (value::class.java.isEnum) {
                                tarifMap[prop.name] = value.toString()
                            } else {
                                tarifMap[prop.name] = value
                            }
                        } else {
                            tarifMap[prop.name] = "null"
                        }
                    }

                    // Generate or use existing Firebase key
                    val key = tarif.keyFireBase.takeIf { it.isNotEmpty() }
                        ?: getKeyFireBase(tarif.id, tarif.nom)

                    tarifsMap[key] = tarifMap
                    resultMap[key] = tarif
                } catch (e: Exception) {
                    Log.e("FireBaseOperationsHandler", "Error processing tarif: ${e.message}")
                }
            }

            // Perform the batch update to Firebase
            if (tarifsMap.isNotEmpty()) {
                ref.child("D_TarificationInfos").updateChildren(tarifsMap).await()
                Log.d("FireBaseOperationsHandler", "Successfully uploaded ${tarifsMap.size} tarifications to Firebase")
            }

            // Call the success callback with the result map
            onAddSuccess(resultMap)

        } catch (e: Exception) {
            Log.e("FireBaseOperationsHandler", "Error in upsertAllAndReturnListIdToData: ${e.message}")
            onAddSuccess(emptyMap()) // Return empty map on error
        }
    }

    suspend fun isDatabaseEmptyAsync(): Boolean = suspendCancellableCoroutine { continuation ->
        isDatabaseEmpty { isEmpty ->
            continuation.resume(isEmpty)
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

    fun addSingleTariffToFirebase(tariff: D_TarificationInfos) {
        try {
            val tarifMap = mutableMapOf<String, Any>()

            tariff::class.memberProperties.forEach { prop ->
                val value = prop.getter.call(tariff)
                if (value != null) {
                    if (value::class.java.isEnum) {
                        tarifMap[prop.name] = value.toString()
                    } else {
                        tarifMap[prop.name] = value
                    }
                } else {
                    tarifMap[prop.name] = "null"
                }
            }

            val key = tariff.keyFireBase.takeIf { it.isNotEmpty() }
                ?: getKeyFireBase(tariff.id, tariff.nom)

            ref.child("D_TarificationInfos").child(key).setValue(tarifMap)
            Log.d("FireBaseOperationsHandler", "Successfully added single tariff to Firebase with key: $key")
        } catch (e: Exception) {
            Log.e("FireBaseOperationsHandler", "Error adding single tariff to Firebase: ${e.message}")
        }
    }
}
