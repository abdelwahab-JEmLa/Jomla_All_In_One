package A_Main.Shared.Proto

import Application4.App.Fragment.ID1.Fragment.ViewModel.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Models.AppType
import EntreApps.Shared.Models.Do
import EntreApps.Shared.Models.Jomla_Clients
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Modules.Base.AppDatabase
import android.content.Context
import android.util.Log
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

private const val VM_TAG = "LoadingInit"

class A_LoadingViewModel(
    private val appDatabase: AppDatabase,
    private val appContext: Context,
) : ViewModel() {

    data class LoadingUiState(
        val initDone: Boolean = false,
        val progress: Float = 0f,
        val currentJobName: String = "",
        val activeCompt: M09AppCompt? = null,
        val seedResult: Empty_App_Initialize_M1_3_16_Filtered_App4Proto2.SeedResult =
            Empty_App_Initialize_M1_3_16_Filtered_App4Proto2.SeedResult(),
        val lightDataBasesResult: Init_LightDataBases.LightDataBasesResult =
            Init_LightDataBases.LightDataBasesResult(),
    )

    private val _uiState = MutableStateFlow(LoadingUiState())
    val uiState = _uiState.asStateFlow()

    private val initMutex = Mutex()
    private var initStarted = false
    private val repo by lazy { RepositorysMainSetter_NewProtoPatterns(appDatabase, appContext) }

    // ── Centralized progress setter – every state change is logged ────────────
    // If you see the ViewModel logging progress=X% but the Screen SideEffect
    // stays at 0%, the StateFlow emission is not reaching the collector
    // (wrong lifecycle owner, collectAsState called before the VM emits, etc.)
    private fun setProgressAndLog(p: Float, job: String) {
        Log.d(VM_TAG, "setProgress  ${(p * 100).toInt()}%  '$job'  thread=${Thread.currentThread().name}")
        _uiState.update { it.copy(progress = p, currentJobName = job) }
    }

    // =========================================================================
    // GATE A – ViewModel instantiated
    // =========================================================================
    // Logged from the viewModelFactory in the Screen.
    // vmHash printed there is System.identityHashCode(this).

    // =========================================================================
    // GATE B – startIfNeeded
    // =========================================================================
    // Called by LaunchedEffect(Unit) in the composable.
    // If you never see GATE B it means LaunchedEffect never fired
    // (composable removed from tree before the coroutine started).
    fun startIfNeeded(context: Context) {
        Log.d(VM_TAG, "GATE B – startIfNeeded  initStarted=$initStarted  vmHash=${System.identityHashCode(this)}")
        viewModelScope.launch {
            initMutex.withLock {
                if (initStarted) {
                    // ── SUSPECT #1 ────────────────────────────────────────────
                    // ViewModel survived recomposition / activity restart.
                    // startIfNeeded was called again but runInit is guarded
                    // → progress stays at whatever value it had, bar appears frozen.
                    Log.w(VM_TAG, "GATE B – BLOCKED: already started  initDone=${_uiState.value.initDone}  progress=${(uiState.value.progress*100).toInt()}%")
                    return@launch
                }
                initStarted = true
                Log.d(VM_TAG, "GATE B – initStarted set TRUE, entering runInit")
            }
            runInit()
        }
    }

    // =========================================================================
    // GATE C – runInit entry
    // =========================================================================
    private suspend fun runInit() {
        Log.d(VM_TAG, "GATE C – runInit BEGIN  vmHash=${System.identityHashCode(this)}")

        // Push 2 % immediately so the screen shows movement right away.
        // If the screen stays at 0 % even after this, the Flow is not reaching
        // the composable collector (check GATE 4 in LoadingScreen logs).
        setProgressAndLog(0.02f, "Démarrage…")

        fun setProgress(p: Float, job: String = _uiState.value.currentJobName) =
            setProgressAndLog(p, job)

        // =====================================================================
        // GATE D – Step 1 : fetch M09AppCompt from Firebase
        // =====================================================================
        // If you see GATE D START but never GATE D END the Firebase call hung.
        // The withTimeoutOrNull(10 s) below converts that into a null + warning.
        Log.d(VM_TAG, "GATE D – Step1 START: fetching M09AppCompt from Firebase")
        viewModelScope.launch(Dispatchers.IO) {
            val key = M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
            Log.d(VM_TAG, "GATE D – target keyID='$key'")

            // ── SUSPECT #2 ────────────────────────────────────────────────────
            // Without a timeout, a captive-portal / offline network lets TCP
            // connect to Firebase but never sends data back.  The coroutine
            // blocks here indefinitely and NOTHING after this line ever runs
            // → loading screen frozen at 2 % forever.
            val snap = withTimeoutOrNull(10_000L) { M09AppCompt.ref.get().await() }

            if (snap == null) {
                Log.e(VM_TAG, "GATE D – SUSPECT #2 CONFIRMED: Firebase timeout after 10 s  →  compt=null")
            } else {
                Log.d(VM_TAG, "GATE D – Firebase replied  childrenCount=${snap.childrenCount}")
            }

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

            Log.d(VM_TAG, "GATE D – resolved compt=${compt?.get_DebugInfos() ?: "null"}")
            compt?.let { appDatabase.dao_M9AppCompt().upsert(it) }
            _uiState.update {
                it.copy(
                    activeCompt    = compt,
                    currentJobName = "Compt: ${compt?.get_DebugInfos() ?: "?"}",
                )
            }
        }.join()
        Log.d(VM_TAG, "GATE D – Step1 END  activeCompt=${_uiState.value.activeCompt?.get_DebugInfos() ?: "null"}")

        // =====================================================================
        // GATE E – Step 2 : decide next_start based on local color count
        // =====================================================================
        Log.d(VM_TAG, "GATE E – Step2 START: checking local color table")
        viewModelScope.launch(Dispatchers.IO) {
            val colorCount = appDatabase.dao_M03CouleurProduitInfos().getAll().size
            val forceFlag  = M00CentralParametresOfAllApps.get_Default().force_next_start_DeleteInsertAll
            val appType    = M00CentralParametresOfAllApps.get_Default().its_AppType
            Log.d(VM_TAG, "GATE E – colorCount=$colorCount  forceFlag=$forceFlag  appType=$appType")

            if (colorCount == 0 || forceFlag) {
                val nextDo = if (appType == AppType.AllInOne)
                    Do.DeleteAll_To_Let_Ancien_Repositorys_GetAll
                else
                    Do.DeleteInsertAll_Active_Key

                Log.d(VM_TAG, "GATE E – forcing next_start=$nextDo")
                val currentCompt = _uiState.value.activeCompt
                if (currentCompt != null) {
                    val updated = currentCompt.copy(next_start = nextDo)
                    _uiState.update { it.copy(activeCompt = updated) }
                    appDatabase.dao_M9AppCompt().upsert(updated)
                    repo.update_M9AppCompt(updated)
                } else {
                    // ── SUSPECT #3 ────────────────────────────────────────────
                    // activeCompt is null (Firebase timed out in GATE D) AND
                    // colors are empty → we create a synthetic compt here.
                    // If this synthetic compt is lost before Step 3 runs, the
                    // branch falls into StandartInit/null and nothing loads.
                    Log.w(VM_TAG, "GATE E – SUSPECT #3: activeCompt null, synthetic compt created with next_start=$nextDo")
                    _uiState.update { it.copy(activeCompt = M09AppCompt(next_start = nextDo)) }
                }
            } else {
                Log.d(VM_TAG, "GATE E – local colors present, no force")
            }
        }.join()
        Log.d(VM_TAG, "GATE E – Step2 END  next_start=${_uiState.value.activeCompt?.next_start}")

        // ── Helpers ───────────────────────────────────────────────────────────

        suspend fun deleteAllLocal(label: String = "Suppression données locales…") {
            setProgress(_uiState.value.progress, label)
            Log.d(VM_TAG, "deleteAllLocal – wiping all tables")
            with(appDatabase) {
                dao_M03CouleurProduitInfos().deleteAll()
                dao_M1Produit().deleteAll()
                dao_16CategorieProduit().deleteAll()
                dao_M13TarificationInfos().deleteAll()
                dao_M14VentPeriode().deleteAll()
                dao_M8BonVent().deleteAll()
                dao_M10OperationVentCouleur().deleteAll()
            }
            Log.d(VM_TAG, "deleteAllLocal – done")
        }

        suspend fun insertSeedAndLightDbs(
            seed: Empty_App_Initialize_M1_3_16_Filtered_App4Proto2.SeedResult,
            applyLightDbFilters: Boolean = false,
        ) {
            val isPresenter = M00CentralParametresOfAllApps.get_Default().its_AppType ==
                    AppType.JomLaElectroLivreurGrossist_PresenterScreen

            // ── GATE F: light-DB job ──────────────────────────────────────────
            val lightDbJob = viewModelScope.launch(Dispatchers.IO) {
                if (isPresenter) {
                    Log.d(VM_TAG, "GATE F – lightDbJob: Presenter mode, skipping")
                    return@launch
                }
                setProgress(_uiState.value.progress, "Chargement tarifs…")
                val filteredProductKeys =
                    if (applyLightDbFilters) seed.products.map { it.keyID }.toSet() else null
                Log.d(VM_TAG, "GATE F – lightDbJob START  filteredKeys=${filteredProductKeys?.size ?: "null(all)"}")

                val r = Init_LightDataBases.returne_FireBase_LightDataBases(
                    filteredProductKeys = filteredProductKeys,
                )
                Log.d(VM_TAG, "GATE F – lightDbJob received  m14=${r.m14VentPeriode.size}  m8=${r.m8BonVent.size}  m10=${r.m10OperationVentCouleur.size}  m13=${r.m13TarificationInfos.size}  clients=${r.m2Clients.size}")
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
                Log.d(VM_TAG, "GATE F – lightDbJob DB insertion done")
            }

            // ── GATE G: seed local insert ─────────────────────────────────────
            viewModelScope.launch(Dispatchers.IO) {
                setProgress(_uiState.value.progress, "Insertion locale…")
                Log.d(VM_TAG, "GATE G – seedJob  colors=${seed.colors.size}  products=${seed.products.size}  categories=${seed.categories.size}")
                with(appDatabase) {
                    if (seed.colors.isNotEmpty())     dao_M03CouleurProduitInfos().insertAll(seed.colors)
                    if (seed.products.isNotEmpty())   dao_M1Produit().insertAll(seed.products)
                    if (seed.categories.isNotEmpty()) dao_16CategorieProduit().insertAll(seed.categories)
                }
                Log.d(VM_TAG, "GATE G – seedJob done")
            }.join()

            Log.d(VM_TAG, "GATE F+G – waiting for lightDbJob to finish")
            lightDbJob.join()
            Log.d(VM_TAG, "GATE F+G – both jobs joined")
        }

        // =====================================================================
        // GATE H – Step 3 : branch on next_start
        // =====================================================================
        val branch = _uiState.value.activeCompt?.next_start
        Log.d(VM_TAG, "GATE H – Step3 branching on next_start=$branch")

        when (branch) {

            Do.DeleteAll_To_Let_Ancien_Repositorys_GetAll -> {
                Log.d(VM_TAG, "GATE H – branch: DeleteAll_To_Let_Ancien_Repositorys_GetAll")
                viewModelScope.launch(Dispatchers.IO) {
                    deleteAllLocal()
                    setProgress(1f, "Suppression terminée ✓")
                }.join()
            }

            Do.DeleteInsertAll_Active_Key -> {
                Log.d(VM_TAG, "GATE H – branch: DeleteInsertAll_Active_Key")
                viewModelScope.launch(Dispatchers.IO) { deleteAllLocal() }.join()
                viewModelScope.launch(Dispatchers.IO) {

                    // ── SUSPECT #4 ────────────────────────────────────────────
                    // isOnline=false → every seedRepo call inside
                    // getReturn_Filtred_For_Presenter_M1_3_16 marks progress=1f
                    // immediately and returns an EMPTY SeedResult.
                    // insertSeedAndLightDbs inserts nothing but initDone is still
                    // set to true → app starts with no data.
                    // On next launch colorCount==0 forces DeleteInsertAll again
                    // → the user keeps seeing the loading screen every time.
                    val online = Empty_App_Initialize_M1_3_16_Filtered_App4Proto2
                        .isInternetAvailable(appContext)
                    Log.d(VM_TAG, "GATE H[DeleteInsertAll_Active_Key] – isOnline=$online")
                    if (!online) Log.w(VM_TAG, "GATE H – SUSPECT #4: offline → seed will return empty, tables won't be populated")

                    val result = Empty_App_Initialize_M1_3_16_Filtered_App4Proto2
                        .getReturn_Filtred_For_Presenter_M1_3_16(
                            context = appContext,
                            on_Progress_Datas = { p -> setProgress(p, "Chargement produits…") },
                        )
                    Log.d(VM_TAG, "GATE H[DeleteInsertAll_Active_Key] – seed done  colors=${result.colors.size}  products=${result.products.size}  categories=${result.categories.size}")

                    _uiState.update { it.copy(seedResult = result) }

                    Log.d(VM_TAG, "GATE H – starting DropBox sync for ${result.colors.size} colors")
                    DropBox_Init.syncAll(result.colors) { p -> setProgress(p, "Sync images…") }
                    Log.d(VM_TAG, "GATE H – DropBox sync done")

                    insertSeedAndLightDbs(result, applyLightDbFilters = true)

                    val isPresenter = M00CentralParametresOfAllApps.get_Default().its_AppType ==
                            AppType.JomLaElectroLivreurGrossist_PresenterScreen
                    if (!isPresenter) {
                        Log.d(VM_TAG, "GATE H – inserting missing echantillants products/colors")
                        insertMissingEchatillantsProductsAndColors()
                        Log.d(VM_TAG, "GATE H – echantillants done")
                    }
                }.join()
            }

            Do.DeleteInsertAll_Ref_All_Datas -> {
                Log.d(VM_TAG, "GATE H – branch: DeleteInsertAll_Ref_All_Datas")
                viewModelScope.launch(Dispatchers.IO) { deleteAllLocal() }.join()
                viewModelScope.launch(Dispatchers.IO) {
                    val online = Empty_App_Initialize_M1_3_16_Filtered_App4Proto2
                        .isInternetAvailable(appContext)
                    Log.d(VM_TAG, "GATE H[DeleteInsertAll_Ref_All_Datas] – isOnline=$online")

                    val result = Empty_App_Initialize_M1_3_16_AllRefs_App4Proto2
                        .getReturn_M1_3_16_AllRefs(
                            context = appContext,
                            on_Progress_Datas = { p -> setProgress(p, "Chargement toutes données ref…") },
                        )
                    Log.d(VM_TAG, "GATE H[DeleteInsertAll_Ref_All_Datas] – done  colors=${result.colors.size}  products=${result.products.size}")
                    _uiState.update { it.copy(seedResult = result) }

                    Log.d(VM_TAG, "GATE H – starting DropBox sync")
                    DropBox_Init.syncAll(result.colors) { p -> setProgress(p, "Sync images…") }
                    Log.d(VM_TAG, "GATE H – DropBox sync done")

                    insertSeedAndLightDbs(result, applyLightDbFilters = false)
                }.join()
            }

            Do.StandartInit_Sans_RienFair, null -> {
                // ── SUSPECT #5 ────────────────────────────────────────────────
                // activeCompt resolved to null (Firebase timed out at GATE D) AND
                // colorCount > 0 (GATE E did not force a nextDo) → we fall here
                // and load nothing.  App starts with potentially stale local data.
                // On the next launch the same path repeats unless Firebase replies.
                Log.w(VM_TAG, "GATE H – SUSPECT #5: StandartInit/null branch, nothing to load  activeCompt=${_uiState.value.activeCompt?.get_DebugInfos() ?: "null"}")
            }
        }

        // =====================================================================
        // GATE I – Step 4 : finalize
        // =====================================================================
        Log.d(VM_TAG, "GATE I – Step4: resetting next_start, setting initDone=true")
        setProgress(1f, "Prêt ✓")

        _uiState.value.activeCompt?.let { compt ->
            val updated = compt.copy(next_start = Do.StandartInit_Sans_RienFair)
            _uiState.update { it.copy(activeCompt = updated) }
            appDatabase.dao_M9AppCompt().upsert(updated)
            repo.update_M9AppCompt(updated)
            Log.d(VM_TAG, "GATE I – next_start reset to StandartInit for compt=${updated.get_DebugInfos()}")
        }

        _uiState.update { it.copy(initDone = true) }
        Log.d(VM_TAG, "GATE I – initDone=true emitted → loading screen should dismiss")
        Log.d(VM_TAG, "GATE C – runInit END")

        // ── Step 5: start periodic data-integrity check (non-Presenter only) ──
        startPeriodicDataIntegrityCheck()
    }

    // =========================================================================
    // GATE J – periodic integrity check
    // =========================================================================
    private fun startPeriodicDataIntegrityCheck() {
        val isPresenter = M00CentralParametresOfAllApps.get_Default().its_AppType ==
                AppType.JomLaElectroLivreurGrossist_PresenterScreen
        if (isPresenter) {
            Log.d(VM_TAG, "GATE J – Presenter mode, periodic check skipped")
            return
        }
        Log.d(VM_TAG, "GATE J – periodic data-integrity check started (every 5 s)")

        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(5_000L)

                val comptEmpty      = _uiState.value.activeCompt == null ||
                        appDatabase.dao_M9AppCompt().getAll().isEmpty()
                val ventPeriodEmpty = appDatabase.dao_M14VentPeriode().getAll().isEmpty()
                val bonVentEmpty    = appDatabase.dao_M8BonVent().getAll().isEmpty()

                if (!comptEmpty && !ventPeriodEmpty && !bonVentEmpty) continue

                Log.w(VM_TAG, "GATE J – empty tables detected: compt=$comptEmpty  ventPeriod=$ventPeriodEmpty  bonVent=$bonVentEmpty  → reloading")

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
                        Log.d(VM_TAG, "GATE J – M09AppCompt reload: ${compt?.get_DebugInfos() ?: "still null"}")
                        compt?.let {
                            appDatabase.dao_M9AppCompt().upsert(it)
                            _uiState.update { s -> s.copy(activeCompt = compt) }
                        }
                    } catch (_: Exception) {
                        Log.e(VM_TAG, "GATE J – exception reloading M09AppCompt")
                    }
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
                        Log.d(VM_TAG, "GATE J – light DB reload  m14=${r.m14VentPeriode.size}  m8=${r.m8BonVent.size}")
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
                        Log.d(VM_TAG, "GATE J – light DB re-inserted into Room")
                    } catch (_: Exception) {
                        Log.e(VM_TAG, "GATE J – exception reloading light databases")
                    }
                }
            }
        }
    }

    private suspend fun insertMissingEchatillantsProductsAndColors() {
        val echatillantsM8Keys = appDatabase.dao_M8BonVent().getAll()
            .filter { it.parent_M2Client_KeyID == Jomla_Clients.ECHATILLANTS_KEY_ID }
            .map { it.keyID }.toSet()

        if (echatillantsM8Keys.isEmpty()) { Log.d(VM_TAG, "echantillants – no M8 keys found, skip"); return }

        val echatillantsProductKeys = appDatabase.dao_M10OperationVentCouleur().getAll()
            .filter { it.parent_M8BonVent_KeyId in echatillantsM8Keys }
            .map { it.parent_M1Produit_KeyId }.toSet()

        if (echatillantsProductKeys.isEmpty()) { Log.d(VM_TAG, "echantillants – no product keys, skip"); return }

        val existingProductKeys = appDatabase.dao_M1Produit().getAll().map { it.keyID }.toSet()
        val missingProductKeys  = echatillantsProductKeys - existingProductKeys
        Log.d(VM_TAG, "echantillants – missingProducts=${missingProductKeys.size}")

        if (missingProductKeys.isEmpty()) return

        val missingProducts = try {
            M01Produit.ref.get().await().children.mapNotNull { child ->
                val key = child.key ?: return@mapNotNull null
                if (key !in missingProductKeys) return@mapNotNull null
                val p = child.getValue(M01Produit::class.java) ?: return@mapNotNull null
                if (p.keyID.isBlank() || p.keyID != key) p.copy(keyID = key) else p
            }
        } catch (_: Exception) { emptyList() }

        Log.d(VM_TAG, "echantillants – inserting ${missingProducts.size} missing products")
        if (missingProducts.isNotEmpty()) appDatabase.dao_M1Produit().insertAll(missingProducts)

        val missingColors = try {
            M3CouleurProduitInfos.ref.get().await().children.mapNotNull { child ->
                val key = child.key ?: return@mapNotNull null
                val c = child.getValue(M3CouleurProduitInfos::class.java) ?: return@mapNotNull null
                val color = if (c.keyID.isBlank() || c.keyID != key) c.copy(keyID = key) else c
                if (color.parentBProduitInfosKeyID in missingProductKeys) color else null
            }
        } catch (_: Exception) { emptyList() }

        Log.d(VM_TAG, "echantillants – inserting ${missingColors.size} missing colors")
        if (missingColors.isNotEmpty()) appDatabase.dao_M03CouleurProduitInfos().insertAll(missingColors)
    }

    public override fun onCleared() {
        Log.d(VM_TAG, "onCleared – ViewModel destroyed  vmHash=${System.identityHashCode(this)}")
        super.onCleared()
    }
}
