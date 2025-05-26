package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.getKeyFireBase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.full.memberProperties

suspend inline fun <reified DataBase : Any> F0_FireBaseOperationsHandler.extractedsetDataInlineFun(
    datas: List<DataBase>,
    processedCount: Int,
    dataMap: MutableMap<String, Any>,
    resultMap: MutableMap<String, DataBase>,
    totalCount: Int
) {
    var processedCount1 = processedCount
    datas.forEach { data ->
        try {
            val itemMap = mutableMapOf<String, Any>()

            data::class.memberProperties.forEach { prop ->
                try {
                    val value = prop.getter.call(data)
                    when {
                        value == null -> itemMap[prop.name] = "null"
                        value::class.java.isEnum -> itemMap[prop.name] = value.toString()
                        else -> itemMap[prop.name] = value
                    }
                } catch (e: Exception) {
                    itemMap[prop.name] = "null"
                }
            }

            val key = when (data) {
                is D_TarificationInfos -> {
                    val updatedData = if (data.keyFireBase.isEmpty()) {
                        data.withProperDefaults()
                    } else {
                        data
                    }
                    updatedData.keyFireBase.ifEmpty {
                        getKeyFireBase(updatedData.id, updatedData.nom)
                    }
                }

                is A_ProduitInfos -> {
                    val updatedData = if (data.keyFireBase.isEmpty()) {
                        data.withProperKeyFireBase()
                    } else {
                        data
                    }
                    updatedData.keyFireBase.ifEmpty {
                        getKeyFireBase(updatedData.idArticle, updatedData.nomArticleFinale)
                    }
                }

                else -> {
                    "unknown_${System.currentTimeMillis()}_$processedCount1"
                }
            }

            dataMap[key] = itemMap
            resultMap[key] = data
            processedCount1++

            val progress = 0.3f + (processedCount1.toFloat() / totalCount) * 0.4f
            onProgressUpdate(progress)

        } catch (e: Exception) {
            processedCount1++
        }
    }

    if (dataMap.isNotEmpty()) {
        try {
            onProgressUpdate(0.8f)

            val childRef = when (DataBase::class) {
                D_TarificationInfos::class -> childD_TarificationInfos
                A_ProduitInfos::class -> childA_ProduitInfos
                else -> throw IllegalArgumentException("Unsupported data type: ${DataBase::class.simpleName}")
            }

            suspendCancellableCoroutine<Unit> { continuation ->
                childRef.updateChildren(dataMap)
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
