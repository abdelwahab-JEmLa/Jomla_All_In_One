package Z_MasterOfApps.Kotlin.ViewModel.Init.Init

import Z_MasterOfApps.Kotlin.Model.B_ClientsDataBase
import Z_MasterOfApps.Kotlin.Model.C_GrossistsDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.FirebaseListeners
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

private var isInitialized = false

fun initializeFirebase(app: FirebaseApp) {
    if (!isInitialized) {
        try {
            FirebaseDatabase.getInstance(app).apply {
                setPersistenceEnabled(true)
                setPersistenceCacheSizeBytes(100L * 1024L * 1024L)
            }
            isInitialized = true
        } catch (_: Exception) {}
    }
}

suspend fun loadData( viewModel: ViewModelInitApp) {
    try {
        viewModel.loadingProgress = 0.1f

        val refs = listOf(
            _ModelAppsFather.ref_HeadOfModels,
            _ModelAppsFather.produitsFireBaseRef,
            B_ClientsDataBase.refClientsDataBase
        ).onEach { it.keepSynced(true) }

        val isOnline = withTimeoutOrNull(3000L) {
            refs[1].child("test").setValue(true).await()
            refs[1].child("test").removeValue().await()
            true
        } ?: false

        val snapshots = if (isOnline) {
            FirebaseListeners.setupRealtimeListeners(viewModel)
            refs.map { it.get().await() }
        } else {
            FirebaseDatabase.getInstance().goOffline()
            refs.map { ref ->
                withTimeoutOrNull(5000L) {
                    ref.get().await()
                }
            }.also { FirebaseDatabase.getInstance().goOnline() }
        }

        val (headModels, products, clients) = snapshots

        withContext(Dispatchers.Main) {
            viewModel.modelAppsFather.apply {
                produitsMainDataBase.clear()
                products?.children?.forEach { snap ->
                    val map = snap.value as? Map<*, *> ?: return@forEach
                    val prod = ProduitModel(
                        id = snap.key?.toLongOrNull() ?: return@forEach,
                        itsTempProduit = map["itsTempProduit"] as? Boolean ?: false,
                        init_nom = map["nom"] as? String ?: "",
                        init_besoin_To_Be_Updated = map["besoin_To_Be_Updated"] as? Boolean ?: false,
                        initialNon_Trouve = map["non_Trouve"] as? Boolean ?: false,
                        init_visible = false
                    ).apply {
                        snap.child("statuesBase").getValue(ProduitModel.StatuesBase::class.java)
                            ?.let {
                                statuesBase = it
                                statuesBase.imageGlidReloadTigger = 0
                            }
                    }
                    produitsMainDataBase.add(prod)
                }

                clientDataBase.clear()
                clients?.children?.forEach { snap ->
                    val map = snap.value as? Map<*, *> ?: return@forEach
                    B_ClientsDataBase(
                        id = snap.key?.toLongOrNull() ?: return@forEach,
                        nom = map["nom"] as? String ?: ""
                    ).apply {
                        snap.child("statueDeBase")
                            .getValue(B_ClientsDataBase.StatueDeBase::class.java)?.let {
                                statueDeBase = it
                            }
                        snap.child("gpsLocation")
                            .getValue(B_ClientsDataBase.GpsLocation::class.java)?.let {
                                gpsLocation = it
                            }
                        clientDataBase.add(this)
                    }
                }

                grossistsDataBase.clear()
                if (headModels != null) {
                    val grossistsNode = headModels.child("C_GrossistsDataBase")
                    if (!grossistsNode.exists()) {
                        grossistsDataBase.add(C_GrossistsDataBase(
                            id = 1,
                            nom = "Default Grossist",
                            statueDeBase = C_GrossistsDataBase.StatueDeBase(
                                cUnClientTemporaire = true
                            )
                        ))
                    } else {
                        grossistsNode.children.forEach { snap ->
                            try {
                                val map = snap.value as? Map<*, *> ?: return@forEach
                                C_GrossistsDataBase(
                                    id = snap.key?.toLongOrNull() ?: return@forEach,
                                    nom = map["nom"] as? String ?: "Non Defini"
                                ).apply {
                                    snap.child("statueDeBase")
                                        .getValue(C_GrossistsDataBase.StatueDeBase::class.java)?.let {
                                            statueDeBase = it
                                        }
                                    grossistsDataBase.add(this)
                                }
                            } catch (e: Exception) {
                                // Silent catch to skip invalid entries
                            }
                        }
                    }
                }
            }
            viewModel.loadingProgress = 1.0f
        }
    } catch (e: Exception) {
        viewModel.loadingProgress = -1f
        throw e
    }
}
