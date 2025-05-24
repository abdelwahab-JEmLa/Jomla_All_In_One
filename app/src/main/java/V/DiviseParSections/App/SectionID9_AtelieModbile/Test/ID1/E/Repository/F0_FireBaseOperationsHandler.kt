package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.getKeyFireBase
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
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class F0_FireBaseOperationsHandler(
    private val onProgressUpdate: (Float) -> Unit = { }
) {
    val ref: DatabaseReference = _0_0_HeadOfRepositorys_Model
        .getHeadSqlDataBaseRef().child("C_InfosSqlDataBases")

    private val childD_TarificationInfos = ref.child("D_TarificationInfos")

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    var needUpdateListener: ValueEventListener? = null

    fun getDataFromFirebase(
        onAddSuccess: (
            List<D_TarificationInfos>,
            List<A_ProduitInfos>,
        ) -> Unit
    ) {
        onProgressUpdate(0.1f)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        onProgressUpdate(0.3f)

                        // Process D_TarificationInfos
                        val tarificationsSnapshot = snapshot.child("D_TarificationInfos")
                        val mappedTarifications = mapSnapshotToObjects(
                            tarificationsSnapshot,
                            D_TarificationInfos::class
                        )

                        onProgressUpdate(0.6f)

                        // Process A_ProduitInfos
                        val productsSnapshot = snapshot.child("A_ProduitInfos")
                        val mappedProducts = mapSnapshotToObjects(
                            productsSnapshot,
                            A_ProduitInfos::class
                        )

                        onProgressUpdate(0.9f)

                        // Debug logging
                        println("Firebase Data Retrieved:")
                        println("Tarifications count: ${mappedTarifications.size}")
                        println("Products count: ${mappedProducts.size}")

                        onAddSuccess(mappedTarifications, mappedProducts)
                        onProgressUpdate(1f)

                    } catch (e: Exception) {
                        println("Error in getDataFromFirebase: ${e.message}")
                        e.printStackTrace()
                        onProgressUpdate(0f)
                        onAddSuccess(emptyList(), emptyList())
                    }
                } else {
                    println("Firebase snapshot does not exist")
                    onProgressUpdate(1f)
                    onAddSuccess(emptyList(), emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase read cancelled: ${error.message}")
                onProgressUpdate(0f)
                onAddSuccess(emptyList(), emptyList())
            }
        })
    }

    inline fun <reified T : Any> mapSnapshotToObjects(
        snapshot: DataSnapshot,
        kClass: KClass<T>
    ): List<T> {
        val results = mutableListOf<T>()

        getDatas<T>(snapshot, kClass, results)

        return results
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

                    val key = tariff.keyFireBase.ifEmpty {
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
