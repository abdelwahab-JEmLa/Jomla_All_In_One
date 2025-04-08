package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.Extension.Log._0_0_HeadOfRepositoryLogOperationsExtension
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchat_Repository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVent_Repository
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur_Repository
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class _0_0_HeadOfRepositorys_RepositoryImpl(
    private val _1_1_Repository: _1_1_CouleurAcheteOperation_Repository,
    private val _1_2_Repository: _1_2_ProduitAcheteOperation_Repository,
    private val _1_3_Repository: _1_3_BonAchat_Repository,
    private val _1_4_Repository: _1_4_PeriodeVent_Repository,
    private val _1_5_Repository: _1_5_Vendeur_Repository
) : _0_0_HeadOfRepositorys_Repository {
    private val TAG = _0_0_HeadOfRepositorys_Repository.TAG

    override var repositorys_Model: _0_0_HeadOfRepositorys_Model = _0_0_HeadOfRepositorys_Model(
        _1_1_Repository,
        _1_2_Repository,
        _1_3_Repository,
        _1_4_Repository,
        _1_5_Repository
    )

    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private var initialDataLoaded = false
    private var lastUpdateTimestamp: Long = 0L
    private var isListenerActive = false
    private var isFlowListenerActive = false

    private val logOperations = _0_0_HeadOfRepositoryLogOperationsExtension(this)

    init {
        repositoryScope.launch {
            initialize_0_0_HeadOfRepositoryRepository()
            startProgressTracking()
        }
    }

    suspend fun ensureDataIsInitialized() {
        try {
            if (!initialDataLoaded) {
                withContext(Dispatchers.IO) {
                    // Wait until data is loaded
                    while (!initialDataLoaded) {
                        delay(100)
                        if (progressRepo.value >= 1.0f) {
                            initialDataLoaded = true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error ensuring data initialization: ${e.message}")
        }
    }

    private suspend fun initialize_0_0_HeadOfRepositoryRepository() {
        try {
            // Initialize all child repositories
            _1_1_Repository.ensureDataIsInitialized()
            _1_2_Repository.ensureDataIsInitialized()
            _1_3_Repository.ensureDataIsInitialized()
            _1_4_Repository.ensureDataIsInitialized()
            _1_5_Repository.ensureDataIsInitialized()

            collectRepositorys()

            if (TAG.isNotEmpty()) {
                log()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing repository: ${e.message}")
        }
    }

    private suspend fun collectRepositorys() {
        try {
            progressRepo.value = 0.2f
            withContext(Dispatchers.IO) {
                // Create a repository head with all repositories
                repositorys_Model = _0_0_HeadOfRepositorys_Model(
                    _1_1_CouleurAcheteOperation_Repository = _1_1_Repository,
                    _1_2_ProduitAcheteOperation_Repository = _1_2_Repository,
                    _1_3_BonAchat_Repository = _1_3_Repository,
                    _1_4_PeriodeVent_Repository = _1_4_Repository,
                    _1_5_Vendeur_Repository = _1_5_Repository
                )

                // Update progress
                progressRepo.value = 1.0f
                initialDataLoaded = true
                lastUpdateTimestamp = System.currentTimeMillis()
            }
        } catch (e: Exception) {
            progressRepo.value = 0f
            Log.e(TAG, "Error collecting repositories: ${e.message}")
        }
    }

    private suspend fun startProgressTracking() {
        isFlowListenerActive = true
        try {
            // Combine all progress flows
            combine(
                _1_1_Repository.progressRepo,
                _1_2_Repository.progressRepo,
                _1_3_Repository.progressRepo,
                _1_4_Repository.progressRepo,
                _1_5_Repository.progressRepo
            ) { progress1, progress2, progress3, progress4, progress5 ->
                // Calculate the average progress
                (progress1 + progress2 + progress3 + progress4 + progress5) / 5f
            }.collect { combinedProgress ->
                progressRepo.value = combinedProgress
                log()
            }
        } catch (e: Exception) {
            isFlowListenerActive = false
            Log.e(TAG, "Error tracking progress: ${e.message}")
        }
    }

    fun log() {
        logOperations.log(
            dataCount = 1, // There's only one model in the repositorys_Model
            initialDataLoaded = initialDataLoaded,
            progressValue = progressRepo.value,
            lastUpdateTimestamp = lastUpdateTimestamp,
            isListenerActive = isListenerActive,
            isFlowListenerActive = isFlowListenerActive
        )
    }
}
