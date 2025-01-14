package Y_AppsFather.Kotlin.ViewModel.Extensions

import Y_AppsFather.Kotlin.Model._ModelAppsFather
import Y_AppsFather.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel
import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel
import Y_AppsFather.Kotlin.Model._ModelAppsFather.ProduitModel.GrossistBonCommandes
import Y_AppsFather.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

fun ViewModelInitApp.setupSimpleDataListener() {
    _modelAppsFather.produitsMainDataBase.forEach { produit ->
        Log.d("DataListener", "Setting up listener for product ${produit.id}")

        produitsFireBaseRef.child(produit.id.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewModelScope.launch {
                        try {
                            // Log initial state
                            Log.d("DataListener", """
                                Product state before update:
                                ID: ${produit.id}
                                Name: ${produit.nom}
                                Has bonsVentDeCetteCota: ${produit.bonsVentDeCetteCota.isNotEmpty()}
                                Has bonCommendDeCetteCota: ${produit.bonCommendDeCetteCota != null}
                            """.trimIndent())

                            // Handle bonsVentDeCetteCota
                            val bonVentSnapshot = snapshot.child("bonsVentDeCetteCota")
                            if (bonVentSnapshot.exists()) {
                                val bonVentValue = bonVentSnapshot.getValue(ClientBonVentModel::class.java)
                                Log.d("DataListener", "Parsed bonVentValue: $bonVentValue")

                                if (bonVentValue != null) {
                                    Log.d("DataListener", "Updating bonsVentDeCetteCota for product ${produit.id}")
                                    ClientBonVentModel.updateSelf(
                                        produit,
                                        bonVentValue,
                                        this@setupSimpleDataListener
                                    )

                                    GrossistBonCommandes.calculeSelf(
                                        produit,
                                        this@setupSimpleDataListener
                                    )
                                } else {
                                    Log.w("DataListener", "bonVentValue was null for product ${produit.id}")
                                }
                            } else {
                                Log.d("DataListener", "No bonsVentDeCetteCota node for product ${produit.id}")
                            }

                            // Handle bonCommendDeCetteCota
                            val bonCommandeSnapshot = snapshot.child("bonCommendDeCetteCota")
                            if (bonCommandeSnapshot.exists()) {
                                val bonCommandeValue = bonCommandeSnapshot.getValue(GrossistBonCommandes::class.java)
                                Log.d("DataListener", "Parsed bonCommandeValue: $bonCommandeValue")

                                if (bonCommandeValue != null) {
                                    Log.d("DataListener", "Updating bonCommendDeCetteCota for product ${produit.id}")
                                    produit.bonCommendDeCetteCota = bonCommandeValue
                                } else {
                                    Log.w("DataListener", "bonCommandeValue was null for product ${produit.id}")
                                }
                            } else {
                                Log.d("DataListener", "No bonCommendDeCetteCota node for product ${produit.id}")
                            }

                            // Log final state
                            Log.d("DataListener", """
                                Product state after update:
                                ID: ${produit.id}
                                Name: ${produit.nom}
                                Has bonsVentDeCetteCota: ${produit.bonsVentDeCetteCota.isNotEmpty()}
                                Has bonCommendDeCetteCota: ${produit.bonCommendDeCetteCota != null}
                            """.trimIndent())

                        } catch (e: Exception) {
                            Log.e("DataListener", """
                                Error updating product ${produit.id}
                                Error type: ${e.javaClass.simpleName}
                                Message: ${e.message}
                                Stack trace: ${e.stackTraceToString()}
                            """.trimIndent())
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("DataListener", """
                        Firebase error for product ${produit.id}:
                        Code: ${error.code}
                        Message: ${error.message}
                        Details: ${error.details}
                    """.trimIndent())
                }
            })
    }
}
sealed class BonType<T> {
    class BonVente(val data: _ModelAppsFather.ProduitModel.ClientBonVentModel) : BonType<_ModelAppsFather.ProduitModel.ClientBonVentModel>()
    class BonCommande(val data: _ModelAppsFather.ProduitModel.GrossistBonCommandes) : BonType<ProduitModel.GrossistBonCommandes>()
}
