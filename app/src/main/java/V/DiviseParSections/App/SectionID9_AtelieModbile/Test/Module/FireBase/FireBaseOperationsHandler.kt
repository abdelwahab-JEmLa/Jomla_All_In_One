package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBase

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.SQl.RoomOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.DataBasesInfosSql
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.getKeyFireBase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.reflect.full.memberProperties

class FireBaseOperationsHandler(
    val roomOperationsHandler: RoomOperationsHandler
) {
    companion object {
        const val TAG = "FireBaseOperationsHandler"
    }

    val ref: DatabaseReference = _0_0_HeadOfRepositorys_Model
        .getHeadSqlDataBaseRef().child("C_InfosSqlDataBases")

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    var needUpdateListener: ValueEventListener? = null

    private fun isDatabaseEmpty(onResult: (Boolean) -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isEmpty = !snapshot.exists() || !snapshot.hasChildren()
                onResult(isEmpty)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(false)
            }
        })
    }

    suspend fun isDatabaseEmptyAsync(): Boolean = suspendCancellableCoroutine { continuation ->
        isDatabaseEmpty { isEmpty ->
            continuation.resume(isEmpty)
        }
    }

    // Method to delete D_TarificationInfos node from Firebase
    fun deleteTarificationInfosNode(onComplete: (Boolean) -> Unit = {}) {
        Log.d(TAG, "Attempting to delete D_TarificationInfos node")
        ref.child("D_TarificationInfos").removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "Successfully deleted D_TarificationInfos node")
                onComplete(true)
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to delete D_TarificationInfos node: ${error.message}")
                onComplete(false)
            }
    }

    // Add a single tarification item directly to Firebase
    fun addSingleTariffToFirebase(tariff: D_TarificationInfos) {
        try {
            val tarifMap = mutableMapOf<String, Any>()
            Log.d(TAG, "Adding tariff to Firebase: id=${tariff.id}, name=${tariff.nom}")

            // Convert all properties from the tariff object to a map
            tariff::class.memberProperties.forEach { prop ->
                val value = prop.getter.call(tariff)
                if (value != null) {
                    if (value::class.java.isEnum) {
                        tarifMap[prop.name] = value.toString()
                        Log.v(TAG, "Property ${prop.name} = ${value.toString()} (enum)")
                    } else {
                        tarifMap[prop.name] = value
                        Log.v(TAG, "Property ${prop.name} = $value")
                    }
                } else {
                    tarifMap[prop.name] = "null"
                    Log.v(TAG, "Property ${prop.name} = null")
                }
            }

            // Generate a unique key for this tariff
            val key = tariff.keyFireBase.takeIf { it.isNotEmpty() }
                ?: getKeyFireBase(tariff.id, tariff.nom)

            Log.d(TAG, "Using Firebase key: $key for tariff: ${tariff.nom}")

            // Direct write to Firebase for this specific tariff
            ref.child("D_TarificationInfos").child(key).setValue(tarifMap)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully wrote tariff to Firebase: $key")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to write tariff to Firebase: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Exception adding tariff to Firebase", e)
        }
    }

    fun addToFirebaseAsync(dataBasesInfosSql: DataBasesInfosSql, onSuccess: () -> Unit = {}) {
        Log.d(TAG, "Adding data to Firebase async")
        val firebaseData = mapToFirebaseFormat(dataBasesInfosSql)
        val updates = mutableMapOf<String, Any>()

        if (firebaseData.containsKey(dataBasesInfosSql.refFireBaseA_ProduitInfos)) {
            updates[dataBasesInfosSql.refFireBaseA_ProduitInfos] = firebaseData[dataBasesInfosSql.refFireBaseA_ProduitInfos] as Any
            Log.d(TAG, "Adding A_ProduitInfos to updates")
        }

        if (firebaseData.containsKey(dataBasesInfosSql.refFireBaseB_ClientInfos)) {
            updates[dataBasesInfosSql.refFireBaseB_ClientInfos] = firebaseData[dataBasesInfosSql.refFireBaseB_ClientInfos] as Any
            Log.d(TAG, "Adding B_ClientInfos to updates")
        }

        if (firebaseData.containsKey(dataBasesInfosSql.refFireBaseC_TypeTarificationInfos)) {
            updates[dataBasesInfosSql.refFireBaseC_TypeTarificationInfos] = firebaseData[dataBasesInfosSql.refFireBaseC_TypeTarificationInfos] as Any
            Log.d(TAG, "Adding C_TypeTarificationInfos to updates")
        }

        if (firebaseData.containsKey(dataBasesInfosSql.refFireBaseD_TarificationInfos)) {
            updates[dataBasesInfosSql.refFireBaseD_TarificationInfos] = firebaseData[dataBasesInfosSql.refFireBaseD_TarificationInfos] as Any
            Log.d(TAG, "Adding D_TarificationInfos to updates")
        }

        if (updates.isEmpty()) {
            Log.w(TAG, "No updates to send to Firebase")
            onSuccess()
            return
        }

        Log.d(TAG, "Sending ${updates.size} updates to Firebase")
        ref.updateChildren(updates)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully updated Firebase data")
                onSuccess()
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to update Firebase data: ${error.message}")
                ref.root.child(".info/connected").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d(TAG, "Firebase connection status: ${snapshot.getValue(Boolean::class.java)}")
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Firebase connection check cancelled: ${error.message}")
                    }
                })
                onSuccess()
            }
    }
}
