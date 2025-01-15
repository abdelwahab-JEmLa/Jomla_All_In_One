package Z_MasterOfApps.Kotlin.ViewModel.Extensions

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather


/*
import com.example.Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import com.example.Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import com.example.Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import com.example.Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch


// In setupSimpleDataListener.kt
fun ViewModelInitApp.setupSimpleDataListener() {
_modelAppsFather.produitsMainDataBase.forEach { produit ->
  Log.d("SetupListener", "Setting up listener for product ${produit.id}")
  produitsFireBaseRef.child(produit.id.toString())
      .addValueEventListener(object : ValueEventListener {
          override fun onDataChange(snapshot: DataSnapshot) {
              viewModelScope.launch {
                  try {
                      Log.d("SetupListener", "Data changed for product ${produit.id}")

                      // Check and create bon vent if needed
                      if (!snapshot.hasChild("bonsVentDeCetteCota")) {
                          Log.d("SetupListener", "Creating default bonsVentDeCetteCota for product ${produit.id}")
                          val defaultBonVent = _ModelAppsFather.ProduitModel.ClientBonVentModel(
                              vid = System.currentTimeMillis(),
                              init_clientInformations = _ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations(
                                  id = System.currentTimeMillis(),
                                  nom = "Client Initial",
                                  couleur = "#000000"
                              ),
                              init_colours_achete = emptyList()
                          )
                          produitsFireBaseRef.child(produit.id.toString())
                              .child("bonsVentDeCetteCota")
                              .setValue(defaultBonVent)
                              .addOnSuccessListener {
                                  Log.d("SetupListener", "Default bonsVentDeCetteCota created successfully")
                              }
                              .addOnFailureListener { e ->
                                  Log.e("SetupListener", "Failed to create default bonsVentDeCetteCota", e)
                              }
                      }

                      snapshot.child("bonsVentDeCetteCota")
                          .getValue(_ModelAppsFather.ProduitModel.ClientBonVentModel::class.java)
                          ?.let { newBonVent ->
                              Log.d("SetupListener", "Processing bon vent for product ${produit.id}")
                              produit.bonsVentDeCetteCota.clear()
                              produit.bonsVentDeCetteCota.add(newBonVent)

                              // Check and create bon commande if needed
                              if (!snapshot.hasChild("bonCommendDeCetteCota")) {
                                  Log.d("SetupListener", "Creating default bonCommendDeCetteCota")
                                  val defaultBonCommande = _ModelAppsFather.ProduitModel.GrossistBonCommandes().apply {
                                      vid = System.currentTimeMillis()
                                      grossistInformations = _ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations(
                                          id = vid,
                                          nom = "Grossist Initial",
                                          couleur = "#000000"
                                      )
                                      date = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                                          .format(java.util.Date())
                                      date_String_Divise = date
                                      time_String_Divise = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                                          .format(java.util.Date())
                                  }
                                  produitsFireBaseRef.child(produit.id.toString())
                                      .child("bonCommendDeCetteCota")
                                      .setValue(defaultBonCommande)
                                      .addOnSuccessListener {
                                          Log.d("SetupListener", "Default bonCommendDeCetteCota created successfully")
                                      }
                                      .addOnFailureListener { e ->
                                          Log.e("SetupListener", "Failed to create default bonCommendDeCetteCota", e)
                                      }
                              }

                              Log.d("SetupListener", "Calling calculeSelf for product ${produit.id}")
                              ProduitModel.GrossistBonCommandes.calculeSelf(
                                  produit,
                                  this@setupSimpleDataListener
                              )
                          }

                      // Update bon de commande
                      snapshot.child("bonCommendDeCetteCota")
                          .getValue(_ModelAppsFather.ProduitModel.GrossistBonCommandes::class.java)
                          ?.let { newBonCommande ->
                              Log.d("SetupListener", "Updating bon commande for product ${produit.id}")
                              produit.bonCommendDeCetteCota = newBonCommande
                          }

                  } catch (e: Exception) {
                      Log.e("SetupListener", "Update failed for ${produit.id}", e)
                      e.printStackTrace()
                  }
              }
          }

          override fun onCancelled(error: DatabaseError) {
              Log.e("SetupListener", "Firebase error for product ${produit.id}: ${error.message}")
          }
      })
}
}
*/
sealed class BonType<T> {
    class BonVente(val data: _ModelAppsFather.ProduitModel.ClientBonVentModel) : BonType<_ModelAppsFather.ProduitModel.ClientBonVentModel>()
    class BonCommande(val data: _ModelAppsFather.ProduitModel.GrossistBonCommandes) : BonType<_ModelAppsFather.ProduitModel.GrossistBonCommandes>()
}
