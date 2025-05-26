package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.getKeyFireBase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.full.memberProperties

suspend inline fun <reified DataBase : Any> F0_FireBaseOperationsHandler.extractedsetDataInlineFunFixed(
    datas: List<DataBase>,
    processedCount: Int,
    dataMap: MutableMap<String, Any>,
    resultMap: MutableMap<String, DataBase>,
    totalCount: Int
) {
    var currentProcessedCount = processedCount

    val childRef = when (DataBase::class) {
        D_TarificationInfos::class -> childD_TarificationInfos
        A_ProduitInfos::class -> childA_ProduitInfos
        else -> {
            return
        }
    }

    datas.forEach { data ->
        try {
            val itemMap = mutableMapOf<String, Any>()

            data::class.memberProperties.forEach { prop ->
                try {
                    val value = prop.getter.call(data)
                    when {
                        value == null -> itemMap[prop.name] = ""
                        value::class.java.isEnum -> itemMap[prop.name] = value.toString()
                        value is String && value.isEmpty() -> itemMap[prop.name] = ""
                        else -> itemMap[prop.name] = value
                    }
                } catch (e: Exception) {
                    itemMap[prop.name] = when (prop.returnType.classifier) {
                        String::class -> ""
                        Long::class -> 0L
                        Int::class -> 0
                        Double::class -> 0.0
                        Boolean::class -> false
                        else -> ""
                    }
                }
            }

            val (safeKey, updatedData) = when (data) {
                is D_TarificationInfos -> {
                    val updated = data.withProperDefaults()
                    val key = getKeyFireBase(updated.id, updated.nom)

                    itemMap["keyFireBase"] = key
                    itemMap["nom"] = updated.nom
                    itemMap["needUpdate"] = true

                    Pair(key, updated as DataBase)
                }

                is A_ProduitInfos -> {
                    val updated = data.withProperKeyFireBase()
                    val key = getKeyFireBase(updated.idArticle, updated.nomArticleFinale)

                    itemMap["keyFireBase"] = key
                    itemMap["needUpdate"] = true

                    Pair(key, updated as DataBase)
                }

                else -> {
                    val fallbackKey = getKeyFireBase(null, null)
                    itemMap["keyFireBase"] = fallbackKey
                    Pair(fallbackKey, data)
                }
            }

            if (isValidFirebaseKey(safeKey)) {
                dataMap[safeKey] = itemMap
                resultMap[safeKey] = updatedData
            } else {
                val fallbackKey = "ITEM_${System.currentTimeMillis()}_${currentProcessedCount}"
                if (isValidFirebaseKey(fallbackKey)) {
                    itemMap["keyFireBase"] = fallbackKey
                    dataMap[fallbackKey] = itemMap
                    resultMap[fallbackKey] = updatedData
                }
            }

            currentProcessedCount++
            val progress = 0.3f + (currentProcessedCount.toFloat() / totalCount) * 0.4f
            onProgressUpdate(progress)

        } catch (e: Exception) {
            e.printStackTrace()
            currentProcessedCount++
        }
    }

    if (dataMap.isNotEmpty()) {
        try {
            onProgressUpdate(0.8f)

            val invalidKeys = dataMap.keys.filter { !isValidFirebaseKey(it) }
            if (invalidKeys.isNotEmpty()) {
                invalidKeys.forEach { dataMap.remove(it) }
            }

            if (dataMap.isNotEmpty()) {
                suspendCancellableCoroutine<Unit> { continuation ->
                    childRef.updateChildren(dataMap)
                        .addOnSuccessListener {
                            continuation.resume(Unit)
                        }
                        .addOnFailureListener { exception ->
                            exception.printStackTrace()
                            continuation.resumeWithException(exception)
                        }
                }
            }

            onProgressUpdate(1f)

        } catch (firebaseException: Exception) {
            firebaseException.printStackTrace()
            onProgressUpdate(0.8f)
        }
    } else {
        onProgressUpdate(1f)
    }
}

fun isValidFirebaseKey(key: String): Boolean {
    return when {
        key.isEmpty() -> false
        key.length > 768 -> false
        key.contains('.') -> false
        key.contains('#') -> false
        key.contains('$') -> false
        key.contains('[') -> false
        key.contains(']') -> false
        key.contains('/') -> false
        key.any { it.toInt() < 32 || it.toInt() == 127 } -> false
        else -> true
    }
}
