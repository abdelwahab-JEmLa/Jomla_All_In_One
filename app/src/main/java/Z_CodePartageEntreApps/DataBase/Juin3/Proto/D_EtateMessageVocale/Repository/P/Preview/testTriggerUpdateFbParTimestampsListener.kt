package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.P.Preview

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.D_EtateMessageVocale
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocaleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun D_EtateMessageVocaleRepository.testTriggerUpdateFbParTimestampsListener() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existingTestEntity = dao.getAll().find { it.keyFireBase.contains("TEST_") }

                if (existingTestEntity != null) {
                    val updatedTestEntity = existingTestEntity.copy(
                        dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                    ).withProperKeyFireBaseAndTimeTamp()

                    repoRef.child(updatedTestEntity.keyFireBase).setValue(updatedTestEntity)
                } else {
                    val testEntity = D_EtateMessageVocale.createTestInstance().first()
                    val testEntityWithTimestamp = testEntity.copy(
                        keyFireBase = "TEST_${System.currentTimeMillis()}"
                    ).withProperKeyFireBaseAndTimeTamp()

                    repoRef.child(testEntityWithTimestamp.keyFireBase).setValue(testEntityWithTimestamp)
                }
            } catch (e: Exception) {}
        }
    }
