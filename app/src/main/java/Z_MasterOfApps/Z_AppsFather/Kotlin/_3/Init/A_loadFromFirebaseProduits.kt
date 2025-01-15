package Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.toMutableStateList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object LoadFromFirebaseHandler {
    private const val DEBUG_LIMIT = 7

    suspend fun loadFromFirebase(initViewModel: ViewModelInitApp) = try {
        FirebaseDataLogger.startLogging()

        val products = loadProducts()

        FirebaseDataLogger.logDataValidation(products)

        initViewModel.apply {
            _modelAppsFather.produitsMainDataBase.clear()
            _modelAppsFather.produitsMainDataBase.addAll(products)
            FirebaseDataLogger.logStateUpdate(products, "Database Updated")
            this.loadingProgress = 1f
        }

        val duration = System.currentTimeMillis() - System.currentTimeMillis()
        FirebaseDataLogger.logLoadingComplete(products.size, duration)

    } catch (e: Exception) {
        FirebaseDataLogger.logDatabaseError(e, "LoadFromFirebase")
        throw e
    }

    private suspend fun loadProducts() = suspendCancellableCoroutine { continuation ->
        _ModelAppsFather.produitsFireBaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) = try {
                FirebaseDataLogger.logSnapshotDetails(snapshot)

                val products = snapshot.children.mapNotNull { childSnapshot ->
                    val product = parseProduct(childSnapshot)
                    FirebaseDataLogger.logProductParsing(childSnapshot, product)
                    product
                }.toMutableStateList()

                continuation.resume(products)
            } catch (e: Exception) {
                FirebaseDataLogger.logDatabaseError(e, "LoadProducts")
                continuation.resumeWithException(e)
            }

            override fun onCancelled(error: DatabaseError) {
                FirebaseDataLogger.logDatabaseError(error.toException(), "Database Operation Cancelled")
                continuation.resumeWithException(error.toException())
            }
        })
    }

    fun parseProduct(snapshot: DataSnapshot): _ModelAppsFather.ProduitModel? {
        val productId = snapshot.key?.toLongOrNull() ?: return null
        val productMap = snapshot.value as? Map<*, *> ?: return null

        return try {
            _ModelAppsFather.ProduitModel(
                id = productId,
                itsTempProduit = (productMap["itsTempProduit"] as? Boolean) ?: false,
                init_nom = (productMap["nom"] as? String) ?: "",
                init_besoin_To_Be_Updated = (productMap["besoin_To_Be_Updated"] as? Boolean)
                    ?: false,
                initialNon_Trouve = (productMap["non_Trouve"] as? Boolean) ?: false,
                init_visible = false,
            ).apply {
                snapshot.child("statuesBase").getValue(_ModelAppsFather.ProduitModel.StatuesBase::class.java)?.let {
                    statuesBase = it
                    statuesBase.imageGlidReloadTigger = 0
                }

                parseList<_ModelAppsFather.ProduitModel.ColourEtGout_Model>("coloursEtGoutsList", snapshot) {
                    coloursEtGoutsList = it
                }

                parseList<_ModelAppsFather.ProduitModel.ClientBonVentModel>("bonsVentDeCetteCotaList", snapshot) {
                    bonsVentDeCetteCotaList = it
                }

                parseList<_ModelAppsFather.ProduitModel.ClientBonVentModel>("historiqueBonsVentsList", snapshot) {
                    historiqueBonsVentsList = it
                }

                parseList<_ModelAppsFather.ProduitModel.GrossistBonCommandes>("historiqueBonsCommendList", snapshot) {
                    historiqueBonsCommendList = it
                }
            }
        } catch (e: Exception) {
            FirebaseDataLogger.logDatabaseError(e, "Product Parse Error: ID $productId")
            null
        }
    }

    private inline fun <reified T> parseList(
        path: String,
        snapshot: DataSnapshot,
        crossinline onSuccess: (List<T>) -> Unit
    ) {
        try {
            val type = object : GenericTypeIndicator<List<T>>() {}
            snapshot.child(path).getValue(type)?.let(onSuccess)
        } catch (e: Exception) {
            FirebaseDataLogger.logDatabaseError(e, "Parse List Error: $path")
        }
    }
}
