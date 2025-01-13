package com.example.Z_AppsFather.Kotlin._3.Init

import Y_AppsFather.Kotlin.ModelAppsFather
import Y_AppsFather.Kotlin.ModelAppsFather.ProduitModel
import Y_AppsFather.Kotlin.ViewModelInitApp
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
        val products = loadProducts()

        initViewModel.apply {
            _modelAppsFather.produitsMainDataBase.clear()
            _modelAppsFather.produitsMainDataBase.addAll(products)

            updateProduitsAvecBonsGrossist()
            initializationProgress = 1f
        }
    } catch (e: Exception) {
        throw e
    }

    private suspend fun loadProducts() = suspendCancellableCoroutine { continuation ->
        ModelAppsFather.produitsFireBaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) = try {
                val products = snapshot.children
                    .mapNotNull { parseProduct(it) }
                    .toMutableStateList()
                continuation.resume(products)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }

    private fun parseProduct(snapshot: DataSnapshot): ProduitModel? {
        val productId = snapshot.key?.toLongOrNull() ?: return null
        val productMap = snapshot.value as? Map<*, *> ?: return null

        return try {
            ProduitModel(
                id = productId,
                itsTempProduit = (productMap["itsTempProduit"] as? Boolean) ?: false,
                init_nom = (productMap["nom"] as? String) ?: "",
                init_besoin_To_Be_Updated = (productMap["besoin_To_Be_Updated"] as? Boolean) ?: false,
                initialNon_Trouve = (productMap["non_Trouve"] as? Boolean) ?: false,
                init_visible = false,
            ).apply {
                snapshot.child("statuesBase").getValue(ProduitModel.StatuesBase::class.java)?.let {
                    statuesBase = it
                    statuesBase.imageGlidReloadTigger = 0
                }

                parseList<ProduitModel.ColourEtGout_Model>("coloursEtGoutsList", snapshot) {
                    coloursEtGoutsList = it
                }

                parseList<ProduitModel.ClientBonVentModel>("bonsVentDeCetteCotaList", snapshot) {
                    bonsVentDeCetteCotaList = it
                }

                parseList<ProduitModel.ClientBonVentModel>("historiqueBonsVentsList", snapshot) {
                    historiqueBonsVentsList = it
                }

                parseList<ProduitModel.GrossistBonCommandes>("historiqueBonsCommendList", snapshot) {
                    historiqueBonsCommendList = it
                }

//                snapshot.child("bonCommendDeCetteCota").let { bonCommendSnapshot ->
//                    if (bonCommendSnapshot.exists()) {
//                        bonCommendDeCetteCota = bonCommendSnapshot.getValue(ProduitModel.GrossistBonCommandes::class.java)?.apply {
//                            grossistInformations = snapshot.child("bonCommendDeCetteCota/grossistInformations")
//                                .getValue(ProduitModel.GrossistBonCommandes.GrossistInformations::class.java)
//
//                            parseList<ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee>(
//                                "coloursEtGoutsCommendeeList",
//                                bonCommendSnapshot
//                            ) { coloursEtGoutsCommendeList = it }
//                        }
//                    }
//                }
            }
        } catch (e: Exception) {
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
            // Silent fail
        }
    }
}
