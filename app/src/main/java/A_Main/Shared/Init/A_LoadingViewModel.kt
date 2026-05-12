package A_Main.Shared.Init

import A_Main.Shared.Module.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Models.AppType
import EntreApps.Shared.Models.Do
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.Jomla_Clients
import EntreApps.Shared.Modules.Base.AppDatabase
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class A_LoadingViewModel(
    private val appDatabase: AppDatabase,
    private val appContext: Context,
) : ViewModel() {

    data class LoadingUiState(
        val initDone: Boolean = false,
        val progress: Float = 0f,
        val currentJobName: String = "",
        val activeCompt: M09AppCompt? = null,
        val seedResult: SeedResult =
            SeedResult(),
        val lightDataBasesResult: Init_LightDataBases.LightDataBasesResult =
            Init_LightDataBases.LightDataBasesResult(),
    )

    private val _uiState = MutableStateFlow(LoadingUiState())
    val uiState = _uiState.asStateFlow()

    private val initMutex = Mutex()
    private var initStarted = false
    private val repo by lazy { RepositorysMainSetter_NewProtoPatterns(appDatabase, appContext) }

    val isPresenter = false

    private fun setProgressAndLog(p: Float, job: String) {
        _uiState.update { it.copy(progress = p, currentJobName = job) }
    }

    fun startIfNeeded(context: Context) {
        viewModelScope.launch {
            initMutex.withLock {
                if (initStarted) return@launch
                initStarted = true
            }
            runInit()
        }
    }

    private suspend fun runInit() {
        fun setProgress(p: Float, job: String = _uiState.value.currentJobName) =
            setProgressAndLog(p, job)

        setProgressAndLog(0.02f, "Démarrage…")

        viewModelScope.launch(Dispatchers.IO) {
            val key = M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
            val snap = withTimeoutOrNull(10_000L) { M09AppCompt.ref.get().await() }

            val compt = snap?.children?.mapNotNull { child ->
                try {
                    child.getValue(M09AppCompt::class.java)
                } catch (_: Exception) {
                    @Suppress("UNCHECKED_CAST")
                    val raw = child.getValue(Object::class.java) as? Map<String, Any?> ?: return@mapNotNull null
                    val rawDo = raw["next_start"] as? String
                    val safeDo = Do.entries.firstOrNull { it.name == rawDo } ?: Do.StandartInit_Sans_RienFair
                    try {
                        M09AppCompt(
                            keyID      = raw["keyID"] as? String ?: return@mapNotNull null,
                            nom        = raw["nom"]   as? String ?: "",
                            next_start = safeDo,
                        )
                    } catch (_: Exception) { null }
                }
            }?.find { it.keyID == key }

            compt?.let { appDatabase.dao_M9AppCompt().upsert(it) }
            _uiState.update {
                it.copy(
                    activeCompt    = compt,
                    currentJobName = "Compt: ${compt?.get_DebugInfos() ?: "?"}",
                )
            }
        }.join()

        viewModelScope.launch(Dispatchers.IO) {
            val colorCount = appDatabase.dao_M03CouleurProduitInfos().getAll().size
            val forceFlag  = M00CentralParametresOfAllApps.get_Default().force_next_start_DeleteInsertAll
            val appType    = M00CentralParametresOfAllApps.get_Default().its_AppType

            if (colorCount == 0 || forceFlag) {
                val nextDo = if (appType == AppType.AllInOne)
                    Do.DeleteAll_To_Let_Ancien_Repositorys_GetAll
                else
                    Do.DeleteInsertAll_Active_Key

                val currentCompt = _uiState.value.activeCompt
                if (currentCompt != null) {
                    val updated = currentCompt.copy(next_start = nextDo)
                    _uiState.update { it.copy(activeCompt = updated) }
                    appDatabase.dao_M9AppCompt().upsert(updated)
                    repo.update_M9AppCompt(updated)
                } else {
                    _uiState.update { it.copy(activeCompt = M09AppCompt(next_start = nextDo)) }
                }
            }
        }.join()

        suspend fun deleteAllLocal(label: String = "Suppression données locales…") {
            setProgress(_uiState.value.progress, label)
            with(appDatabase) {
                dao_M03CouleurProduitInfos().deleteAll()
                dao_M1Produit().deleteAll()
                dao_16CategorieProduit().deleteAll()
                dao_M13TarificationInfos().deleteAll()
                dao_M14VentPeriode().deleteAll()
                dao_M8BonVent().deleteAll()
                dao_M10OperationVentCouleur().deleteAll()
            }
        }
        suspend fun insertSeedAndLightDbs(
            seed: SeedResult,
            applyLightDbFilters: Boolean = false,
        ) {

            val lightDbJob = viewModelScope.launch(Dispatchers.IO) {
                if (isPresenter) return@launch
                setProgress(_uiState.value.progress, "Chargement tarifs…")
                val filteredProductKeys =
                    if (applyLightDbFilters) seed.products.map { it.keyID }.toSet() else null

                val r = Init_LightDataBases.returne_FireBase_LightDataBases(
                    filteredProductKeys = filteredProductKeys,
                )
                _uiState.update { it.copy(lightDataBasesResult = r) }

                with(appDatabase) {
                    dao_M13TarificationInfos().deleteAll()
                    dao_M14VentPeriode().deleteAll()
                    dao_M8BonVent().deleteAll()
                    dao_M10OperationVentCouleur().deleteAll()
                    if (r.m13TarificationInfos.isNotEmpty()) dao_M13TarificationInfos().insertAll(r.m13TarificationInfos)
                    if (r.m14VentPeriode.isNotEmpty())        dao_M14VentPeriode().insertAll(r.m14VentPeriode)
                    if (r.m8BonVent.isNotEmpty())             dao_M8BonVent().insertAll(r.m8BonVent)
                    if (r.m10OperationVentCouleur.isNotEmpty()) dao_M10OperationVentCouleur().insertAll(r.m10OperationVentCouleur)
                    if (r.m2Clients.isNotEmpty()) dao_M2Client().insertAll(r.m2Clients)
                }
            }

            viewModelScope.launch(Dispatchers.IO) {
                setProgress(_uiState.value.progress, "Insertion locale…")
                with(appDatabase) {
                    if (seed.colors.isNotEmpty())     dao_M03CouleurProduitInfos().insertAll(seed.colors)
                    if (seed.products.isNotEmpty())   dao_M1Produit().insertAll(seed.products)
                    if (seed.categories.isNotEmpty()) dao_16CategorieProduit().insertAll(seed.categories)
                }
            }.join()

            lightDbJob.join()
        }

        val branch = _uiState.value.activeCompt?.next_start

        when (branch) {

            Do.DeleteAll_To_Let_Ancien_Repositorys_GetAll -> {
                viewModelScope.launch(Dispatchers.IO) {
                    deleteAllLocal()
                    setProgress(1f, "Suppression terminée ✓")
                }.join()
            }

            Do.DeleteInsertAll_Active_Key -> {
                viewModelScope.launch(Dispatchers.IO) { deleteAllLocal() }.join()
                viewModelScope.launch(Dispatchers.IO) {
                    val result = Empty_App_Initialize_M1_3_16_AllRefs_App4Proto2
                        .getReturn_M1_3_16_AllRefs(
                            context = appContext,
                            on_Progress_Datas = { p -> setProgress(p, "Chargement données…") },
                        )
                    _uiState.update { it.copy(seedResult = result) }
                    insertSeedAndLightDbs(result, applyLightDbFilters = false)
                }.join()
            }

            Do.DeleteInsertAll_Ref_All_Datas -> {
                viewModelScope.launch(Dispatchers.IO) { deleteAllLocal() }.join()
                viewModelScope.launch(Dispatchers.IO) {
                    val result = Empty_App_Initialize_M1_3_16_AllRefs_App4Proto2
                        .getReturn_M1_3_16_AllRefs(
                            context = appContext,
                            on_Progress_Datas = { p -> setProgress(p, "Chargement toutes données ref…") },
                        )
                    _uiState.update { it.copy(seedResult = result) }

                    DropBox_Init.syncAll(result.colors) { p -> setProgress(p, "Sync images…") }

                    insertSeedAndLightDbs(result, applyLightDbFilters = false)
                }.join()
            }

            Do.StandartInit_Sans_RienFair, null -> { }
        }

        setProgress(1f, "Prêt ✓")

        _uiState.value.activeCompt?.let { compt ->
            val updated = compt.copy(next_start = Do.StandartInit_Sans_RienFair)
            _uiState.update { it.copy(activeCompt = updated) }
            appDatabase.dao_M9AppCompt().upsert(updated)
            repo.update_M9AppCompt(updated)
        }

        _uiState.update { it.copy(initDone = true) }

        startPeriodicDataIntegrityCheck()
    }

    private fun startPeriodicDataIntegrityCheck() {
        if (isPresenter) return

        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(5_000L)

                val comptEmpty      = _uiState.value.activeCompt == null ||
                        appDatabase.dao_M9AppCompt().getAll().isEmpty()
                val ventPeriodEmpty = appDatabase.dao_M14VentPeriode().getAll().isEmpty()
                val bonVentEmpty    = appDatabase.dao_M8BonVent().getAll().isEmpty()

                if (!comptEmpty && !ventPeriodEmpty && !bonVentEmpty) continue

                if (comptEmpty) {
                    try {
                        val key  = M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
                        val snap = withTimeoutOrNull(10_000L) { M09AppCompt.ref.get().await() }
                        val compt = snap?.children
                            ?.mapNotNull { child ->
                                try { child.getValue(M09AppCompt::class.java) }
                                catch (_: Exception) { null }
                            }
                            ?.find { it.keyID == key }
                        compt?.let {
                            appDatabase.dao_M9AppCompt().upsert(it)
                            _uiState.update { s -> s.copy(activeCompt = compt) }
                        }
                    } catch (_: Exception) { }
                }

                if (ventPeriodEmpty || bonVentEmpty) {
                    try {
                        val seed = _uiState.value.seedResult
                        val filteredProductKeys =
                            if (seed.products.isNotEmpty()) seed.products.map { it.keyID }.toSet()
                            else null

                        val r = Init_LightDataBases.returne_FireBase_LightDataBases(
                            filteredProductKeys = filteredProductKeys,
                        )
                        _uiState.update { it.copy(lightDataBasesResult = r) }

                        with(appDatabase) {
                            if (ventPeriodEmpty && r.m14VentPeriode.isNotEmpty()) {
                                dao_M14VentPeriode().deleteAll()
                                dao_M14VentPeriode().insertAll(r.m14VentPeriode)
                            }
                            if (bonVentEmpty && r.m8BonVent.isNotEmpty()) {
                                dao_M8BonVent().deleteAll()
                                dao_M8BonVent().insertAll(r.m8BonVent)
                            }
                            if (r.m10OperationVentCouleur.isNotEmpty()) {
                                dao_M10OperationVentCouleur().deleteAll()
                                dao_M10OperationVentCouleur().insertAll(r.m10OperationVentCouleur)
                            }
                            if (r.m13TarificationInfos.isNotEmpty()) {
                                dao_M13TarificationInfos().deleteAll()
                                dao_M13TarificationInfos().insertAll(r.m13TarificationInfos)
                            }
                            if (r.m2Clients.isNotEmpty()) dao_M2Client().insertAll(r.m2Clients)
                        }
                    } catch (_: Exception) { }
                }
            }
        }
    }

    private suspend fun insertMissingEchatillantsProductsAndColors() {
        val echatillantsM8Keys = appDatabase.dao_M8BonVent().getAll()
            .filter { it.parent_M2Client_KeyID == Jomla_Clients.ECHATILLANTS_KEY_ID }
            .map { it.keyID }.toSet()

        if (echatillantsM8Keys.isEmpty()) return

        val echatillantsProductKeys = appDatabase.dao_M10OperationVentCouleur().getAll()
            .filter { it.parent_M8BonVent_KeyId in echatillantsM8Keys }
            .map { it.parent_M1Produit_KeyId }.toSet()

        if (echatillantsProductKeys.isEmpty()) return

        val existingProductKeys = appDatabase.dao_M1Produit().getAll().map { it.keyID }.toSet()
        val missingProductKeys  = echatillantsProductKeys - existingProductKeys

        if (missingProductKeys.isEmpty()) return

        val missingProducts = try {
            M01Produit.ref.get().await().children.mapNotNull { child ->
                val key = child.key ?: return@mapNotNull null
                if (key !in missingProductKeys) return@mapNotNull null
                val p = child.getValue(M01Produit::class.java) ?: return@mapNotNull null
                if (p.keyID.isBlank() || p.keyID != key) p.copy(keyID = key) else p
            }
        } catch (_: Exception) { emptyList() }

        if (missingProducts.isNotEmpty()) appDatabase.dao_M1Produit().insertAll(missingProducts)

        val missingColors = try {
            M3CouleurProduitInfos.ref.get().await().children.mapNotNull { child ->
                val key = child.key ?: return@mapNotNull null
                val c = child.getValue(M3CouleurProduitInfos::class.java) ?: return@mapNotNull null
                val color = if (c.keyID.isBlank() || c.keyID != key) c.copy(keyID = key) else c
                if (color.parentBProduitInfosKeyID in missingProductKeys) color else null
            }
        } catch (_: Exception) { emptyList() }

        if (missingColors.isNotEmpty()) appDatabase.dao_M03CouleurProduitInfos().insertAll(missingColors)
    }

    public override fun onCleared() {
        super.onCleared()
    }
}
