package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.List_Datas
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit.Companion.filter_passive
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit.Companion.filter_passive
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos.Companion.filter_passive_datas
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos.Companion.filter_passive
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class Initializer_ViewModel(private val AViewModel_NewProtoPatterns: A_ViewModel_NewProtoPatterns) {

    fun run() {
        collect_ListDatas()
        startPeriodicComptKeyCheck()
    }

    /**
     * Periodically verifies that active_M9Compt.keyID matches the expected
     * au_Lence_Set_Compt_Ac_KeyId. When they differ (or the compt is null),
     * fetches from Firebase, upserts to the local DAO and refreshes active_Datas.
     */
    private fun startPeriodicComptKeyCheck() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            val expectedKey =
                M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId

            while (isActive) {
                delay(5_000L)

                val currentKey = AViewModel_NewProtoPatterns.active_Datas.active_M9Compt?.keyID
                if (currentKey == expectedKey) continue          // already in sync — nothing to do

                // Key mismatch (or compt is null) → pull from Firebase
                val snap = withTimeoutOrNull(10_000L) {
                    M09AppCompt.ref.get().await()
                }
                if (snap == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            AViewModel_NewProtoPatterns.repositorysMainSetter_NewProtoPatterns.context,
                            "Erreur : délai dépassé — compte introuvable (Firebase)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    continue                                      // timed-out — retry next tick
                }

                val remote = snap.children
                    .mapNotNull { child ->
                        try { child.getValue(M09AppCompt::class.java) }
                        catch (_: Exception) { null }
                    }
                    .find { it.keyID == expectedKey }
                if (remote == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            AViewModel_NewProtoPatterns.repositorysMainSetter_NewProtoPatterns.context,
                            "Erreur : compte introuvable sur Firebase (clé : $expectedKey)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    continue                                      // not found remotely — retry next tick
                }

                // Persist locally then expose to the UI layer
                AViewModel_NewProtoPatterns.appDatabase.dao_M9AppCompt().upsert(remote)
                AViewModel_NewProtoPatterns.active_Datas.active_M9Compt = remote
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    private fun collect_ListDatas() {
        load_then_Collect_Active_Datas()
        collectListM16()
        collectListM1Produit()
        collectList_M3()
        collectList_M8BonVent()
        collectList_M2Client()
        collectList_M10OperationVentCouleur_All()
    }

    private fun load_then_Collect_Active_Datas() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            loadAllDatasOnce()
            collectActiveM9Compt()
        }
    }

    private suspend fun loadAllDatasOnce() {
        fun progress(p: Float) {
            AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value =
                AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value.copy(
                    initDatasProgressEtate = p
                )
        }
        progress(1 / 9f)

        // Try local DB first; fall back to Firebase if the compt is not yet cached locally.
        val appCompt: M09AppCompt? = run {
            val local = AViewModel_NewProtoPatterns.appDatabase
                .dao_M9AppCompt()
                .getBy_M00_Lence_Key_Flow()
                .firstOrNull()
            if (local != null) return@run local

            val key  = M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
            val snap = withTimeoutOrNull(10_000L) { M09AppCompt.ref.get().await() }
            val remote = snap?.children?.mapNotNull { child ->
                try { child.getValue(M09AppCompt::class.java) }
                catch (_: Exception) { null }
            }?.find { it.keyID == key }

            remote?.let { AViewModel_NewProtoPatterns.appDatabase.dao_M9AppCompt().upsert(it) }
            remote
        }

        val limiteCouleursOuLeurLastAchateEstMoinQueJour =
            appCompt?.limite_couleurs_ou_leur_last_achate_est_moin_que_jour

        val colours   = AViewModel_NewProtoPatterns.appDatabase.dao_M03CouleurProduitInfos().getAll().filter_passive_datas(
            1000
        )

        progress(2 / 9f)
        val products  = AViewModel_NewProtoPatterns.appDatabase.dao_M1Produit().getAll()
            .filter_passive(colours.map { it.parentBProduitInfosKeyID }.distinct())

        progress(3 / 9f)
        val clients   = AViewModel_NewProtoPatterns.appDatabase.dao_M2Client().getAll()
        progress(4 / 9f)

        val categories = AViewModel_NewProtoPatterns.appDatabase.dao_16CategorieProduit().getAll()
            .filter_passive(products.map { it.idParentCategorie }.distinct())

        progress(5 / 9f)

        if (appCompt == null) {
            AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value =
                AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value.copy(
                    initDatasProgressEtate = 0f
                )
            return
        }

        progress(6 / 9f)
        val bonVent        = AViewModel_NewProtoPatterns.appDatabase.dao_M8BonVent().getAll()
        progress(7 / 9f)
        val ventPeriodes   = AViewModel_NewProtoPatterns.appDatabase.dao_M14VentPeriode().getAll()
        progress(8 / 9f)
        val tarification   = AViewModel_NewProtoPatterns.appDatabase.dao_M13TarificationInfos().getAll()
            .filter_passive(products.map { it.keyID }.distinct())

        val operationVentCouleurs =
            AViewModel_NewProtoPatterns.appDatabase.dao_M10OperationVentCouleur().getAll()

        seedActiveDatas(
            appCompt      = appCompt,
            bonVent       = bonVent,
            clients       = clients,
            categories    = categories,
            products      = products,
            colours       = colours,
            allOperations = operationVentCouleurs,
        )

        AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value =
            AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value.copy(
                initDatasProgressEtate = 1f,
                list_Datas = List_Datas(
                    m2Client                = clients,
                    m14VentPeriode          = ventPeriodes,
                    m16CategorieProduit     = categories,
                    m8BonVent               = bonVent,
                    m13TarificationInfos    = tarification,
                    m10OperationVentCouleur = operationVentCouleurs,
                )
            )
    }

    private fun seedActiveDatas(
        appCompt: M09AppCompt?,
        bonVent: List<M8BonVent>,
        clients: List<M2Client>,
        categories: List<M16CategorieProduit>,
        products: List<M01Produit>,
        colours: List<M3CouleurProduitInfos>,
        allOperations: List<M10OperationVentCouleur>,
    ) {
        AViewModel_NewProtoPatterns.active_Datas.active_M9Compt               = appCompt
        AViewModel_NewProtoPatterns.active_Datas.list_M8BonVent               = bonVent
        AViewModel_NewProtoPatterns.active_Datas.list_M2Client                = clients
        AViewModel_NewProtoPatterns.active_Datas.list_M16CategorieProduit     = categories
        AViewModel_NewProtoPatterns.active_Datas.list_M1Produit               = products
        AViewModel_NewProtoPatterns.active_Datas.list_M03CouleurProduitInfos  = colours
        AViewModel_NewProtoPatterns.active_Datas.list_M10OperationVentCouleur = allOperations
        AViewModel_NewProtoPatterns.active_Datas.its_Panie_Mode               =
            appCompt?.its_Panie_Mode_Au_Lence_Boutique ?: false
    }

    private suspend fun collectActiveM9Compt() {
        FlowsFunctions_ActiveDatasFragNewProto.getFlow_active_M9Compt_By_au_Lence_Set_Compt_Ac_KeyId(
            dao_M9AppCompt          = AViewModel_NewProtoPatterns.appDatabase.dao_M9AppCompt(),
            activeDatasFragNewProto = AViewModel_NewProtoPatterns.active_Datas,
        ).collect { }
    }

    private fun collectListM16() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            AViewModel_NewProtoPatterns.appDatabase.dao_16CategorieProduit().getAllFlow()
                .collect { AViewModel_NewProtoPatterns.active_Datas.list_M16CategorieProduit = it }
        }
    }

    private fun collectListM1Produit() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            FlowsFunctions_ActiveDatasFragNewProto.getFlow_list_M1Produit(
                dao_M1Produit           = AViewModel_NewProtoPatterns.appDatabase.dao_M1Produit(),
                activeDatasFragNewProto = AViewModel_NewProtoPatterns.active_Datas,
            ).collect { }
        }
    }

    private fun collectList_M3() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            AViewModel_NewProtoPatterns.appDatabase.dao_M03CouleurProduitInfos().getAllFlow()
                .collect { AViewModel_NewProtoPatterns.active_Datas.list_M03CouleurProduitInfos = it }
        }
    }

    private fun collectList_M8BonVent() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            AViewModel_NewProtoPatterns.appDatabase.dao_M8BonVent().getAllFlow()
                .collect { AViewModel_NewProtoPatterns.active_Datas.list_M8BonVent = it }
        }
    }

    private fun collectList_M2Client() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            AViewModel_NewProtoPatterns.appDatabase.dao_M2Client().getAllFlow()
                .collect { AViewModel_NewProtoPatterns.active_Datas.list_M2Client = it }
        }
    }

    private fun collectList_M10OperationVentCouleur_All() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            AViewModel_NewProtoPatterns.appDatabase.dao_M10OperationVentCouleur().getAllFlow()
                .collect { AViewModel_NewProtoPatterns.active_Datas.list_M10OperationVentCouleur = it }
        }
    }

}
