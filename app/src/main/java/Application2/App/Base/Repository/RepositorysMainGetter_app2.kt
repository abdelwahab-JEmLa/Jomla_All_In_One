package Application2.App.Base.Repository

import Application2.App.Init.Initializer_Funcs_app2
import EntreApps.Shared.Modules.Base.AppDatabase
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class RepositorysMainGetter_app2(
    private val context: Context,
    appDatabase: AppDatabase,
) {
    val repoScope = CoroutineScope(Dispatchers.IO)
    private val _ActiveCentralValues_app2 = mutableStateOf(
        ActiveCentralValues_app2()
    )
    val active_Central_Values by derivedStateOf { _ActiveCentralValues_app2.value }

    val loadingProgress: Float by derivedStateOf {
        active_Central_Values.mainInitDataBaseProgressEtate
    }

    val dao_M1Produit = appDatabase.dao_M1Produit()
    val dao_16CategorieProduit = appDatabase.dao_16CategorieProduit()
    val dao_M3CouleurProduitInfos = appDatabase.dao_M03CouleurProduitInfos()

    fun update_ActiveCentralValues_app2(new: ActiveCentralValues_app2) {
        _ActiveCentralValues_app2.value = new
    }

    @Suppress("unused")
    private val initializer = Initializer_Funcs_app2(
        context = context,
        on_Progress_Datas = { progress ->
            val current = _ActiveCentralValues_app2.value
            _ActiveCentralValues_app2.value = current.copy(mainInitDataBaseProgressEtate = progress)
        },
        dao_M1Produit,
        dao_16CategorieProduit,
        dao_M3CouleurProduitInfos
    )

    init {
        repoScope.launch {
            initializer.initializeAllRepositories()
        }
    }
}
