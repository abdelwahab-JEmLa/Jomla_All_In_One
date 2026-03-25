package EntreApps.Shared.Models.Home

import Application4.App.A.Start.Init.Initializer_App4
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.ifTrue
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Modules.Base.AppDatabase
import android.content.Context
import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun find_ListM3CouleurInfos_By_Parent_Produit_KeyID(
    datas: List<M3CouleurProduitInfos>,
    parentBProduitInfosKeyID: String
) =
    datas.filter { it.parentBProduitInfosKeyID == parentBProduitInfosKeyID }

@Stable
class CentraleMainGetter_NewProtoPattern(
    private val context: Context,
    private val appDatabase: AppDatabase,
    on_Progress_Datas: (Float) -> Unit,
) {
    private val initScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        M00CentralParametresOfAllApps.get_Default().load_Initializer_App4.ifTrue {
            initScope.launch {
                Initializer_App4.initializeAllRepositories(
                    context = context,
                    appDatabase = appDatabase,
                    on_Progress_Datas = on_Progress_Datas,
                    callerScope = initScope,
                )
            }
        }
    }
}
