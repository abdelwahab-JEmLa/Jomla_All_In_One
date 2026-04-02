package A_Main.Shared.Proto

import Application4.App.Fragment.ID1.Fragment.ViewModel.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Models.AppType
import EntreApps.Shared.Models.Do
import EntreApps.Shared.Models.Jomla_Clients
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Modules.Base.AppDatabase
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

class A_LoadingViewModel(
    private val appDatabase: AppDatabase,
    private val appContext: Context,
) : ViewModel() {

    data class LoadingUiState(
        val initDone: Boolean = false,
        val progress: Float = 0f,
        val currentJobName: String = "",
        val activeCompt: M09AppCompt? = null,
        val seedResult: Empty_App_Initialize_M1_3_16_App4Proto2.SeedResult =
            Empty_App_Initialize_M1_3_16_App4Proto2.SeedResult(),
        val lightDataBasesResult: Init_LightDataBases.LightDataBasesResult =
            Init_LightDataBases.LightDataBasesResult(),
    )

    private val _uiState = MutableStateFlow(LoadingUiState())
    val uiState = _uiState.asStateFlow()

    private val initMutex = Mutex()
    private var initStarted = false
    private val repo by lazy { RepositorysMainSetter_NewProtoPatterns(appDatabase, appContext) }

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
            _uiState.update { it.copy(progress = p, currentJobName = job) }

        viewModelScope.launch(Dispatchers.IO) {
            val key = M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
            val snap = M09AppCompt.ref.get().await()
            val compt = snap.children
                .mapNotNull { child ->
                    try {
                        child.getValue(M09AppCompt::class.java)
                    } catch (_: Exception) {
                        @Suppress("UNCHECKED_CAST")
                        val raw = child.getValue(Object::class.java) as? Map<String, Any?>
                            ?: return@mapNotNull null
                        val rawDo = raw["next_start"] as? String
                        val safeDo = Do.entries.firstOrNull { it.name == rawDo }
                            ?: Do.StandartInit_Sans_RienFair
                        try {
                            M09AppCompt(
                                keyID = raw["keyID"] as? String ?: return@mapNotNull null,
                                nom = raw["nom"] as? String ?: "",
                                next_start = safeDo,
                            )
                        } catch (_: Exception) {
                            null
                        }
                    }
                }
                .find { it.keyID == key }

            compt?.let { appDatabase.dao_M9AppCompt().upsert(it) }

            _uiState.update {
                it.copy(
                    activeCompt = compt,
                    currentJobName = "Compt: ${compt?.get_DebugInfos() ?: "?"}"
                )
            }
        }.join()

        viewModelScope.launch(Dispatchers.IO) {
            if (appDatabase.dao_M03CouleurProduitInfos().getAll().isEmpty()
                || M00CentralParametresOfAllApps.get_Default().force_next_start_DeleteInsertAll
            ) {
                val nextDo =
                    if (M00CentralParametresOfAllApps.get_Default().its_AppType == AppType.AllInOne)
                        Do.DeleteAll_To_Let_Ancien_Repositorys_GetAll
                    else Do.DeleteInsertAll_Active_Key

                val currentCompt = _uiState.value.activeCompt
                if (currentCompt != null) {
                    val updated = currentCompt.copy(next_start = nextDo)
                    _uiState.update { it.copy(activeCompt = updated) }
                    appDatabase.dao_M9AppCompt().upsert(updated)
                    repo.update_M9AppCompt(updated)
                } else {
                    // ✅ FIX : aucun M09AppCompt trouvé en Firebase (cas Presenter typique).
                    // On force quand même le chargement avec un compt temporaire en mémoire.
                    Log.w("A_LoadingViewModel",
                        "activeCompt null — DB vide ou force flag actif, forcing $nextDo")
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

        /**
         * Inserts [seed] data locally and fetches/stores the light databases.
         *
         * @param seed             The seeded colors/products/categories to insert.
         * @param applyLightDbFilters When true (i.e. [Do.DeleteInsertAll_Active_Key] mode),
         *   the light-database fetch is scoped to the seeded product keys so that only
         *   relevant m14/m8/m10/m13 records are downloaded and stored locally.
         *   When false (AllRefs mode) everything is fetched unfiltered.
         *
         * NOTE: In [AppType.JomLaElectroLivreurGrossist_PresenterScreen] mode the entire
         *   light-database step (m13/m14/m8/m10) is skipped — the presenter screen only
         *   needs colors and products, not pricing or sales data.
         */
        suspend fun insertSeedAndLightDbs(
            seed: Empty_App_Initialize_M1_3_16_App4Proto2.SeedResult,
            applyLightDbFilters: Boolean = false,
        ) {
            val isPresenter = M00CentralParametresOfAllApps.get_Default().its_AppType ==
                    AppType.JomLaElectroLivreurGrossist_PresenterScreen

            val lightDbJob = viewModelScope.launch(Dispatchers.IO) {
                if (isPresenter) {
                    Log.d(
                        "A_LoadingViewModel",
                        "insertSeedAndLightDbs: Presenter mode — skipping light-database fetch " +
                                "(m13/m14/m8/m10 are unused in the presenter screen)"
                    )
                    return@launch
                }
                setProgress(_uiState.value.progress, "Chargement tarifs…")
                val filteredProductKeys = if (applyLightDbFilters)
                    seed.products.map { it.keyID }.toSet()
                else
                    null
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
                    if (r.m14VentPeriode.isNotEmpty()) dao_M14VentPeriode().insertAll(r.m14VentPeriode)
                    if (r.m8BonVent.isNotEmpty()) dao_M8BonVent().insertAll(r.m8BonVent)
                    if (r.m10OperationVentCouleur.isNotEmpty()) dao_M10OperationVentCouleur().insertAll(
                        r.m10OperationVentCouleur
                    )
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                setProgress(_uiState.value.progress, "Insertion locale…")
                with(appDatabase) {
                    if (seed.colors.isNotEmpty()) dao_M03CouleurProduitInfos().insertAll(seed.colors)
                    if (seed.products.isNotEmpty()) dao_M1Produit().insertAll(seed.products)
                    if (seed.categories.isNotEmpty()) dao_16CategorieProduit().insertAll(seed.categories)
                }
            }.join()
            lightDbJob.join()
        }

        when (_uiState.value.activeCompt?.next_start) {

            Do.DeleteAll_To_Let_Ancien_Repositorys_GetAll -> {
                viewModelScope.launch(Dispatchers.IO) {
                    deleteAllLocal()
                    setProgress(1f, "Suppression terminée ✓")
                }.join()
            }

            Do.DeleteInsertAll_Active_Key -> {
                viewModelScope.launch(Dispatchers.IO) { deleteAllLocal() }.join()
                viewModelScope.launch(Dispatchers.IO) {
                    val result = Empty_App_Initialize_M1_3_16_App4Proto2.getReturn_Filtred_For_Presenter_M1_3_16(
                        context = appContext,
                        on_Progress_Datas = { p -> setProgress(p, "Chargement produits…") },
                    )
                    _uiState.update { it.copy(seedResult = result) }
                    DropBox_Init.syncAll(result.colors) { p -> setProgress(p, "Sync images…") }
                    // Active-key mode: apply light-db filters scoped to the seeded products
                    insertSeedAndLightDbs(result, applyLightDbFilters = true)

                    // Fetch and insert any Échatillants products/colors not covered by active ref keys.
                    // Skipped in presenter mode: échantillon colors are excluded at seed level
                    // (filtered by its_in_echantiallants) so there is nothing missing to backfill.
                    val isPresenter = M00CentralParametresOfAllApps.get_Default().its_AppType ==
                            AppType.JomLaElectroLivreurGrossist_PresenterScreen
                    if (isPresenter) {
                        Log.d(
                            "A_LoadingViewModel",
                            "DeleteInsertAll_Active_Key: Presenter mode — skipping " +
                                    "insertMissingEchatillantsProductsAndColors (échantillons are " +
                                    "excluded from the seed by its_in_echantiallants filter)"
                        )
                    } else {
                        insertMissingEchatillantsProductsAndColors()
                    }
                }.join()
            }

            Do.DeleteInsertAll_Ref_All_Datas -> {
                viewModelScope.launch(Dispatchers.IO) { deleteAllLocal() }.join()
                viewModelScope.launch(Dispatchers.IO) {
                    val result = Empty_App_Initialize_M1_3_16_App4Proto2.getReturne_M1_3_16_AllRefs(
                        context = appContext,
                        on_Progress_Datas = { p ->
                            setProgress(p, "Chargement toutes données ref…")
                        },
                    )
                    _uiState.update { it.copy(seedResult = result) }
                    DropBox_Init.syncAll(result.colors) { p -> setProgress(p, "Sync images…") }
                    // AllRefs mode: no filtering — fetch everything
                    insertSeedAndLightDbs(result, applyLightDbFilters = false)
                }.join()
            }

            Do.StandartInit_Sans_RienFair, null -> { /* nothing */ }
        }

        setProgress(1f, "Prêt ✓")

        _uiState.value.activeCompt?.let { compt ->
            val updated = compt.copy(next_start = Do.StandartInit_Sans_RienFair)
            _uiState.update { it.copy(activeCompt = updated) }
            appDatabase.dao_M9AppCompt().upsert(updated)
            repo.update_M9AppCompt(updated)
        }

        _uiState.update { it.copy(initDone = true) }
    }

    private suspend fun insertMissingEchatillantsProductsAndColors() {
        // 1. Identify M8 bons belonging to the Echatillants client
        val echatillantsM8Keys = appDatabase.dao_M8BonVent().getAll()
            .filter { it.parent_M2Client_KeyID == Jomla_Clients.ECHATILLANTS_KEY_ID }
            .map { it.keyID }
            .toSet()

        if (echatillantsM8Keys.isEmpty()) {
            Log.d("A_LoadingViewModel", "insertMissingEchatillants: no Echatillants bons found — skipping")
            return
        }

        // 2. Collect product keys referenced by Echatillants ops
        val echatillantsProductKeys = appDatabase.dao_M10OperationVentCouleur().getAll()
            .filter { it.parent_M8BonVent_KeyId in echatillantsM8Keys }
            .map { it.parent_M1Produit_KeyId }
            .toSet()

        if (echatillantsProductKeys.isEmpty()) return

        // 3. Keep only the ones not already in Room
        val existingProductKeys = appDatabase.dao_M1Produit().getAll().map { it.keyID }.toSet()
        val missingProductKeys  = echatillantsProductKeys - existingProductKeys

        Log.d("A_LoadingViewModel",
            "insertMissingEchatillants: echatillants products=${echatillantsProductKeys.size} " +
                    "existing=${existingProductKeys.size} missing=${missingProductKeys.size}: $missingProductKeys")

        if (missingProductKeys.isEmpty()) return

        // 4. Fetch missing products from Firebase
        val missingProducts = try {
            M01Produit.ref.get().await().children.mapNotNull { child ->
                val key = child.key ?: return@mapNotNull null
                if (key !in missingProductKeys) return@mapNotNull null
                val p = child.getValue(M01Produit::class.java) ?: return@mapNotNull null
                if (p.keyID.isBlank() || p.keyID != key) p.copy(keyID = key) else p
            }
        } catch (e: Exception) {
            Log.e("A_LoadingViewModel", "insertMissingEchatillants: M1 fetch failed — ${e.message}")
            emptyList()
        }

        if (missingProducts.isNotEmpty()) {
            appDatabase.dao_M1Produit().insertAll(missingProducts)
            Log.d("A_LoadingViewModel", "insertMissingEchatillants: inserted ${missingProducts.size} products")
        }

        // 5. Fetch colors for those products from Firebase
        val missingColors = try {
            M3CouleurProduitInfos.ref.get().await().children.mapNotNull { child ->
                val key = child.key ?: return@mapNotNull null
                val c = child.getValue(M3CouleurProduitInfos::class.java) ?: return@mapNotNull null
                val color = if (c.keyID.isBlank() || c.keyID != key) c.copy(keyID = key) else c
                if (color.parentBProduitInfosKeyID in missingProductKeys) color else null
            }
        } catch (e: Exception) {
            Log.e("A_LoadingViewModel", "insertMissingEchatillants: M3 fetch failed — ${e.message}")
            emptyList()
        }

        if (missingColors.isNotEmpty()) {
            appDatabase.dao_M03CouleurProduitInfos().insertAll(missingColors)
            Log.d("A_LoadingViewModel", "insertMissingEchatillants: inserted ${missingColors.size} colors")
        }
    }

    public override fun onCleared() {
        super.onCleared()
    }
}
