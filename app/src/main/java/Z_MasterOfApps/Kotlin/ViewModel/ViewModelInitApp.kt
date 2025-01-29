package Z_MasterOfApps.Kotlin.ViewModel

import Z_MasterOfApps.Kotlin.Model.ClientsDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.Init.Init.LoadFromFirebaseProduits
import Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.ViewModel.Startup_Extension
import Z_MasterOfApps.Z.Android.Packages._1.GerantAfficheurGrossistCommend.App.NH_2.id1_GerantDefinirePosition.ViewModel.Extension.ViewModelExtension_App1_F1
import Z_MasterOfApps.Z.Android.Packages._1.GerantAfficheurGrossistCommend.App.NH_3.id2_TravaillieurListProduitAchercheChezLeGrossist.ViewModel.Extension.ViewModelExtension_App1_F2
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.ParamatersAppsModel
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.CreeDepuitAncienDataBases
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
class ViewModelInitApp : ViewModel() {
    var _paramatersAppsViewModelModel by mutableStateOf(ParamatersAppsModel())
    var _modelAppsFather by mutableStateOf(_ModelAppsFather())

    val modelAppsFather: _ModelAppsFather get() = _modelAppsFather
    val produitsMainDataBase = _modelAppsFather.produitsMainDataBase

    val clientDataBaseSnapList = _modelAppsFather.clientDataBaseSnapList

    var isLoading by mutableStateOf(false)
    var loadingProgress by mutableFloatStateOf(0f)

    val extentionStartup = Startup_Extension(this@ViewModelInitApp)

    val extension_App1_F1 = ViewModelExtension_App1_F1(
        viewModel = this@ViewModelInitApp,
        produitsMainDataBase = produitsMainDataBase,
        viewModelScope = this@ViewModelInitApp.viewModelScope,
    )
    val extension_App1_F2 = ViewModelExtension_App1_F2(
        viewModel = this@ViewModelInitApp,
        produitsMainDataBase = produitsMainDataBase,
        viewModelScope = this@ViewModelInitApp.viewModelScope,
    )

    init {
        viewModelScope.launch {
            try {
                isLoading = true
                val nombre = 0
                if (nombre == 0) {
                    LoadFromFirebaseProduits.loadFromFirebase(this@ViewModelInitApp)
                } else {
                    CreeDepuitAncienDataBases(
                        _modelAppsFather,
                        this@ViewModelInitApp
                    )
                }

                isLoading = false
            } catch (e: Exception) {
                Log.e("ViewModelInitApp", "Init failed", e)
                isLoading = false
            }
        }
    }

    // In ViewModelInitApp.kt, modify setupRealtimeListeners
    fun setupRealtimeListeners(viewModel: ViewModelInitApp) {
        val scope = CoroutineScope(Dispatchers.IO)

        // Products listener
        _ModelAppsFather.produitsFireBaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scope.launch {
                    val products = snapshot.children.mapNotNull {
                        LoadFromFirebaseProduits.parseProduct(it)
                    }
                    viewModel.modelAppsFather.produitsMainDataBase.apply {
                        clear()
                        addAll(products)
                    }
                    Log.d("Firebase", "Real-time products updated: ${products.size} items")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Products listener cancelled: ${error.message}")
            }
        })

        // Clients listener
        // Modified Clients listener
        ClientsDataBase.refClientsDataBase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scope.launch {
                    try {
                        val clients = snapshot.children.mapNotNull { childSnapshot ->
                            // Add explicit key handling
                            val client = LoadFromFirebaseProduits.parseClient(childSnapshot)
                            client?.let {
                                it.copy(id = childSnapshot.key?.toLongOrNull() ?: it.id)
                            }
                        }

                        viewModel.modelAppsFather.clientDataBaseSnapList.apply {
                            clear()
                            addAll(clients)
                        }
                        Log.d("Firebase", "Real-time clients updated: ${clients.size} items")
                    } catch (e: Exception) {
                        Log.e("Firebase", "Failed to parse clients", e)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Clients listener cancelled: ${error.message}")
            }
        })
    }
}
