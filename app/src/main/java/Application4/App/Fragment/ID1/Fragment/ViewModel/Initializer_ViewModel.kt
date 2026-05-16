package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application4.App.Fragment.ID1.Fragment.ProductListFilterLogic
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
import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import android.widget.Toast
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class Initializer_ViewModel(private val AViewModel_NewProtoPatterns: A_ViewModel_NewProtoPatterns) {

    /** Full start: loads data AND starts the periodic compt-key watcher. Call only once. */
    fun run() {
        collect_ListDatas()
        startPeriodicComptKeyCheck()
    }

    /**
     * Lightweight reload: re-runs the data load pipeline without spawning another
     * periodic-check loop. Use this for retries and on-screen re-entry so that
     * [startPeriodicComptKeyCheck] is never launched more than once per ViewModel lifetime.
     */
    fun reload() {
        collect_ListDatas()
    }

    /**
     * Periodically verifies that active_M9Compt.keyID matches the expected
     * au_Lence_Set_Compt_Ac_KeyId. When they differ (or the compt is null),
     * fetches from Firebase, upserts to the local DAO and refreshes active_Datas.
     *
     * Note: the initial seeding of active_M9Compt is handled synchronously in
     * [loadAllDatasOnce] (local DB first, Firebase fallback). This loop only
     * takes over afterwards to keep the compt in sync if the key changes at runtime
     * (e.g. after a hot-swap of accounts).
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
                        try {
                            child.getValue(M09AppCompt::class.java)
                        } catch (_: Exception) {
                            null
                        }
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
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            loadAllDatasOnce()
        }
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.Main) {
            snapshotFlow { AViewModel_NewProtoPatterns.active_Datas.filterAffichageMode_Proto }
                .drop(1)
                .collect { withContext(Dispatchers.IO) { reloadPassiveColourFilter() } }
        }
    }

    private suspend fun reloadPassiveColourFilter() {
        val limit = AViewModel_NewProtoPatterns.active_Datas
            .active_M9Compt?.limite_couleurs_ou_leur_last_achate_est_moin_que_jour
        val mode = AViewModel_NewProtoPatterns.active_Datas.filterAffichageMode_Proto
        val colours = AViewModel_NewProtoPatterns.appDatabase.dao_M03CouleurProduitInfos().getAll()
            .filter_passive_datas(limit)
            .let {
                if (mode == Filter_Affichage_Mode_Proto.Panie ||
                    mode == Filter_Affichage_Mode_Proto.Panie_Si_Couleur_Ac_Vent_Affiche_Tout_Ces_Freres
                ) it
                else ProductListFilterLogic.filterByDepot(it)
            }
        AViewModel_NewProtoPatterns.active_Datas.list_M03CouleurProduitInfos = colours
        val products = AViewModel_NewProtoPatterns.appDatabase.dao_M1Produit().getAll()
            .filter_passive(colours.map { it.parentBProduitInfosKeyID }.distinct())
        AViewModel_NewProtoPatterns.active_Datas.list_M1Produit = products
    }

    private suspend fun loadAllDatasOnce() {
        fun progress(p: Float) {
            AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value =
                AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value.copy(
                    initDatasProgressEtate = p
                )
        }

        progress(1 / 9f)

        val appCompt: M09AppCompt? = run {
            val key = M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
            val local = AViewModel_NewProtoPatterns.appDatabase
                .dao_M9AppCompt()
                .getAll()
                .find { it.keyID == key }
            if (local != null) return@run local
            val snap = withTimeoutOrNull(10_000L) { M09AppCompt.ref.get().await() }
            val remote = snap?.children?.mapNotNull { child ->
                try {
                    child.getValue(M09AppCompt::class.java)
                } catch (_: Exception) {
                    null
                }
            }?.find { it.keyID == key }

            remote?.let { AViewModel_NewProtoPatterns.appDatabase.dao_M9AppCompt().upsert(it) }
            remote
        }

        val limiteCouleursOuLeurLastAchateEstMoinQueJour =
            appCompt?.limite_couleurs_ou_leur_last_achate_est_moin_que_jour

        val colours = AViewModel_NewProtoPatterns.appDatabase.dao_M03CouleurProduitInfos().getAll()
            .filter_passive_datas(limiteCouleursOuLeurLastAchateEstMoinQueJour)
            .let {
                val mode = AViewModel_NewProtoPatterns.active_Datas.filterAffichageMode_Proto
                if (mode == Filter_Affichage_Mode_Proto.Panie ||
                    mode == Filter_Affichage_Mode_Proto.Panie_Si_Couleur_Ac_Vent_Affiche_Tout_Ces_Freres
                ) it
                else ProductListFilterLogic.filterByDepot(it)
            }

        progress(2 / 9f)
        val products = AViewModel_NewProtoPatterns.appDatabase.dao_M1Produit().getAll()
            .filter_passive(colours.map { it.parentBProduitInfosKeyID }.distinct())

        progress(3 / 9f)
        val clients = AViewModel_NewProtoPatterns.appDatabase.dao_M2Client().getAll()
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
        val bonVent = AViewModel_NewProtoPatterns.appDatabase.dao_M8BonVent().getAll()
        progress(7 / 9f)
        val ventPeriodes = AViewModel_NewProtoPatterns.appDatabase.dao_M14VentPeriode().getAll()
        progress(8 / 9f)
        val tarification =
            AViewModel_NewProtoPatterns.appDatabase.dao_M13TarificationInfos().getAll()
                .filter_passive(products.map { it.keyID }.distinct())

        val operationVentCouleurs =
            AViewModel_NewProtoPatterns.appDatabase.dao_M10OperationVentCouleur().getAll()

        seedActiveDatas(
            appCompt = appCompt,
            ventPeriodes= AViewModel_NewProtoPatterns.appDatabase.dao_M14VentPeriode().getAll().find {
                it.keyID ==appCompt.current_OnVent_M14VentPeriode_KeyID
            },
            bonVent = bonVent,
            clients = clients,
            categories = categories,
            products = products,
            colours = colours,
            allOperations = operationVentCouleurs,
        )

        AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value =
            AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value.copy(
                initDatasProgressEtate = 1f,
                list_Datas = List_Datas(
                    m2Client = clients,
                    m14VentPeriode = ventPeriodes,
                    m16CategorieProduit = categories,
                    m8BonVent = bonVent,
                    m13TarificationInfos = tarification,
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
        ventPeriodes: M14VentPeriode?,
    ) {
        AViewModel_NewProtoPatterns.active_Datas.active_M9Compt = appCompt
        AViewModel_NewProtoPatterns.active_Datas.active_PeriodVent = ventPeriodes
        AViewModel_NewProtoPatterns.active_Datas.list_M8BonVent = bonVent
        AViewModel_NewProtoPatterns.active_Datas.list_M2Client = clients
        AViewModel_NewProtoPatterns.active_Datas.list_M16CategorieProduit = categories
        AViewModel_NewProtoPatterns.active_Datas.list_M1Produit = products
        AViewModel_NewProtoPatterns.active_Datas.list_M03CouleurProduitInfos = colours
        AViewModel_NewProtoPatterns.active_Datas.list_M10OperationVentCouleur = allOperations
    }
}
