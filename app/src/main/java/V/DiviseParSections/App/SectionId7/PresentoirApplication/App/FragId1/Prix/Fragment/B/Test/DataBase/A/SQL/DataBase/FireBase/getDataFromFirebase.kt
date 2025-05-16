package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.DataBase.FireBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.DataBasesInfosSql
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun FireBaseOperationsHandler.getDataFromFirebase(): DataBasesInfosSql? = withContext(Dispatchers.IO) {
    suspendCancellableCoroutine { continuation ->
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        val infosSqlDataBases = mapFromFirebaseSnapshot(snapshot)
                        continuation.resume(infosSqlDataBases)
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                } else {
                    continuation.resume(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(Exception("Firebase data retrieval cancelled: ${error.message}"))
            }
        })
    }
}
