package Z_MasterOfApps.Kotlin.ViewModel.Init.Init

import Z_MasterOfApps.Kotlin.Model.ClientsDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.runtime.toMutableStateList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object LoadFromFirebaseProduits {
    private const val TAG = "FirebaseLoader"
    private var realtimeListener: ValueEventListener? = null
    private var clientListener: ValueEventListener? = null

    suspend fun loadFromFirebase(viewModel: ViewModelInitApp) {
        try {
            Log.d(TAG, "🚀 Starting data loading...")
            viewModel.loadingProgress = 0.1f

            val (prodSnapshot, clientSnapshot) = FirebaseOfflineHandler.loadData(
                ref = _ModelAppsFather.produitsFireBaseRef,
                refClientsDataBase = ClientsDataBase.refClientsDataBase,
                viewModel = viewModel
            )

            val products = prodSnapshot?.let { parseProducts(it) } ?: emptyList()
            val clients = clientSnapshot?.let { parseClients(it) } ?: emptyList()

            CoroutineScope(Dispatchers.Main).launch {
                viewModel.modelAppsFather.produitsMainDataBase.apply {
                    clear()
                    addAll(products)
                }
                viewModel.modelAppsFather.clientDataBaseSnapList.apply {
                    clear()
                    addAll(clients)
                }
                viewModel.loadingProgress = 0.7f
                setupRealtimeUpdates(viewModel)
                viewModel.loadingProgress = 1.0f
                Log.d(TAG, "✅ Loading completed successfully")
            }

        } catch (e: Exception) {
            Log.e(TAG, "💥 Loading failed", e)
            viewModel.loadingProgress = -1f
            throw e
        }
    }

    fun parseProducts(snapshot: DataSnapshot): List<ProduitModel> {
        return snapshot.children.mapNotNull { parseProduct(it) }.toMutableStateList()
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
    fun parseClients(snapshot: DataSnapshot): List<ClientsDataBase> {
        return snapshot.children.mapNotNull { parseClient(it) }.toMutableStateList()
    }

    fun parseClient(snapshot: DataSnapshot): ClientsDataBase? {
        return try {
            val clientMap = snapshot.value as? Map<*, *> ?: return null
            ClientsDataBase(
                id = snapshot.key?.toLongOrNull() ?: return null,
                nom = clientMap["nom"] as? String ?: ""
            ).apply {
                snapshot.child("statueDeBase").getValue(ClientsDataBase.StatueDeBase::class.java)?.let {
                    statueDeBase = it
                }
                snapshot.child("gpsLocation").getValue(ClientsDataBase.GpsLocation::class.java)?.let {
                    gpsLocation = it
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing client ${snapshot.key}", e)
            null
        }
    }

    private fun setupRealtimeUpdates(viewModel: ViewModelInitApp) {
        realtimeListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModel.modelAppsFather.produitsMainDataBase.apply {
                    clear()
                    addAll(parseProducts(snapshot))
                    Log.d(TAG, "Real-time products update: ${size} items")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Products real-time error: ${error.message}")
            }
        }

        clientListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModel.modelAppsFather.clientDataBaseSnapList.apply {
                    clear()
                    addAll(parseClients(snapshot))
                    Log.d(TAG, "Real-time clients update: ${size} items")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Clients real-time error: ${error.message}")
            }
        }

        _ModelAppsFather.produitsFireBaseRef.addValueEventListener(realtimeListener!!)
        ClientsDataBase.refClientsDataBase.addValueEventListener(clientListener!!)
        Log.d(TAG, "🔔 Real-time listeners activated")
    }

    fun cleanup() {
        realtimeListener?.let { _ModelAppsFather.produitsFireBaseRef.removeEventListener(it) }
        clientListener?.let { ClientsDataBase.refClientsDataBase.removeEventListener(it) }
        Log.d(TAG, "🧹 Listeners cleaned up")
    }
}
