package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.getKeyFireBaseSafe
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.full.memberProperties

suspend fun F0_FireBaseOperationsHandler.extractedupsertAllAndReturnListIdToData(
    mapData: Map<Long, D_TarificationInfos>,
    tariffsMap: MutableMap<String, Any>,
    resultMap: MutableMap<String, D_TarificationInfos>,
    processedCount: Int,
    totalCount: Int
) {
    var processedCount1 = processedCount
    mapData.values.forEach { tariff ->
        try {
            val tariffMap = mutableMapOf<String, Any>()

            tariff::class.memberProperties.forEach { prop ->
                try {
                    val value = prop.getter.call(tariff)
                    when {
                        value == null -> tariffMap[prop.name] = "null"
                        value::class.java.isEnum -> tariffMap[prop.name] = value.toString()
                        else -> tariffMap[prop.name] = value
                    }
                } catch (e: Exception) {
                    tariffMap[prop.name] = "null"
                }
            }

            val key = getKeyFireBaseSafe(tariff.id, tariff.nom)

            tariffsMap[key] = tariffMap
            resultMap[key] = tariff

            processedCount1++
            val progress = 0.3f + (processedCount1.toFloat() / totalCount) * 0.4f
            onProgressUpdate(progress)

        } catch (e: Exception) {
            onProgressUpdate(0.5f)
        }
    }

    if (tariffsMap.isNotEmpty()) {
        try {
            onProgressUpdate(0.8f)

            suspendCancellableCoroutine<Unit> { continuation ->
                childD_TarificationInfos.updateChildren(tariffsMap)
                    .addOnSuccessListener {
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }

            onProgressUpdate(1f)

        } catch (firebaseException: Exception) {
            onProgressUpdate(0.8f)
        }
    } else {
        onProgressUpdate(1f)
    }
}
