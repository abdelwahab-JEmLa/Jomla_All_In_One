package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository.FireBase

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

suspend inline fun <reified DataBase : Any> F0_FireBaseOperationsHandler.deleteRef(): Boolean = withContext(Dispatchers.IO) {
        return@withContext suspendCancellableCoroutine { continuation ->
            try {
                onProgressUpdate(0.1f)

                val childRef = when (DataBase::class) {
                    D_TarificationInfos::class -> childD_TarificationInfos
                    A_ProduitInfos::class -> childA_ProduitInfos
                    else -> {
                        onProgressUpdate(0f)
                        F6_FirebaseDebugUtils.logFirebaseOperation(
                            "deleteRef_INVALID_TYPE",
                            ref,
                            0,
                            false,
                            Exception("Invalid DataBase type: ${DataBase::class.simpleName}")
                        )
                        continuation.resume(false)
                        return@suspendCancellableCoroutine
                    }
                }

                onProgressUpdate(0.5f)

                childRef.removeValue()
                    .addOnSuccessListener {
                        onProgressUpdate(1f)
                        F6_FirebaseDebugUtils.logFirebaseOperation(
                            "deleteRef_SUCCESS",
                            childRef,
                            0,
                            true
                        )
                        continuation.resume(true)
                    }
                    .addOnFailureListener { exception ->
                        onProgressUpdate(0f)
                        F6_FirebaseDebugUtils.logFirebaseOperation(
                            "deleteRef_ERROR",
                            childRef,
                            0,
                            false,
                            exception
                        )
                        continuation.resume(false)
                    }

            } catch (e: Exception) {
                onProgressUpdate(0f)
                F6_FirebaseDebugUtils.logFirebaseOperation("deleteRef_EXCEPTION", ref, 0, false, e)
                e.printStackTrace()
                continuation.resume(false)
            }
        }
    }
