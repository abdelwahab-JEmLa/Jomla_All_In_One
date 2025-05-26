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
                        onAddSuccess(mappedTarifications, mappedProducts)
                        onProgressUpdate(1f)

                    } else {
                        onProgressUpdate(1f)
                        onAddSuccess(emptyList(), emptyList())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    onProgressUpdate(0f)
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
                    val (originalCount, resultMap) = extracteFrom_getAncienDB_changeKeysFireBase(refDBJetPackExport)
                    continuation.resume(Pair(originalCount, resultMap))
                } catch (e: Exception) {
                    e.printStackTrace()
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

            if (datas.isEmpty()) {
                onProgressUpdate(1f)
                return@withContext emptyMap()
            }

            onProgressUpdate(0.3f)
            val dataMap = mutableMapOf<String, Any>()
            val resultMap = mutableMapOf<String, DataBase>()
            val processedCount = 0
            val totalCount = datas.size

            extractedFrom_setDataInlineFunFixed<DataBase>(datas, processedCount, dataMap, resultMap, totalCount)

            return@withContext resultMap

        } catch (e: Exception) {
            e.printStackTrace()
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

            var processedCount1 = 0
            val totalCount = mapData.size

            extractedFromeupsertAllAndReturnListIdToData(mapData, tariffsMap, resultMap, processedCount1, totalCount)
            onAddSuccess(resultMap)

        } catch (e: Exception) {
            e.printStackTrace()
            onProgressUpdate(0f)
            onAddSuccess(emptyMap())
        }
    }
}
