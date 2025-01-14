package Y_AppsFather.Kotlin.ViewModel.Extensions

import Y_AppsFather.Kotlin.Model._ModelAppsFather
import Y_AppsFather.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Y_AppsFather.Kotlin.ViewModel.BonType
import Y_AppsFather.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

fun ViewModelInitApp.setupSimpleDataListener() {
        _modelAppsFather.produitsMainDataBase.forEach { produit ->
            produitsFireBaseRef.child(produit.id.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        viewModelScope.launch {
                            try {
                                // Update bon de commande
                                snapshot.child("bonCommendDeCetteCota")
                                    .getValue(_ModelAppsFather.ProduitModel.GrossistBonCommandes::class.java)
                                    ?.let { newBonCommande ->
                                        _bonCommandeFlow.value = newBonCommande
                                        _bonTypeFlow.value = BonType.BonCommande(newBonCommande)
                                    }

                                // Update bon de commande
                                snapshot.child("bonCommendDeCetteCota")
                                    .getValue(_ModelAppsFather.ProduitModel.GrossistBonCommandes::class.java)
                                    ?.let { newBonCommande ->
                                        _bonCommandeFlow.value = newBonCommande
                                        _bonTypeFlow.value = BonType.BonCommande(newBonCommande)
                                    }

                            } catch (e: Exception) {
                                Log.e("ViewModelInitApp", "Update failed for ${produit.id}", e)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ViewModelInitApp", "Firebase error: ${error.message}")
                    }
                })
        }
    }
