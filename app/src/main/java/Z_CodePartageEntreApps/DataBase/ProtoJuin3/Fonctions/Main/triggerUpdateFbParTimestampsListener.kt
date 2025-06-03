package A.AtelierMobile.Test.ID1.Test.Shared.DataBase.Fonctions.Main

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.A_ProduitInfosProtoJuin3
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.C_CategorieProduitInfos
import android.util.Log
import com.example.serveure.Manager.Room.AppDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

inline fun <reified T : Any> triggerUpdateFbParTimestampsListener(
    compt: Y_Model_ComptApp,
    scope: CoroutineScope,
    ref: DatabaseReference,
    noinline upsertFunction: suspend (T) -> Unit,
    noinline callback: (() -> Unit)? = null
) {
    ref.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            scope.launch {
                try {
                    processSnapshot(snapshot, compt.dernierFireBaseListening, upsertFunction)
                    callback?.invoke()
                } catch (e: Exception) {
                    Log.e("FirebaseListener", "Error in onDataChange for ${T::class.java.simpleName}", e)
                    callback?.invoke()
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseListener", "Firebase listener cancelled: ${error.message}", error.toException())
            callback?.invoke()
        }
    })
}

suspend inline fun <reified T : Any> processSnapshot(
    snapshot: DataSnapshot,
    lastTimestamp: Long,
    upsertFunction: suspend (T) -> Unit
) {
    var updateCount = 0
    val entityName = T::class.java.simpleName

    for (child in snapshot.children) {
        try {
            child.getValue(T::class.java)?.let { entity ->
                if (shouldUpdateEntity(entity, lastTimestamp)) {
                    upsertFunction(entity)
                    updateCount++
                }
            }
        } catch (e: Exception) {
            Log.w("FirebaseListener", "Failed to process child ${child.key}", e)
        }
    }

    Log.d("FirebaseListener", "Updated $updateCount $entityName records")
}

fun <T : Any> shouldUpdateEntity(entity: T, lastListeningTimestamp: Long): Boolean {
    return try {
        val timestampField = entity::class.java.getDeclaredField("dernierFireBaseUpdateTimestamps")
        timestampField.isAccessible = true
        timestampField.getLong(entity) > lastListeningTimestamp
    } catch (e: Exception) {
        Log.w("FirebaseListener", "Error checking timestamp for ${entity::class.java.simpleName}", e)
        true
    }
}

inline fun <reified T : Any> createFirebaseTimestampListener(
    compt: Y_Model_ComptApp,
    scope: CoroutineScope,
    ref: DatabaseReference,
    noinline upsertFunction: suspend (T) -> Unit,
    noinline callback: (() -> Unit)? = null
): ValueEventListener {
    return object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            scope.launch {
                try {
                    processSnapshot(snapshot, compt.dernierFireBaseListening, upsertFunction)
                    callback?.invoke()
                } catch (e: Exception) {
                    Log.e("FirebaseListener", "Error in onDataChange for ${T::class.java.simpleName}", e)
                    callback?.invoke()
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseListener", "Firebase listener cancelled: ${error.message}", error.toException())
            callback?.invoke()
        }
    }.also { ref.addValueEventListener(it) }
}

fun setupFirebaseListeners(
    scope: kotlinx.coroutines.CoroutineScope,
    appDatabase: AppDatabase,
    comptApp: Y_Model_ComptApp
) {
    setupProductFirebaseListener(scope, appDatabase, comptApp)
    setupCategoryFirebaseListener(scope, appDatabase, comptApp)
}

private fun setupProductFirebaseListener(
    scope: kotlinx.coroutines.CoroutineScope,
    appDatabase: AppDatabase,
    comptApp: Y_Model_ComptApp
) {
    triggerUpdateFbParTimestampsListener<A_ProduitInfosProtoJuin3>(
        compt = comptApp,
        scope = scope,
        ref = A_ProduitInfosProtoJuin3.caRef,
        upsertFunction = { product: A_ProduitInfosProtoJuin3 ->
            appDatabase.A_ProduitInfosProtoJuin3Dao().upsertData(product)
        },
        callback = {
            Log.d("Repository", "Product Firebase listener callback executed")
        }
    )
}

private fun setupCategoryFirebaseListener(
    scope: kotlinx.coroutines.CoroutineScope,
    appDatabase: AppDatabase,
    comptApp: Y_Model_ComptApp
) {
    triggerUpdateFbParTimestampsListener<C_CategorieProduitInfos>(
        compt = comptApp,
        scope = scope,
        ref = C_CategorieProduitInfos.caRef,
        upsertFunction = { category: C_CategorieProduitInfos ->
            appDatabase.c_CategorieProduitInfosDao().upsertData(category)
        },
        callback = {
            Log.d("Repository", "Category Firebase listener callback executed")
        }
    )
}
