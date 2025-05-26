package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository.FireBase

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.getKeyFireBase
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository.FireBase.ReflectionUtils.isSyntheticPropertyName
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.full.memberProperties

suspend fun F0_FireBaseOperationsHandler.extractedFrome_upsertAllAndReturnListIdToData(
    mapData: Map<Long, D_TarificationInfos>,
    tariffsMap: MutableMap<String, Any>,
    resultMap: MutableMap<String, D_TarificationInfos>,
    processedCount1: Int,
    totalCount: Int
) {
    var processedCount11 = processedCount1
    mapData.values.forEach { tariff ->
        try {
            val tariffMap = mutableMapOf<String, Any>()

            // FIXED: Use consistent property filtering
            tariff::class.memberProperties.forEach { prop ->
                if (!isSyntheticPropertyName(prop.name)) {
                    try {
                        val value = prop.getter.call(tariff)
                        tariffMap[prop.name] =
                            sanitizePropertyValue(value, prop.returnType.classifier)
                    } catch (e: Exception) {
                        tariffMap[prop.name] = getDefaultValueForType(prop.returnType.classifier)
                    }
                }
            }

            val updatedTariff = tariff.withProperDefaults()
            val key = getKeyFireBase(updatedTariff.id, updatedTariff.nom)

            tariffMap["keyFireBase"] = key
            tariffMap["nom"] = updatedTariff.nom
            tariffMap["needUpdate"] = true

            tariffsMap[key] = tariffMap
            resultMap[key] = updatedTariff

            processedCount11++
            val progress = 0.3f + (processedCount11.toFloat() / totalCount) * 0.4f
            onProgressUpdate(progress)

        } catch (e: Exception) {
            e.printStackTrace()
            processedCount11++
        }
    }

    if (tariffsMap.isNotEmpty()) {
        try {
            onProgressUpdate(0.8f)

            suspendCancellableCoroutine<Unit> { continuation ->
                childD_TarificationInfos.updateChildren(tariffsMap)
                    .addOnSuccessListener {
                        F9_FirebaseDebugUtils.logFirebaseOperation(
                            "upsertAllAndReturnListIdToData_FIREBASE_SUCCESS",
                            childD_TarificationInfos,
                            tariffsMap.size,
                            true
                        )
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { exception ->
                        F9_FirebaseDebugUtils.logFirebaseOperation(
                            "upsertAllAndReturnListIdToData_FIREBASE_ERROR",
                            childD_TarificationInfos,
                            tariffsMap.size,
                            false,
                            exception
                        )
                        continuation.resumeWithException(exception)
                    }
            }

            onProgressUpdate(1f)

        } catch (firebaseException: Exception) {
            firebaseException.printStackTrace()
            F9_FirebaseDebugUtils.logFirebaseOperation(
                "upsertAllAndReturnListIdToData_FIREBASE_EXCEPTION",
                childD_TarificationInfos,
                0,
                false,
                firebaseException
            )
            onProgressUpdate(0.8f)
        }
    } else {
        onProgressUpdate(1f)
    }

    F9_FirebaseDebugUtils.logFirebaseOperation(
        "upsertAllAndReturnListIdToData_COMPLETE",
        childD_TarificationInfos,
        resultMap.size,
        true
    )
}
