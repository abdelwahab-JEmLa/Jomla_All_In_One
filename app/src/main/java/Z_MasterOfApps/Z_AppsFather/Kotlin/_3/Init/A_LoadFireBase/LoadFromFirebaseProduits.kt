package Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.A_LoadFireBase

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.runtime.toMutableStateList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object LoadFromFirebaseProduits {
    private const val TAG = "LoadFromFirebaseProduits"
    private var realtimeListener: ValueEventListener? = null

    suspend fun loadFromFirebase(initViewModel: ViewModelInitApp) {
        try {
            initViewModel.loadingProgress = 0.1f

            // Charger les données avec la nouvelle méthode simplifiée
            val snapshot = FirebaseOfflineHandler.loadData(_ModelAppsFather.produitsFireBaseRef)

            snapshot?.let {
                // Traiter les données
                val products = parseSnapshot(it)
                updateViewModel(initViewModel, products)
                initViewModel.loadingProgress = 0.5f

                // Configurer la synchronisation en temps réel
                setupRealtimeSync(initViewModel)
            }

            initViewModel.loadingProgress = 1.0f
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load data from Firebase", e)
            throw e
        }
    }

    private fun parseSnapshot(snapshot: DataSnapshot): List<ProduitModel> {
        return snapshot.children.mapNotNull { childSnapshot ->
            try {
                parseProduct(childSnapshot)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse product ${childSnapshot.key}", e)
                null
            }
        }.toMutableStateList()
    }

    fun parseProduct(snapshot: DataSnapshot): ProduitModel? {
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
                // Parse StatuesBase
                snapshot.child("statuesBase").getValue(ProduitModel.StatuesBase::class.java)?.let {
                    statuesBase = it
                    statuesBase.imageGlidReloadTigger = 0
                }

                // Parse BonCommend
                snapshot.child("bonCommendDeCetteCota").let { bonCommendSnapshot ->
                    if (bonCommendSnapshot.exists()) {
                        bonCommendDeCetteCota = bonCommendSnapshot.getValue(ProduitModel.GrossistBonCommandes::class.java)?.apply {
                            grossistInformations = bonCommendSnapshot.child("grossistInformations")
                                .getValue(ProduitModel.GrossistBonCommandes.GrossistInformations::class.java)

                            bonCommendSnapshot.child("mutableBasesStates")
                                .getValue(ProduitModel.GrossistBonCommandes.MutableBasesStates::class.java)?.let {
                                    mutableBasesStates = it
                                }

                            // Parser les listes avec parseChild
                            FirebaseOfflineHandler.parseChild<ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee>(
                                "coloursEtGoutsCommendeeList",
                                bonCommendSnapshot
                            ) { coloursEtGoutsCommendeeList = it }
                        }
                    }
                }

                // Parse les autres listes
                FirebaseOfflineHandler.parseChild<ProduitModel.ColourEtGout_Model>(
                    "coloursEtGoutsList",
                    snapshot
                ) { coloursEtGoutsList = it }

                FirebaseOfflineHandler.parseChild<ProduitModel.ClientBonVentModel>(
                    "bonsVentDeCetteCotaList",
                    snapshot
                ) { bonsVentDeCetteCotaList = it }

                FirebaseOfflineHandler.parseChild<ProduitModel.ClientBonVentModel>(
                    "historiqueBonsVentsList",
                    snapshot
                ) { historiqueBonsVentsList = it }

                FirebaseOfflineHandler.parseChild<ProduitModel.GrossistBonCommandes>(
                    "historiqueBonsCommendList",
                    snapshot
                ) { historiqueBonsCommendList = it }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse product ID $productId", e)
            null
        }
    }

    private fun setupRealtimeSync(initViewModel: ViewModelInitApp) {
        realtimeListener?.let {
            _ModelAppsFather.produitsFireBaseRef.removeEventListener(it)
        }

        realtimeListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val products = parseSnapshot(snapshot)
                    updateViewModel(initViewModel, products)
                } catch (e: Exception) {
                    Log.e(TAG, "Real-time sync data processing failed", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Real-time sync failed: ${error.message}")
            }
        }.also {
            _ModelAppsFather.produitsFireBaseRef.addValueEventListener(it)
        }
    }

    private fun updateViewModel(
        initViewModel: ViewModelInitApp,
        products: List<ProduitModel>
    ) {
        try {
            initViewModel.apply {
                _modelAppsFather.produitsMainDataBase.clear()
                _modelAppsFather.produitsMainDataBase.addAll(products)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update ViewModel", e)
        }
    }
}
