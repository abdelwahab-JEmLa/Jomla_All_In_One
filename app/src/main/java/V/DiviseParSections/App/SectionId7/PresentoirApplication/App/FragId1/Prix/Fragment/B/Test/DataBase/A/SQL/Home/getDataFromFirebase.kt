package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Home
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.DataBasesInfosSql
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun FireBaseHandler.getDataFromFirebase(): DataBasesInfosSql? = withContext(Dispatchers.IO) {
        Log.d(TAG, "Attempting to retrieve data from Firebase path: ${getRefPath()}")

        suspendCancellableCoroutine { continuation ->
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Log.d(TAG, "Firebase data snapshot exists, mapping data...")
                        try {
                            val infosSqlDataBases = mapFromFirebaseSnapshot(snapshot)
                            Log.d(TAG, "Successfully mapped Firebase data: " +
                                    "Products: ${infosSqlDataBases.a_ProduitInfos.size}, " +
                                    "Clients: ${infosSqlDataBases.b_ClientInfos.size}, " +
                                    "TypeTarifs: ${infosSqlDataBases.c_TypeTarificationInfos.size}, " +
                                    "Tarifications: ${infosSqlDataBases.d_TarificationInfos.size}")
                            continuation.resume(infosSqlDataBases)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error mapping Firebase data: ${e.message}", e)
                            continuation.resumeWithException(e)
                        }
                    } else {
                        Log.w(TAG, "Firebase data snapshot does NOT exist at path: ${getRefPath()}")
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Firebase data retrieval cancelled: ${error.message}")
                    continuation.resumeWithException(Exception("Firebase data retrieval cancelled: ${error.message}"))
                }
            })

            continuation.invokeOnCancellation {
                Log.d(TAG, "Firebase data retrieval coroutine cancelled")
                // Clean up if needed
            }
        }
    }
