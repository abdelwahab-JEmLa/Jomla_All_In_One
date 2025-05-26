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
import kotlinx.coroutines.tasks.await
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

                        val tarificationsSnapshot = snapshot.child("D_TarificationInfos")
                        val mappedTarifications = mapSnapshotToObjects(
                            tarificationsSnapshot,
                            D_TarificationInfos::class
                        )

                        onProgressUpdate(0.6f)

                        val productsSnapshot = snapshot.child("A_ProduitInfos")
                        val mappedProducts = mapSnapshotToObjects(
                            productsSnapshot,
                            A_ProduitInfos::class
                        )

                        onProgressUpdate(0.9f)

                        onAddSuccess(mappedTarifications, mappedProducts)
                        onProgressUpdate(1f)

                    } catch (e: Exception) {
                        onProgressUpdate(0f)
                        onAddSuccess(emptyList(), emptyList())
                    }
                } else {
                    onProgressUpdate(1f)
                    onAddSuccess(emptyList(), emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
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
            var processedCount = 0
            val totalCount = datas.size

            extractedsetDataInlineFun<DataBase>(datas, processedCount, dataMap, resultMap, totalCount)

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

            var processedCount = 0
            val totalCount = mapData.size

            extractedupsertAllAndReturnListIdToData(mapData, tariffsMap, resultMap, processedCount, totalCount)

            onAddSuccess(resultMap)

        } catch (e: Exception) {
            onProgressUpdate(0f)
            onAddSuccess(emptyMap())
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

    fun logFirebaseError(operation: String, error: Exception) {
        val timestamp = System.currentTimeMillis()
        val errorMessage = """
            |Firebase Operation Error:
            |Timestamp: $timestamp
            |Operation: $operation
            |Error Type: ${error::class.simpleName}
            |Error Message: ${error.message}
            |Stack Trace: ${error.stackTrace.joinToString("\n")}
            |Database Path: ${ref.toString()}
        """.trimMargin()

        println(errorMessage)
    }
}
