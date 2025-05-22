package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBase.mapFromFirebaseSnapshot
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.getKeyFireBase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.full.memberProperties

class FireBaseOperationsHandler(
    val roomOperationsHandler: RoomOperationsHandler,
    private val onProgressUpdate: (Float) -> Unit = { }
) {
    val ref: DatabaseReference = _0_0_HeadOfRepositorys_Model
        .getHeadSqlDataBaseRef().child("C_InfosSqlDataBases")

    private val childD_TarificationInfos = ref.child("D_TarificationInfos")

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    var needUpdateListener: ValueEventListener? = null

    suspend fun isDatabaseEmpty(onDataEstEmpty: () -> Unit) =
        suspendCancellableCoroutine { continuation ->
            onProgressUpdate(0.1f)
            isDatabaseEmpty { isEmpty ->
                onProgressUpdate(0.8f)
                continuation.resume(isEmpty)
                onDataEstEmpty()
            }
        }

    private fun isDatabaseEmpty(onResult: (Boolean) -> Unit) {
        childD_TarificationInfos.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isEmpty = !snapshot.exists() || !snapshot.hasChildren()
                onProgressUpdate(1f)
                onResult(isEmpty)
            }

            override fun onCancelled(error: DatabaseError) {
                onProgressUpdate(0f)
                onResult(false)
            }
        })
    }

    suspend fun getDataFromFirebase(onAddSuccess: (List<D_TarificationInfos>) -> Unit): DataBasesInfosSql? {
        return suspendCancellableCoroutine { continuation ->
            onProgressUpdate(0.1f)

            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        try {
                            onProgressUpdate(0.5f)
                            val infosSqlDataBases = mapFromFirebaseSnapshot(snapshot)

                            onProgressUpdate(0.9f)
                            continuation.resume(infosSqlDataBases)
                            onAddSuccess(infosSqlDataBases.d_TarificationInfos)

                            onProgressUpdate(1f)
                        } catch (e: Exception) {
                            onProgressUpdate(0f)
                            continuation.resumeWithException(e)
                        }
                    } else {
                        onProgressUpdate(1f)
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onProgressUpdate(0f)
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
            onProgressUpdate(0.1f)

            if (mapData.isEmpty()) {
                onProgressUpdate(1f)
                onAddSuccess(emptyMap())
                return@withContext
            }

            onProgressUpdate(0.3f)
            val tariffsMap = mutableMapOf<String, Any>()
            val resultMap = mutableMapOf<String, D_TarificationInfos>()

            var processedCount = 0
            val totalCount = mapData.size

            mapData.values.forEach { tariff ->
                try {
                    val tariffMap = mutableMapOf<String, Any>()

                    tariff::class.memberProperties.forEach { prop ->
                        try {
                            val value = prop.getter.call(tariff)
                            when {
                                value == null -> tariffMap[prop.name] = "null"
                                value::class.java.isEnum -> tariffMap[prop.name] = value.toString()
                                else -> tariffMap[prop.name] = value
                            }
                        } catch (e: Exception) {
                            tariffMap[prop.name] = "null"
                        }
                    }

                    val key = if (tariff.keyFireBase.isNotEmpty()) {
                        tariff.keyFireBase
                    } else {
                        getKeyFireBase(tariff.id, tariff.nom)
                    }

                    tariffsMap[key] = tariffMap
                    resultMap[key] = tariff

                    processedCount++
                    val progress = 0.3f + (processedCount.toFloat() / totalCount) * 0.4f
                    onProgressUpdate(progress)

                } catch (e: Exception) {
                    onProgressUpdate(0.5f)
                }
            }

            if (tariffsMap.isNotEmpty()) {
                try {
                    onProgressUpdate(0.8f)

                    suspendCancellableCoroutine<Unit> { continuation ->
                        childD_TarificationInfos.updateChildren(tariffsMap)
                            .addOnSuccessListener {
                                continuation.resume(Unit)
                            }
                            .addOnFailureListener { exception ->
                                continuation.resumeWithException(exception)
                            }
                    }

                    onProgressUpdate(1f)

                } catch (firebaseException: Exception) {
                    onProgressUpdate(0.8f)
                }
            } else {
                onProgressUpdate(1f)
            }

            onAddSuccess(resultMap)

        } catch (e: Exception) {
            onProgressUpdate(0f)
            onAddSuccess(emptyMap())
        }
    }

    fun deleteTarificationInfosNode(onComplete: (Boolean) -> Unit = {}) {
        onProgressUpdate(0.3f)

        ref.child("D_TarificationInfos").removeValue()
            .addOnSuccessListener {
                onProgressUpdate(1f)
                onComplete(true)
            }
            .addOnFailureListener { exception ->
                onProgressUpdate(0f)
                onComplete(false)
            }
    }

    fun startNeedUpdateListener() {
        onProgressUpdate(0.5f)

        needUpdateListener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onProgressUpdate(1f)
            }

            override fun onCancelled(error: DatabaseError) {
                onProgressUpdate(0f)
            }
        })
    }

    fun stopNeedUpdateListener() {
        needUpdateListener?.let {
            ref.removeEventListener(it)
            needUpdateListener = null
            onProgressUpdate(1f)
        }
    }
}
