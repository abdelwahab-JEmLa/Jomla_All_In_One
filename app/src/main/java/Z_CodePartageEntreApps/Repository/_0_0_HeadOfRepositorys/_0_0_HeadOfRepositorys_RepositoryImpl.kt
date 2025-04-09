package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys

import Z_CodePartageEntreApps.Model._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.Extension.Log._0_0_HeadOfRepositoryLogOperationsExtension
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchat_Repository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVent_Repository
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur_Repository
import Z_CodePartageEntreApps.Repository._2_2_ClientsDataBase._2_2_ClientsDataBase_Repository
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * way inject
 *
 *     ,
 *     _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository = koinInject()
 */
class _0_0_HeadOfRepositorys_RepositoryImpl(
    private val _1_1_Repository: _1_1_CouleurAcheteOperation_Repository,
    private val _1_2_Repository: _1_2_ProduitAcheteOperation_Repository,
    private val _1_3_Repository: _1_3_BonAchat_Repository,
    private val _1_4_Repository: _1_4_PeriodeVent_Repository,
    private val _1_5_Repository: _1_5_Vendeur_Repository,
    private val _2_2_Repository: _2_2_ClientsDataBase_Repository,
) : _0_0_HeadOfRepositorys_Repository {
    private val TAG = _0_0_HeadOfRepositorys_Repository.TAG

    override var repositorys_Model: _0_0_HeadOfRepositorys_Model = _0_0_HeadOfRepositorys_Model(
        _1_1_Repository,
        _1_2_Repository,
        _1_3_Repository,
        _1_4_Repository,
        _1_5_Repository,
        _2_2_Repository,
        activeVID_1_3_BonAchat = 0L, // Initial value, will be updated later
    )
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private var initialDataLoaded = false
    private var lastUpdateTimestamp: Long = 0L
    private var isListenerActive = false
    private var isFlowListenerActive = false

    private val logOperations = _0_0_HeadOfRepositoryLogOperationsExtension(this)

    // In the head repository's init block
    init {
        repositoryScope.launch {
            initialize_0_0_HeadOfRepositoryRepository()

            // Ensure all child repositories are initialized
            _1_1_Repository.ensureDataIsInitialized()
            _1_2_Repository.ensureDataIsInitialized()
            _1_3_Repository.ensureDataIsInitialized()
            _1_4_Repository.ensureDataIsInitialized()
            _1_5_Repository.ensureDataIsInitialized()
            _2_2_Repository.ensureDataIsInitialized()

            // Start tracking progress afterward
            startProgressTracking() {
                checkADD_1_5_Repository()
                checkADD_1_4_PeriodeVent()
            }
        }
    }

    private suspend fun createInitialData() {
        withContext(Dispatchers.IO) {
            try {
                // Ensure we create data in the correct order (parent repositories first)
                checkADD_1_5_Repository()
                delay(100) // Give a small delay to ensure Firebase operations complete

                checkADD_1_4_PeriodeVent()
                delay(100)


                Log.d(TAG, "Initial data creation completed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error creating initial data: ${e.message}")
            }
        }
    }

    private fun checkADD_1_5_Repository() {
        try {
            // Implement the checkADD_1_5_Repository functionality
            val modelDatasSnapList = _1_5_Repository.modelDatasSnapList
            val existingVendor = modelDatasSnapList.find { it.deviceModelNom == Build.MODEL }

            val newVendorPair = if (existingVendor != null) {
                Pair(existingVendor, existingVendor.vid)
            } else {
                val newVid = modelDatasSnapList.maxOfOrNull { it.vid }?.plus(1) ?: 1L
                val newVendor = _1_5_Vendeur(
                    vid = newVid,
                    deviceModelNom = Build.MODEL,
                    nom = "Manager Vendor"
                )
                Pair(newVendor, newVid)
            }

            // Check if the vendor exists and add if not
            if (!modelDatasSnapList.any { it.vid == newVendorPair.second }) {
                _1_5_Repository.addData(newVendorPair.first)
                _1_5_Repository.activeId.value = newVendorPair.second
                Log.d(TAG, "Added new vendor with VID: ${newVendorPair.second}")
            } else {
                _1_5_Repository.activeId.value = newVendorPair.second
                Log.d(TAG, "Using existing vendor with VID: ${newVendorPair.second}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in checkADD_1_5_Repository: ${e.message}")
        }
    }

    private fun checkADD_1_4_PeriodeVent() {
        try {
            // Get the model data list from the repository
            val modelDatasSnapList = _1_4_Repository.modelDatasSnapList

            // Find an existing active period (where endDateInString is empty)
            val existingPeriod = modelDatasSnapList.find {
                it.endDateInString == "" }

            val newPeriodPair = if (existingPeriod != null) {
                Pair(existingPeriod, existingPeriod.vid)
            } else {
                val newVid = modelDatasSnapList.maxOfOrNull { it.vid }?.plus(1) ?: 1L
                val newPeriod = _1_4_PeriodeVent(
                    vid = newVid,
                    vendeur_ParentVID = _1_5_Repository.activeId.value
                )
                Pair(newPeriod, newVid)
            }

            repositorys_Model.activeVidRepository_1_4 = newPeriodPair.second

            // Check if the period exists and add if not
            if (!modelDatasSnapList.any { it.vid == newPeriodPair.second }) {
                _1_4_Repository.addData(newPeriodPair.first)
                _1_4_Repository.activeId.value = newPeriodPair.second
            } else {
                _1_4_Repository.activeId.value = newPeriodPair.second
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error in checkADD_1_4_PeriodeVent: ${e.message}")
        }
    }

    suspend fun ensureDataIsInitialized() {
        try {
            if (!initialDataLoaded) {
                withContext(Dispatchers.IO) {
                    // Wait until data is loaded
                    var timeoutCounter = 0
                    val maxTimeout = 50 // 5 seconds max wait (50 * 100ms)

                    while (!initialDataLoaded && timeoutCounter < maxTimeout) {
                        delay(100)
                        timeoutCounter++

                        if (progressRepo.value >= 0.95f) {
                            // Check if any required data is missing and create it
                            if (_1_5_Repository.modelDatasSnapList.isEmpty()) {
                                checkADD_1_5_Repository()
                            }

                            if (_1_4_Repository.modelDatasSnapList.isEmpty()) {
                                checkADD_1_4_PeriodeVent()
                            }



                            initialDataLoaded = true
                            progressRepo.value = 1.0f
                        }
                    }

                    if (!initialDataLoaded) {
                        Log.w(TAG, "Data initialization timed out, forcing initialization")
                        createInitialData()
                        initialDataLoaded = true
                        progressRepo.value = 1.0f
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error ensuring data initialization: ${e.message}")
            // Even if there's an error, try to create initial data
            createInitialData()
            initialDataLoaded = true
            progressRepo.value = 1.0f
        }
    }

    private suspend fun initialize_0_0_HeadOfRepositoryRepository() {
        try {
            progressRepo.value = 0.1f
            Log.d(TAG, "Starting repository initialization")

            // Initialize all child repositories in parallel for better performance
            val initJobs = listOf(
                repositoryScope.launch { _1_1_Repository.ensureDataIsInitialized() },
                repositoryScope.launch { _1_2_Repository.ensureDataIsInitialized() },
                repositoryScope.launch { _1_3_Repository.ensureDataIsInitialized() },
                repositoryScope.launch { _1_4_Repository.ensureDataIsInitialized() },
                repositoryScope.launch { _1_5_Repository.ensureDataIsInitialized() },
                repositoryScope.launch { _2_2_Repository.ensureDataIsInitialized() }
            )

            // Wait for all initialization to complete
            initJobs.forEach { it.join() }

            progressRepo.value = 0.5f
            collectRepositorys()

            if (TAG.isNotEmpty()) {
                log()
            }

            Log.d(TAG, "Repository initialization completed")
        } catch (e: Exception) {
            progressRepo.value = 0.1f
            Log.e(TAG, "Error initializing repository: ${e.message}", e)
        }
    }

    private suspend fun collectRepositorys() {
        try {
            progressRepo.value = 0.6f
            withContext(Dispatchers.IO) {
                // Create a repository head with all repositories
                repositorys_Model = _0_0_HeadOfRepositorys_Model(
                    _1_1_CouleurAcheteOperation_Repository = _1_1_Repository,
                    _1_2_ProduitAcheteOperation_Repository = _1_2_Repository,
                    _1_3_BonAchat_Repository = _1_3_Repository,
                    _1_4_PeriodeVent_Repository = _1_4_Repository,
                    _1_5_Vendeur_Repository = _1_5_Repository,
                    _2_2_ClientsDataBase_Repository = _2_2_Repository,
                    activeVID_1_3_BonAchat = _1_3_Repository.activeId.value // Use the active ID from the BonAchat repository
                )

                // Update progress
                progressRepo.value = 0.8f
                lastUpdateTimestamp = System.currentTimeMillis()
            }
        } catch (e: Exception) {
            progressRepo.value = 0.5f
            Log.e(TAG, "Error collecting repositories: ${e.message}")
        }
    }

    private suspend fun startProgressTracking(onComplete: () -> Unit = {}) {
        isFlowListenerActive = true
        var hasCompletedOnce = false

        try {
            // Use combine with a different syntax
            val combinedFlow = combine(
                _1_1_Repository.progressRepo,
                _1_2_Repository.progressRepo,
                _1_3_Repository.progressRepo,
                _1_4_Repository.progressRepo,
                _1_5_Repository.progressRepo,
                _2_2_Repository.progressRepo
            ) { flowValues ->
                // flowValues is an Array<Float> containing all the progress values
                val combinedProgress = flowValues.sum() / flowValues.size.toFloat()

                // Log the combined progress and possible reasons if not complete
                if (combinedProgress < 1.0f) {
                    Log.d(TAG, "Combined progress: ${String.format("%.2f", combinedProgress * 100)}%")
                    Log.d(TAG, "Possible reasons for incomplete progress:")

                    flowValues.forEachIndexed { index, progress ->
                        if (progress < 1.0f) {
                            val repoName = when(index) {
                                0 -> "_1_1_Repository"
                                1 -> "_1_2_Repository"
                                2 -> "_1_3_Repository"
                                3 -> "_1_4_Repository"
                                4 -> "_1_5_Repository"
                                5 -> "_2_2_Repository"
                                else -> "Unknown"
                            }
                            Log.d(TAG, "- $repoName incomplete: $progress")
                        }
                    }
                }

                combinedProgress
            }

            combinedFlow.collect { combinedProgress ->
                progressRepo.value = combinedProgress
                log()

                // Check if loading is complete (progress = 1.0f)
                if (combinedProgress >= 1.0f && !hasCompletedOnce) {
                    hasCompletedOnce = true
                    onComplete()
                }
            }
        } catch (e: Exception) {
            isFlowListenerActive = false
            Log.e(TAG, "Error tracking progress: ${e.message}")
            logOperations.logError("startProgressTracking", e)
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
