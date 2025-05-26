package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository.FireBase

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.getKeyFireBase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.full.memberProperties

suspend fun F0_FireBaseOperationsHandler.extractedFromeupsertAllAndReturnListIdToData(
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

                tariff::class.memberProperties.forEach { prop ->
                    try {
                        val value = prop.getter.call(tariff)
                        when {
                            value == null -> tariffMap[prop.name] = ""
                            value::class.java.isEnum -> tariffMap[prop.name] = value.toString()
                            value is String && value.isEmpty() -> tariffMap[prop.name] = ""
                            else -> tariffMap[prop.name] = value
                        }
                    } catch (e: Exception) {
                        tariffMap[prop.name] = when (prop.returnType.classifier) {
                            String::class -> ""
                            Long::class -> 0L
                            Int::class -> 0
                            Double::class -> 0.0
                            Boolean::class -> false
                            else -> ""
                        }
                    }
                }

                val updatedTariff = tariff.withProperDefaults()
                val key = getKeyFireBase(updatedTariff.id, updatedTariff.nom)

                if (isValidFirebaseKey(key)) {
                    tariffMap["keyFireBase"] = key
                    tariffMap["nom"] = updatedTariff.nom
                    tariffMap["needUpdate"] = true

                    tariffsMap[key] = tariffMap
                    resultMap[key] = updatedTariff
                }

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
                            F6_FirebaseDebugUtils.logFirebaseOperation(
                                "upsertAllAndReturnListIdToData_FIREBASE_SUCCESS",
                                childD_TarificationInfos,
                                tariffsMap.size,
                                true
                            )
                            continuation.resume(Unit)
                        }
                        .addOnFailureListener { exception ->
                            F6_FirebaseDebugUtils.logFirebaseOperation(
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
                F6_FirebaseDebugUtils.logFirebaseOperation(
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

    F6_FirebaseDebugUtils.logFirebaseOperation(
        "upsertAllAndReturnListIdToData_COMPLETE",
        childD_TarificationInfos,
        resultMap.size,
        true
    )
    }
