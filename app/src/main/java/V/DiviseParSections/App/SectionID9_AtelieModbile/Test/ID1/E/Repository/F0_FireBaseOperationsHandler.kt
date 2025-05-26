package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.getKeyFireBase
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

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
            } catch (e: Exception) {
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
        F6_FirebaseDebugUtils.logFirebaseOperation("getDataFromFirebase", ref)
        onProgressUpdate(0.1f)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (snapshot.exists()) {
                        onProgressUpdate(0.3f)

                        val tarificationsSnapshot = snapshot.child("D_TarificationInfos")
                        val mappedTarifications = if (tarificationsSnapshot.exists()) {
                            mapSnapshotToObjects(tarificationsSnapshot, D_TarificationInfos::class)
                        } else {
                            emptyList()
                        }

                        onProgressUpdate(0.6f)

                        val productsSnapshot = snapshot.child("A_ProduitInfos")
                        val mappedProducts = if (productsSnapshot.exists()) {
                            mapSnapshotToObjects(productsSnapshot, A_ProduitInfos::class)
                        } else {
                            emptyList()
                        }

                        onProgressUpdate(0.9f)

                        F6_FirebaseDebugUtils.logFirebaseOperation("getDataFromFirebase", ref, mappedTarifications.size + mappedProducts.size, true)

                        onAddSuccess(mappedTarifications, mappedProducts)
                        onProgressUpdate(1f)

                    } else {
                        F6_FirebaseDebugUtils.logFirebaseOperation("getDataFromFirebase", ref, 0, true)
                        onProgressUpdate(1f)
                        onAddSuccess(emptyList(), emptyList())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    F6_FirebaseDebugUtils.logFirebaseOperation("getDataFromFirebase", ref, 0, false, e)
                    onProgressUpdate(0f)
                    onAddSuccess(emptyList(), emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                F6_FirebaseDebugUtils.logFirebaseOperation("getDataFromFirebase", ref, 0, false, Exception(error.message))
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
            getDatas<T>(snapshot, kClass, results)
            results
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getAncienDB_changeKeysFireBase(): Pair<Int, Map<String, A_ProduitInfos>> = withContext(Dispatchers.IO) {
        return@withContext suspendCancellableCoroutine { continuation ->
            coroutineScope.launch {
                try {
                    onProgressUpdate(0.1f)
                    val firebaseDatabase = FirebaseDatabase.getInstance()
                    val refDBJetPackExport = firebaseDatabase.getReference("e_DBJetPackExport")

                    onProgressUpdate(0.3f)
                    val articlesSnapshot = refDBJetPackExport.get().await()
                    val articles = articlesSnapshot.children.mapNotNull { snapshot ->
                        snapshot.getValue(ArticlesBasesStatsTable::class.java)
                    }

                    onProgressUpdate(0.5f)
                    val productsWithoutKeys = articles.map { product ->
                        product.copy(keyFireBase = "")
                    }

                    val a_ProduitInfosList = convertArticlesBasesToProduitInfos(productsWithoutKeys)
                    val originalCount = a_ProduitInfosList.size

                    onProgressUpdate(0.7f)
                    val resultMap = setDataInlineFun<A_ProduitInfos>(a_ProduitInfosList)

                    onProgressUpdate(1f)

                    continuation.resume(Pair(originalCount, resultMap))

                } catch (e: Exception) {
                    onProgressUpdate(0f)
                    continuation.resume(Pair(0, emptyMap()))
                }
            }
        }
    }

    private fun convertArticlesBasesToProduitInfos(anciennesListe: List<ArticlesBasesStatsTable>): List<A_ProduitInfos> {
        return anciennesListe.map { ancien ->
            aProduitinfos(ancien)
        }
    }

    private suspend inline fun <reified DataBase : Any> setDataInlineFun(
        datas: List<DataBase> = emptyList()
    ): Map<String, DataBase> = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.1f)

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

            return@withContext resultMap

        } catch (e: Exception) {
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

            if (mapData.isEmpty()) {
                onProgressUpdate(1f)
                onAddSuccess(emptyMap())
                return@withContext
            }

            onProgressUpdate(0.3f)
            val tariffsMap = mutableMapOf<String, Any>()
            val resultMap = mutableMapOf<String, D_TarificationInfos>()

            val processedCount = 0
            val totalCount = mapData.size

            var processedCount1 = processedCount
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

                    val key = getKeyFireBase(tariff.id, tariff.nom)

                    tariffsMap[key] = tariffMap
                    resultMap[key] = tariff

                    processedCount1++
                    val progress = 0.3f + (processedCount1.toFloat() / totalCount) * 0.4f
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
    // Add this function to F0_FireBaseOperationsHandler class

    suspend inline fun <reified DataBase : Any> deleteRef(): Boolean = withContext(Dispatchers.IO) {
        return@withContext suspendCancellableCoroutine { continuation ->
            try {
                onProgressUpdate(0.1f)

                val childRef = when (DataBase::class) {
                    D_TarificationInfos::class -> childD_TarificationInfos
                    A_ProduitInfos::class -> childA_ProduitInfos
                    else -> {
                        onProgressUpdate(0f)
                        continuation.resume(false)
                        return@suspendCancellableCoroutine
                    }
                }

                onProgressUpdate(0.5f)

                childRef.removeValue()
                    .addOnSuccessListener {
                        onProgressUpdate(1f)
                        F6_FirebaseDebugUtils.logFirebaseOperation(
                            "deleteRef",
                            childRef,
                            0,
                            true
                        )
                        continuation.resume(true)
                    }
                    .addOnFailureListener { exception ->
                        onProgressUpdate(0f)
                        F6_FirebaseDebugUtils.logFirebaseOperation(
                            "deleteRef",
                            childRef,
                            0,
                            false,
                            exception
                        )
                        continuation.resume(false)
                    }

            } catch (e: Exception) {
                onProgressUpdate(0f)
                e.printStackTrace()
                continuation.resume(false)
            }
        }
    }
    fun verifyDatabaseStructure(onResult: (String) -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val structure = StringBuilder()
                structure.append("Database structure at ${ref.toString()}:\n")

                if (snapshot.exists()) {
                    structure.append("Root exists: true\n")
                    snapshot.children.forEach { child ->
                        structure.append("- ${child.key}: ${child.childrenCount} items\n")
                    }
                } else {
                    structure.append("Root exists: false\n")
                }

                onResult(structure.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                onResult("Error verifying structure: ${error.message}")
            }
        })
    }

}
