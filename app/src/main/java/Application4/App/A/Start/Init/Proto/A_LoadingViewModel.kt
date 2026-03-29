package Application4.App.A.Start.Init.Proto

import EntreApps.Shared.Models.Do
import EntreApps.Shared.Models.Home.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.ifTrue
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Modules.Base.AppDatabase
import android.content.Context
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
            val compt = Z_AppCompt.ref.get()
                .also { task -> await(task) }
                .let { (it as com.google.android.gms.tasks.Task<*>).result }
                .let { snap ->
                    @Suppress("UNCHECKED_CAST")
                    (snap as com.google.firebase.database.DataSnapshot)
                        .children.mapNotNull { it.getValue(Z_AppCompt::class.java) }
                        .find { it.keyID == key }
                }
            _uiState.update { it.copy(activeCompt = compt, currentJobName = "Compt: ${compt?.get_DebugInfos() ?: "?"}") }
        }.join()

        viewModelScope.launch(Dispatchers.IO) {
            if (appDatabase.dao_M03CouleurProduitInfos().getAll().isEmpty()) {
                _uiState.value.activeCompt?.let { compt ->
                    val updated = compt.copy(next_start = Do.DeleteInsertAll_Active_Key)
                    _uiState.update { it.copy(activeCompt = updated) }
                    repo.update_M9AppCompt(updated)
                }
            }
        }.join()

        (_uiState.value.activeCompt?.next_start == Do.DeleteInsertAll_Active_Key).ifTrue {

            viewModelScope.launch(Dispatchers.IO) {
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
            }.join()

            viewModelScope.launch(Dispatchers.IO) {
                val result = Empty_App_Initialize_M1_3_16_App4Proto2.getReturne_M1_3_16(
                    context = appContext,
                    on_Progress_Datas = { p -> setProgress(p, "Chargement produits…") },
                )
                _uiState.update { it.copy(seedResult = result) }
                DropBox_Init.syncAll(result.colors) { p -> setProgress(p, "Sync images…") }
            }.join()

            val currentSeed = _uiState.value.seedResult

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
                    if (r.m14VentPeriode.isNotEmpty())        dao_M14VentPeriode().insertAll(r.m14VentPeriode)
                    if (r.m8BonVent.isNotEmpty())             dao_M8BonVent().insertAll(r.m8BonVent)
                    if (r.m10OperationVentCouleur.isNotEmpty()) dao_M10OperationVentCouleur().insertAll(r.m10OperationVentCouleur)
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                setProgress(_uiState.value.progress, "Insertion locale…")
                with(appDatabase) {
                    if (currentSeed.colors.isNotEmpty())     dao_M03CouleurProduitInfos().insertAll(currentSeed.colors)
                    if (currentSeed.products.isNotEmpty())   dao_M1Produit().insertAll(currentSeed.products)
                    if (currentSeed.categories.isNotEmpty()) dao_16CategorieProduit().insertAll(currentSeed.categories)
                }
            }.join()
            lightDbJob.join()
        }

        setProgress(1f, "Prêt ✓")

        _uiState.value.activeCompt?.let { compt ->
            val updated = compt.copy(next_start = Do.StandartInit)
            _uiState.update { it.copy(activeCompt = updated) }
            repo.update_M9AppCompt(updated)
        }

        _uiState.update { it.copy(initDone = true) }
    }
    public override fun onCleared() {
        super.onCleared()
    }
}
