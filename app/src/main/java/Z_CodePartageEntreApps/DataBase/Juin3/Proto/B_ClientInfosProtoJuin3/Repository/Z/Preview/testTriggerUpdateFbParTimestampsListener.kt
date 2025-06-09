package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Preview

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun B_ClientInfosProtoJuin3Repository.testTriggerUpdateFbParTimestampsListener() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existingTestEntity = dao.getAll().find { it.keyFireBase.contains("TEST_") }

                if (existingTestEntity != null) {
                    val updatedTestEntity = existingTestEntity.copy(
                        dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                    ).withProperKeyFireBaseAndTimeTamp()

                    repoRef.child(updatedTestEntity.keyFireBase).setValue(updatedTestEntity)
                } else {
                    val testEntity = B_ClientInfosProtoJuin3.createTestInstance().first()
                    val testEntityWithTimestamp = testEntity.copy(
                        keyFireBase = "TEST_${System.currentTimeMillis()}"
                    ).withProperKeyFireBaseAndTimeTamp()

                    repoRef.child(testEntityWithTimestamp.keyFireBase).setValue(testEntityWithTimestamp)
                }
            } catch (e: Exception) {}
        }
    }
