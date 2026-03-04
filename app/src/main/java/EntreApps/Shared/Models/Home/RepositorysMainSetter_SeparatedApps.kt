package EntreApps.Shared.Models.Home

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Modules.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class RepositorysMainSetter_SeparatedApps(
    val appDatabase: AppDatabase
) {
    private val composScope = CoroutineScope(Dispatchers.IO)

    fun update_M1Produit(
        data: M01Produit
    ) {
        val updates = mutableMapOf<String, Any>()

        composScope.launch {
            appDatabase.dao_M1Produit().update(data)
            listOf(data).forEach { data ->
                updates[data.keyID] = data.toFirebaseMap()
            }
            val firebaseRef = M01Produit.ref
            firebaseRef.updateChildren(updates).await()
        }
    }

    fun insert_M16CategorieProduit(
        data: M16CategorieProduit
    ) {
        composScope.launch {
            appDatabase.dao_16CategorieProduit().insert(data)

            val updates = mutableMapOf<String, Any>()
            listOf(data).forEach { data ->
                updates[data.keyID] = data.toFirebaseMap()
            }
            val firebaseRef = M16CategorieProduit.ref
            firebaseRef.updateChildren(updates).await()
        }
    }

    fun update_M16CategorieProduit(
        data: M16CategorieProduit
    ) {              //<--
    //TODO(1): affiche toest si non dispot data et n update pas 
        composScope.launch {
            appDatabase.dao_16CategorieProduit().update(data)

            val updates = mutableMapOf<String, Any>()
            listOf(data).forEach { data ->
                updates[data.keyID] = data.toFirebaseMap()
            }
            val firebaseRef = M16CategorieProduit.ref
            firebaseRef.updateChildren(updates).await()
        }
    }

}
