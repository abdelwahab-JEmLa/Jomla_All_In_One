package Application4.App.A.Start.Init.Proto

import EntreApps.Shared.Models.Components.AppType
import EntreApps.Shared.Models.Do
import EntreApps.Shared.Models.Home.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Modules.Base.AppDatabase
import android.content.Context
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
        val activeCompt: Z_AppCompt? = null,
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
            val snap = Z_AppCompt.ref.get().await()
            val compt = snap.children
                .mapNotNull { child ->
                    try {
                        child.getValue(Z_AppCompt::class.java)
                    } catch (_: Exception) {
                        @Suppress("UNCHECKED_CAST")
                        val raw = child.getValue(Object::class.java) as? Map<String, Any?>
                            ?: return@mapNotNull null
                        val rawDo = raw["next_start"] as? String
                        val safeDo = Do.entries.firstOrNull { it.name == rawDo }
                            ?: Do.StandartInit_Sans_RienFair
                        try {
                            Z_AppCompt(
                                keyID      = raw["keyID"] as? String ?: return@mapNotNull null,
                                nom        = raw["nom"]   as? String ?: "",
                                next_start = safeDo,
                            )
                        } catch (_: Exception) { null }
                    }
                }
                .find { it.keyID == key }

            _uiState.update {
                it.copy(
                    activeCompt = compt,
                    currentJobName = "Compt: ${compt?.get_DebugInfos() ?: "?"}"
                )
            }
        }.join()

        viewModelScope.launch(Dispatchers.IO) {
            if (appDatabase.dao_M03CouleurProduitInfos().getAll().isEmpty()) {
                _uiState.value.activeCompt?.let { compt ->
                    val updated = compt.copy(
                        next_start =
                            if (M00CentralParametresOfAllApps.get_Default().its_AppType == AppType.AllInOne)
                                Do.DeleteAll_To_Let_Ancien_Repositorys_GetAll
                            else Do.DeleteInsertAll_Active_Key
                    )
                    _uiState.update { it.copy(activeCompt = updated) }
                    repo.update_M9AppCompt(updated)
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

        suspend fun insertSeedAndLightDbs(seed: Empty_App_Initialize_M1_3_16_App4Proto2.SeedResult) {
            val lightDbJob = viewModelScope.launch(Dispatchers.IO) {
                setProgress(_uiState.value.progress, "Chargement tarifs…")
                val r = Init_LightDataBases.returne_FireBase_LightDataBases()
                _uiState.update { it.copy(lightDataBasesResult = r) }
                with(appDatabase) {
                    dao_M13TarificationInfos().deleteAll()
                    dao_M14VentPeriode().deleteAll()
                    dao_M8BonVent().deleteAll()
                    dao_M10OperationVentCouleur().deleteAll()
                    if (r.m13TarificationInfos.isNotEmpty()) dao_M13TarificationInfos().insertAll(r.m13TarificationInfos)
                    if (r.m14VentPeriode.isNotEmpty()) dao_M14VentPeriode().insertAll(r.m14VentPeriode)
                    if (r.m8BonVent.isNotEmpty()) dao_M8BonVent().insertAll(r.m8BonVent)
                    if (r.m10OperationVentCouleur.isNotEmpty()) dao_M10OperationVentCouleur().insertAll(r.m10OperationVentCouleur)
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
                    val result = Empty_App_Initialize_M1_3_16_App4Proto2.getReturne_M1_3_16(
                        context = appContext,
                        on_Progress_Datas = { p -> setProgress(p, "Chargement produits…") },
                    )
                    _uiState.update { it.copy(seedResult = result) }
                    DropBox_Init.syncAll(result.colors) { p -> setProgress(p, "Sync images…") }
                    insertSeedAndLightDbs(result)
                }.join()
            }

            Do.DeleteInsertAll_Ref_All_Datas -> {
                viewModelScope.launch(Dispatchers.IO) { deleteAllLocal() }.join()
                viewModelScope.launch(Dispatchers.IO) {
                    val result = Empty_App_Initialize_M1_3_16_App4Proto2.getReturne_M1_3_16_AllRefs(
                        context = appContext,
                        on_Progress_Datas = { p -> setProgress(p, "Chargement toutes données ref…") },
                    )
                    _uiState.update { it.copy(seedResult = result) }
                    DropBox_Init.syncAll(result.colors) { p -> setProgress(p, "Sync images…") }
                    insertSeedAndLightDbs(result)
                }.join()
            }

            // StandartInit or null (incl. stale enum fallback): skip everything.
            Do.StandartInit_Sans_RienFair, null -> { /* nothing */ }
        }

        setProgress(1f, "Prêt ✓")

        _uiState.value.activeCompt?.let { compt ->
            val updated = compt.copy(next_start = Do.StandartInit_Sans_RienFair)
            _uiState.update { it.copy(activeCompt = updated) }
            repo.update_M9AppCompt(updated)
        }

        _uiState.update { it.copy(initDone = true) }
    }

    public override fun onCleared() {
        super.onCleared()
    }
}
