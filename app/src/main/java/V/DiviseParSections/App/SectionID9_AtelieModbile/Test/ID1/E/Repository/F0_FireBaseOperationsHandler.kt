package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.reflect.KClass

class F0_FireBaseOperationsHandler(
    val onProgressUpdate: (Float) -> Unit = { }
) {
    val ref: DatabaseReference = _0_0_HeadOfRepositorys_Model
        .getHeadSqlDataBaseRef().child("C_InfosSqlDataBases")

    val childD_TarificationInfos = ref.child("D_TarificationInfos")
    val childA_ProduitInfos = ref.child("A_ProduitInfos")

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    var needUpdateListener: ValueEventListener? = null

    init {
        verifyFirebaseConnectivity()
    }

    private fun verifyFirebaseConnectivity() {
        coroutineScope.launch {
            try {
                val isConnected = F6_FirebaseDebugUtils.verifyFirebaseReference(ref)
                F6_FirebaseDebugUtils.logFirebaseOperation(
                    "verifyFirebaseConnectivity",
                    ref,
                    0,
                    isConnected
                )
            } catch (e: Exception) {
                F6_FirebaseDebugUtils.logFirebaseOperation(
                    "verifyFirebaseConnectivity",
                    ref,
                    0,
                    false,
                    e
                )
                e.printStackTrace()
            }
        }
    }

    fun getDataFromFirebase(
        onAddSuccess: (
            List<D_TarificationInfos>,
            List<A_ProduitInfos>,
        ) -> Unit
    ) {
        F6_FirebaseDebugUtils.logFirebaseOperation("getDataFromFirebase_START", ref)
        onProgressUpdate(0.1f)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    F6_FirebaseDebugUtils.logFirebaseOperation(
                        "getDataFromFirebase_SNAPSHOT_EXISTS",
                        ref,
                        0,
                        snapshot.exists()
                    )

                    if (snapshot.exists()) {
                        onProgressUpdate(0.3f)

                        // Debug: Log all available children
                        println("=== Available Firebase Children ===")
                        snapshot.children.forEach { child ->
                            println("Child key: ${child.key}, exists: ${child.exists()}, childrenCount: ${child.childrenCount}")
                        }
                        println("===================================")

                        val tarificationsSnapshot = snapshot.child("D_TarificationInfos")
                        F6_FirebaseDebugUtils.logFirebaseOperation(
                            "TARIFICATION_SNAPSHOT",
                            childD_TarificationInfos,
                            tarificationsSnapshot.childrenCount.toInt(),
                            tarificationsSnapshot.exists()
                        )

                        val mappedTarifications = if (tarificationsSnapshot.exists()) {
                            mapSnapshotToObjects(tarificationsSnapshot, D_TarificationInfos::class)
                        } else {
                            emptyList()
                        }

                        onProgressUpdate(0.6f)

                        val productsSnapshot = snapshot.child("A_ProduitInfos")
                        F6_FirebaseDebugUtils.logFirebaseOperation(
                            "PRODUCTS_SNAPSHOT",
                            childA_ProduitInfos,
                            productsSnapshot.childrenCount.toInt(),
                            productsSnapshot.exists()
                        )

                        // Debug: Log sample product data
                        if (productsSnapshot.exists()) {
                            println("=== Sample Product Data ===")
                            productsSnapshot.children.take(3).forEach { child ->
                                println("Product key: ${child.key}")
                                println("Product data sample:")
                                child.children.take(5).forEach { field ->
                                    println("  ${field.key}: ${field.value}")
                                }
                                println("---")
                            }
                            println("==========================")
                        } else {
                            println("=== No Products Found ===")
                            println("Products snapshot path: ${productsSnapshot.ref}")
                            println("Products snapshot exists: ${productsSnapshot.exists()}")
                            println("========================")
                        }

                        val mappedProducts = if (productsSnapshot.exists()) {
                            mapSnapshotToObjects(productsSnapshot, A_ProduitInfos::class)
                        } else {
                            emptyList()
                        }

                        onProgressUpdate(0.9f)

                        F6_FirebaseDebugUtils.logFirebaseOperation(
                            "getDataFromFirebase_SUCCESS",
                            ref,
                            mappedTarifications.size + mappedProducts.size,
                            true
                        )

                        println("=== Final Results ===")
                        println("Tarifications count: ${mappedTarifications.size}")
                        println("Products count: ${mappedProducts.size}")
                        println("====================")

                        onAddSuccess(mappedTarifications, mappedProducts)
                        onProgressUpdate(1f)

                    } else {
                        F6_FirebaseDebugUtils.logFirebaseOperation("getDataFromFirebase_EMPTY", ref, 0, true)
                        onProgressUpdate(1f)
                        onAddSuccess(emptyList(), emptyList())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    F6_FirebaseDebugUtils.logFirebaseOperation("getDataFromFirebase_ERROR", ref, 0, false, e)
                    onProgressUpdate(0f)
                    onAddSuccess(emptyList(), emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                F6_FirebaseDebugUtils.logFirebaseOperation("getDataFromFirebase_CANCELLED", ref, 0, false, Exception(error.message))
                onProgressUpdate(0f)
                onAddSuccess(emptyList(), emptyList())
            }
        })
    }

    inline fun <reified T : Any> mapSnapshotToObjects(
        snapshot: DataSnapshot,
        kClass: KClass<T>
    ): List<T> {
        return try {
            val results = mutableListOf<T>()
            F6_FirebaseDebugUtils.logFirebaseOperation(
                "mapSnapshotToObjects_START",
                snapshot.ref,
                snapshot.childrenCount.toInt(),
                snapshot.exists()
            )

            getDatas<T>(snapshot, kClass, results)

            F6_FirebaseDebugUtils.logFirebaseOperation(
                "mapSnapshotToObjects_SUCCESS",
                snapshot.ref,
                results.size,
                true
            )

            results
        } catch (e: Exception) {
            e.printStackTrace()
            F6_FirebaseDebugUtils.logFirebaseOperation(
                "mapSnapshotToObjects_ERROR",
                snapshot.ref,
                0,
                false,
                e
            )
            emptyList()
        }
    }

    suspend fun getAncienDB_changeKeysFireBase(): Pair<Int, Map<String, A_ProduitInfos>> = withContext(Dispatchers.IO) {
        return@withContext suspendCancellableCoroutine { continuation ->
            coroutineScope.launch {
                try {
                    onProgressUpdate(0.1f)
                    F6_FirebaseDebugUtils.logFirebaseOperation("getAncienDB_START", ref)

                    val firebaseDatabase = FirebaseDatabase.getInstance()
                    val refDBJetPackExport = firebaseDatabase.getReference("e_DBJetPackExport")

                    val (originalCount, resultMap) = pairgetAncienDB_changeKeysFireBase(refDBJetPackExport)

                    continuation.resume(Pair(originalCount, resultMap))

                } catch (e: Exception) {
                    e.printStackTrace()
                    F6_FirebaseDebugUtils.logFirebaseOperation("getAncienDB_ERROR", ref, 0, false, e)
                    onProgressUpdate(0f)
                    continuation.resume(Pair(0, emptyMap()))
                }
            }
        }
    }



    fun convertArticlesBasesToProduitInfos(anciennesListe: List<ArticlesBasesStatsTable>): List<A_ProduitInfos> {
        return anciennesListe.map { ancien ->
            aProduitinfos(ancien)
        }
    }

    suspend inline fun <reified DataBase : Any> setDataInlineFun(
        datas: List<DataBase> = emptyList()
    ): Map<String, DataBase> = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.1f)
            F6_FirebaseDebugUtils.logFirebaseOperation("setDataInlineFun_START", ref, datas.size, true)

            if (datas.isEmpty()) {
                onProgressUpdate(1f)
                return@withContext emptyMap()
            }

            onProgressUpdate(0.3f)
            val dataMap = mutableMapOf<String, Any>()
            val resultMap = mutableMapOf<String, DataBase>()
            val processedCount = 0
            val totalCount = datas.size

            extractedsetDataInlineFunFixed<DataBase>(datas, processedCount, dataMap, resultMap, totalCount)

            F6_FirebaseDebugUtils.logFirebaseOperation(
                "setDataInlineFun_SUCCESS",
                ref,
                resultMap.size,
                true
            )

            return@withContext resultMap

        } catch (e: Exception) {
            e.printStackTrace()
            F6_FirebaseDebugUtils.logFirebaseOperation("setDataInlineFun_ERROR", ref, 0, false, e)
            onProgressUpdate(0f)
            return@withContext emptyMap()
        }
    }

    suspend fun upsertAllAndReturnListIdToData(
        mapData: Map<Long, D_TarificationInfos>,
        onAddSuccess: (Map<String, D_TarificationInfos>) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.1f)
            F6_FirebaseDebugUtils.logFirebaseOperation("upsertAllAndReturnListIdToData_START", childD_TarificationInfos, mapData.size, true)

            if (mapData.isEmpty()) {
                onProgressUpdate(1f)
                onAddSuccess(emptyMap())
                return@withContext
            }

            onProgressUpdate(0.3f)
            val tariffsMap = mutableMapOf<String, Any>()
            val resultMap = mutableMapOf<String, D_TarificationInfos>()

            var processedCount1 = 0
            val totalCount = mapData.size

            extractedFromeupsertAllAndReturnListIdToData(mapData, tariffsMap, resultMap, processedCount1, totalCount)
            onAddSuccess(resultMap)

        } catch (e: Exception) {
            e.printStackTrace()
            F6_FirebaseDebugUtils.logFirebaseOperation("upsertAllAndReturnListIdToData_ERROR", childD_TarificationInfos, 0, false, e)
            onProgressUpdate(0f)
            onAddSuccess(emptyMap())
        }
    }



}
