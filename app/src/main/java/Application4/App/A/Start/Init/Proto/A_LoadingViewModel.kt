package Application4.App.A.Start.Init.Proto

import EntreApps.Shared.Models.Do
import EntreApps.Shared.Models.Home.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.ifTrue
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Modules.Base.AppDatabase
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// FIX(TODO-1): All init coroutines now run in viewModelScope (not a LaunchedEffect scope).
// viewModelScope is tied to the ViewModel lifecycle, NOT the composition, so it is never
// cancelled by recomposition or navigation — eliminating LeftCompositionCancellationException.
//
// FIX(FIXME): RepositorysMainSetter_NewProtoPatterns is constructed once here using the
// appDatabase and context that are injected by Koin, instead of being re-created inline
// in the composable.

class A_LoadingViewModel(
    private val appDatabase: AppDatabase,
    // Application context — safe to hold in a ViewModel (not an Activity context).
    private val appContext: Context,
) : ViewModel() {
    data class LoadingUiState(
        val initDone: Boolean = false,
        val progress: Float = 0f,
        val currentJobName: String = "",
        val activeCompt: Z_AppCompt? = null,
        val seedResult: Empty_App_Initialize_M1_3_16_App4Proto2.SeedResult =
            Empty_App_Initialize_M1_3_16_App4Proto2.SeedResult(),
        val lightDataBasesResult: Init_LightDataBases.LightDataBasesResult =
            Init_LightDataBases.LightDataBasesResult(),
    )

    private val _uiState = MutableStateFlow(LoadingUiState())
    val uiState = _uiState.asStateFlow()

    // Guards against running init more than once (e.g. if startIfNeeded is called on every
    // recomposition via LaunchedEffect(Unit)).
    private val initMutex = Mutex()
    private var initStarted = false

    // FIX(FIXME): single Repo instance reused for the whole init sequence.
    private val repo by lazy { RepositorysMainSetter_NewProtoPatterns(appDatabase, appContext) }

    fun startIfNeeded(context: Context) {
        // context param kept for API compatibility; we use appContext (injected) internally.
        viewModelScope.launch {
            initMutex.withLock {
                if (initStarted) return@launch
                initStarted = true
            }
            runInit()
        }
    }

    private suspend fun runInit() {
        fun setProgress(p: Float, job: String = _uiState.value.currentJobName) {
            _uiState.update { it.copy(progress = p, currentJobName = job) }
        }

        // ── Step 1: load active account ──────────────────────────────────────────────
        val activeComptJob = viewModelScope.launch(Dispatchers.IO) {
            val key = M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
            val compt = Z_AppCompt.ref.get()
                // FIX: use suspending await() instead of a blocking call
                .also { task -> await(task) }
                .let { (it as com.google.android.gms.tasks.Task<*>).result }
                .let { snap ->
                    @Suppress("UNCHECKED_CAST")
                    (snap as com.google.firebase.database.DataSnapshot)
                        .children.mapNotNull { it.getValue(Z_AppCompt::class.java) }
                        .find { it.keyID == key }
                }
            _uiState.update { it.copy(activeCompt = compt, currentJobName = "Compt: ${compt?.get_DebugInfos() ?: "?"}") }
        }
        activeComptJob.join()

        // ── Step 2: check if local colour DB is empty → force full re-sync ───────────
        // FIX(1-top): this block is the intended "check empty → set DeleteInsertAll" logic,
        // now safely running in viewModelScope instead of a composable LaunchedEffect.
        val checkEmptyJob = viewModelScope.launch(Dispatchers.IO) {
            val isColorsEmpty = appDatabase.dao_M03CouleurProduitInfos().getAll().isEmpty()
            if (isColorsEmpty) {
                _uiState.value.activeCompt?.let { compt ->
                    val updated = compt.copy(next_start = Do.DeleteInsertAll_Active_Key)
                    _uiState.update { it.copy(activeCompt = updated) }
                    repo.update_M9AppCompt(updated)
                    Log.d("LoadingScreen", "dao_M03CouleurProduitInfos is empty — next_start set to DeleteInsertAll_Active_Key")
                }
            }
        }
        checkEmptyJob.join()

        // ── Step 3: full re-seed (only when flagged) ─────────────────────────────────
        (_uiState.value.activeCompt?.next_start == Do.DeleteInsertAll_Active_Key).ifTrue {

            val deleteJob = viewModelScope.launch(Dispatchers.IO) {
                setProgress(_uiState.value.progress, "Suppression données locales…")
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
            deleteJob.join()

            // FIX(TODO-1 core): getReturne_M1_3_16 now runs in viewModelScope (IO dispatcher).
            // It will never receive a LeftCompositionCancellationException because viewModelScope
            // is decoupled from the composable tree.
            val seedJob = viewModelScope.launch(Dispatchers.IO) {
                val result = Empty_App_Initialize_M1_3_16_App4Proto2.getReturne_M1_3_16(
                    context = appContext,
                    on_Progress_Datas = { p -> setProgress(p, "Chargement produits…") },
                )
                _uiState.update { it.copy(seedResult = result) }
                Log.d("LoadingScreen", "SeedResult updated — " +
                        "products=${result.products.size} " +
                        "colors=${result.colors.size} " +
                        "categories=${result.categories.size} " +
                        "filterKeys=${result.filterKeys.size}")
                if (result.products.isEmpty() && result.colors.isNotEmpty()) {
                    Log.w("LoadingScreen", "⚠️ products==0 but colors=${result.colors.size} — " +
                            "check parentBProduitInfosKeyID mapping in Empty_App_Initialize logs")
                }

                // Sync Dropbox images after seed, within the same job so insert waits for it.
                DropBox_Init.syncAll(result.colors) { p -> setProgress(p, "Sync images…") }
            }
            seedJob.join()

            val currentSeed = _uiState.value.seedResult

            val lightDbJob = viewModelScope.launch(Dispatchers.IO) {
                setProgress(_uiState.value.progress, "Chargement tarifs…")
                val r = Init_LightDataBases.returne_FireBase_LightDataBases()
                _uiState.update { it.copy(lightDataBasesResult = r) }
                with(appDatabase) {
                    // FIX(UNIQUE-constraint): delete before re-inserting.
                    // If the app crashed mid-init on a previous run, next_start was never reset
                    // to StandartInit, so the delete block at the top of DeleteInsertAll runs
                    // again — but Room's @Insert (without onConflict=REPLACE) will crash on any
                    // row that Room flushed to disk before the crash.
                    // Deleting here, immediately before the insert, closes that window entirely.
                    dao_M13TarificationInfos().deleteAll()
                    dao_M14VentPeriode().deleteAll()
                    dao_M8BonVent().deleteAll()
                    dao_M10OperationVentCouleur().deleteAll()
                    if (r.m13TarificationInfos.isNotEmpty()) dao_M13TarificationInfos().insertAll(r.m13TarificationInfos)
                    if (r.m14VentPeriode.isNotEmpty())        dao_M14VentPeriode().insertAll(r.m14VentPeriode)
                    if (r.m8BonVent.isNotEmpty())             dao_M8BonVent().insertAll(r.m8BonVent)
                    if (r.m10OperationVentCouleur.isNotEmpty()) dao_M10OperationVentCouleur().insertAll(r.m10OperationVentCouleur)
                }
            }
            val insertJob = viewModelScope.launch(Dispatchers.IO) {
                setProgress(_uiState.value.progress, "Insertion locale…")
                with(appDatabase) {
                    if (currentSeed.colors.isNotEmpty())     dao_M03CouleurProduitInfos().insertAll(currentSeed.colors)
                    if (currentSeed.products.isNotEmpty())   dao_M1Produit().insertAll(currentSeed.products)
                    if (currentSeed.categories.isNotEmpty()) dao_16CategorieProduit().insertAll(currentSeed.categories)
                }
            }
            insertJob.join()
            lightDbJob.join()
        }

        setProgress(1f, "Prêt ✓")

        // FIX(1-bottom): reset next_start to StandartInit after successful init.
        _uiState.value.activeCompt?.let { compt ->
            val updated = compt.copy(next_start = Do.StandartInit)
            _uiState.update { it.copy(activeCompt = updated) }
            repo.update_M9AppCompt(updated)
            Log.d("LoadingScreen", "Init complete — next_start reset to StandartInit")
        }

        _uiState.update { it.copy(initDone = true) }
    }
    public override fun onCleared() {
        super.onCleared()
    }
}
