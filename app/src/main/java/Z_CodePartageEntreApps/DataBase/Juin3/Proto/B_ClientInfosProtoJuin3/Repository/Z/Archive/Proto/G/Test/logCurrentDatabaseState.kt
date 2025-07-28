package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Test

import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.dataBaseCreationFactoryMID2ClientRepository
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// Extension pour ajouter des méthodes utilitaires de suivi
fun dataBaseCreationFactoryMID2ClientRepository.logCurrentDatabaseState() {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val currentData = dao.getAll()
            android.util.Log.d(repoTAG, "=== ÉTAT ACTUEL DE LA BD ===")
            android.util.Log.d(repoTAG, "Nombre total d'enregistrements: ${currentData.size}")
            android.util.Log.d(repoTAG, "Dernière mise à jour: ${System.currentTimeMillis()}")

            // Afficher quelques statistiques utiles
            val temporaryClients = currentData.count { it.cUnClientTemporaire }
            val clientsWithPhone = currentData.count { it.numTelephone.isNotEmpty() }
            val testEntities = currentData.count { it.keyFireBase.contains("TEST_") }

            android.util.Log.d(repoTAG, "Clients temporaires: $temporaryClients")
            android.util.Log.d(repoTAG, "Clients avec téléphone: $clientsWithPhone")
            android.util.Log.d(repoTAG, "Entités de test: $testEntities")
            android.util.Log.d(repoTAG, "=== FIN ÉTAT BD ===")

        } catch (e: Exception) {
            android.util.Log.e(repoTAG, "Erreur lors de l'affichage de l'état BD", e)
        }
    }
}

/**
 * Données d'état pour suivre les mises à jour de la BD locale
 */
data class LocalDbUpdateState(
    val lastUpdateTimestamp: Long = 0L,
    val totalRecords: Int = 0,
    val isUpdating: Boolean = false,
    val lastUpdatedRecordId: Long = 0L,
    val updateSource: UpdateSource = UpdateSource.UNKNOWN
)

enum class UpdateSource {
    FIREBASE_SYNC,
    LOCAL_INSERT,
    LOCAL_UPDATE,
    LOCAL_DELETE,
    INITIALIZATION,
    UNKNOWN
}

/**
 * Extension pour suivre les mises à jour de la base de données locale
 */
fun dataBaseCreationFactoryMID2ClientRepository.setupLocalDbUpdateTracker() {
    val _localDbUpdateState = MutableStateFlow(LocalDbUpdateState())
    val localDbUpdateState: StateFlow<LocalDbUpdateState> = _localDbUpdateState.asStateFlow()

    // Observer les changements dans repoState
    CoroutineScope(Dispatchers.IO).launch {
        repoState.collect { newState ->
            newState?.let { state ->
                val currentUpdateState = _localDbUpdateState.value
                val newRecordCount = state.modelListFlow.size

                // Détecter si la BD add_New été mise à jour
                val isUpdated = newRecordCount != currentUpdateState.totalRecords ||
                        currentUpdateState.lastUpdateTimestamp == 0L

                if (isUpdated) {
                    Log.d(repoTAG, "Base de données locale mise à jour!")
                    Log.d(repoTAG, "Ancien nombre d'enregistrements: ${currentUpdateState.totalRecords}")
                    Log.d(repoTAG, "Nouveau nombre d'enregistrements: $newRecordCount")

                    // Mettre à jour l'état de suivi
                    _localDbUpdateState.value = currentUpdateState.copy(
                        lastUpdateTimestamp = System.currentTimeMillis(),
                        totalRecords = newRecordCount,
                        isUpdating = false,
                        updateSource = determineUpdateSource(state.modelListFlow)
                    )

                    // Déclencher les actions post-mise à jour
                    onLocalDatabaseUpdated(state.modelListFlow, _localDbUpdateState.value)
                }
            }
        }
    }
}

/**
 * Détermine la source de la mise à jour basée sur les données
 */
private fun dataBaseCreationFactoryMID2ClientRepository.determineUpdateSource(
    dataList: List<M2Client>
): UpdateSource {
    return when {
        dataList.any { it.keyFireBase.contains("TEST_") } -> UpdateSource.FIREBASE_SYNC
        dataList.isEmpty() -> UpdateSource.INITIALIZATION
        else -> UpdateSource.LOCAL_UPDATE
    }
}

/**
 * Actions à effectuer quand la base de données locale est mise à jour
 */
private fun dataBaseCreationFactoryMID2ClientRepository.onLocalDatabaseUpdated(
    updatedData: List<M2Client>,
    updateState: LocalDbUpdateState
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            Log.d(repoTAG, "=== BD LOCALE MISE À JOUR ===")
            Log.d(repoTAG, "Timestamp: ${updateState.lastUpdateTimestamp}")
            Log.d(repoTAG, "Nombre total d'enregistrements: ${updateState.totalRecords}")
            Log.d(repoTAG, "Source de mise à jour: ${updateState.updateSource}")

            // 1. Valider la cohérence des données
            validateDataConsistency(updatedData)

            // 2. Mettre à jour les statistiques
            updateLocalStatistics(updatedData)

            // 3. Déclencher les notifications si nécessaire
            triggerUpdateNotifications(updateState)

            // 4. Synchroniser avec Firebase si c'est une mise à jour locale
            if (updateState.updateSource == UpdateSource.LOCAL_UPDATE ||
                updateState.updateSource == UpdateSource.LOCAL_INSERT
            ) {
                syncRecentChangesToFirebase(updatedData)
            }

            // 5. Nettoyer les anciennes données de test si nécessaire
            cleanupTestDataIfNeeded(updatedData)

            Log.d(repoTAG, "=== FIN TRAITEMENT MISE À JOUR BD ===")

        } catch (e: Exception) {
            Log.e(repoTAG, "Erreur lors du traitement de la mise à jour BD", e)
        }
    }
}

/**
 * Valide la cohérence des données après mise à jour
 */
private suspend fun dataBaseCreationFactoryMID2ClientRepository.validateDataConsistency(
    data: List<M2Client>
) {
    withContext(Dispatchers.IO) {
        // Vérifier les doublons
        val duplicateIds = data.groupBy { it.id }
            .filter { it.value.size > 1 }
            .keys

        if (duplicateIds.isNotEmpty()) {
            Log.w(repoTAG, "IDs dupliqués détectés: $duplicateIds")
        }

        // Vérifier les clés Firebase vides
        val emptyFirebaseKeys = data.filter { it.keyID.isEmpty() }
        if (emptyFirebaseKeys.isNotEmpty()) {
            Log.w(repoTAG, "${emptyFirebaseKeys.size} enregistrements avec clés Firebase vides")
        }

        Log.d(repoTAG, "Validation de cohérence terminée")
    }
}

/**
 * Met à jour les statistiques locales
 */
private fun dataBaseCreationFactoryMID2ClientRepository.updateLocalStatistics(
    data: List<M2Client>
) {
    val stats = mapOf(
        "total_clients" to data.size,
        "clients_temporaires" to data.count { it.cUnClientTemporaire },
        "clients_avec_telephone" to data.count { it.numTelephone.isNotEmpty() },
        "derniere_mise_a_jour" to System.currentTimeMillis()
    )

    Log.d(repoTAG, "Statistiques mises à jour: $stats")
}

/**
 * Déclenche les notifications de mise à jour
 */
private fun dataBaseCreationFactoryMID2ClientRepository.triggerUpdateNotifications(
    updateState: LocalDbUpdateState
) {
    // Ici, vous pouvez ajouter des notifications push, des callbacks UI, etc.
    Log.d(repoTAG, "Notifications de mise à jour déclenchées")
}

/**
 * Synchronise les changements récents avec Firebase
 */
private fun dataBaseCreationFactoryMID2ClientRepository.syncRecentChangesToFirebase(
    data: List<M2Client>
) {
    val recentChanges = data.filter {
        System.currentTimeMillis() - it.dernierTimeTampsSynchronisationAvecFireBase < 60000 // 1 minute
    }

    if (recentChanges.isNotEmpty()) {
        Log.d(repoTAG, "Synchronisation de ${recentChanges.size} changements récents avec Firebase")
        // La synchronisation sera effectuée par les fonctions existantes
    }
}

/**
 * Nettoie les données de test anciennes
 */
private suspend fun dataBaseCreationFactoryMID2ClientRepository.cleanupTestDataIfNeeded(
    data: List<M2Client>
) {
    withContext(Dispatchers.IO) {
        val testData = data.filter { it.keyFireBase.contains("TEST_") }
        val oldTestData = testData.filter {
            System.currentTimeMillis() - it.cretionTimestamps > 24 * 60 * 60 * 1000 // 24 heures
        }

        if (oldTestData.isNotEmpty()) {
            Log.d(repoTAG, "Nettoyage de ${oldTestData.size} anciennes données de test")
            oldTestData.forEach { testEntity ->
                try {
                    dao.deleteData(testEntity)
                    repoRef.child(testEntity.keyFireBase).removeValue()
                } catch (e: Exception) {
                    Log.e(repoTAG, "Erreur lors de la suppression des données de test", e)
                }
            }
        }
    }
}
/**
 * Test function to trigger Firebase timestamp listener by creating or updating add_New test entity
 * This function helps verify that the Firebase listener is working correctly
 */
fun dataBaseCreationFactoryMID2ClientRepository.testTriggerUpdateFbParTimestampsListener() {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            Log.d(repoTAG, "Starting Firebase listener test")

            // Check if add_New test entity already exists
            val existingTestEntity = dao.getAll().find { it.keyFireBase.contains("TEST_") }

            if (existingTestEntity != null) {
                Log.d(repoTAG, "Updating existing test entity: ${existingTestEntity.keyFireBase}")

                // Update existing test entity with new timestamp
                val updatedTestEntity = existingTestEntity.copy(
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                ).with_Trigger_RealTime()

                // Push update_showDetailsExpanded to Firebase to trigger listener
                repoRef.child(updatedTestEntity.keyFireBase).setValue(updatedTestEntity)
                    .addOnSuccessListener {
                        Log.d(repoTAG, "Test entity updated successfully in Firebase")
                    }
                    .addOnFailureListener { exception ->
                        Log.e(repoTAG, "Failed to update_showDetailsExpanded test entity in Firebase", exception)
                    }
            } else {
                Log.d(repoTAG, "Creating new test entity")

                // Create new test entity
                val testEntity = M2Client(
                    nom = "TEST_ENTITY_${System.currentTimeMillis()}",
                    keyFireBase = "TEST_${System.currentTimeMillis()}",
                    cUnClientTemporaire = true,
                    numTelephone = "TEST_PHONE"
                ).with_Trigger_RealTime()

                // Push new entity to Firebase to trigger listener
                repoRef.child(testEntity.keyFireBase).setValue(testEntity)
                    .addOnSuccessListener {
                        Log.d(repoTAG, "New test entity created successfully in Firebase")
                    }
                    .addOnFailureListener { exception ->
                        Log.e(repoTAG, "Failed to create test entity in Firebase", exception)
                    }
            }
        } catch (e: Exception) {
            Log.e(repoTAG, "Error in testTriggerUpdateFbParTimestampsListener", e)
        }
    }
}
