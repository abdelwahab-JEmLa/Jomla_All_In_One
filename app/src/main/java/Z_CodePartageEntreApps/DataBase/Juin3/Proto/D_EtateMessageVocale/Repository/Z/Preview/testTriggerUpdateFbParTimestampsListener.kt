package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Z.Preview

import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocaleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun D_EtateMessageVocaleRepository.testTriggerUpdateFbParTimestampsListener() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existingTestEntity = dao.getAll().find { it.keyID.contains("TEST_") }

                if (existingTestEntity != null) {
                    val updatedTestEntity = existingTestEntity.copy(
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )

                    repoRef.child(updatedTestEntity.keyID).setValue(updatedTestEntity)
                } else {
                    val testEntity = M17MessageVocale.createTestInstance().first()
                    val testEntityWithTimestamp = testEntity.copy(
                        keyID = "TEST_${System.currentTimeMillis()}"
                    )

                    repoRef.child(testEntityWithTimestamp.keyID).setValue(testEntityWithTimestamp)
                }
            } catch (e: Exception) {}
        }
    }
