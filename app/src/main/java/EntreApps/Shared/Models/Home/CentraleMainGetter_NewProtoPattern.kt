package EntreApps.Shared.Models.Home

import Application4.App.Archive.Archive.Initializer_App4
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Modules.Base.AppDatabase
import android.content.Context
import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun find_ListM3CouleurInfos_By_Parent_Produit_KeyID(datas: List<M3CouleurProduitInfos>, parentBProduitInfosKeyID: String) =
    datas.filter { it.parentBProduitInfosKeyID == parentBProduitInfosKeyID }

@Stable
class CentraleMainGetter_NewProtoPattern(
    private val context: Context,
    private val appDatabase: AppDatabase,
    on_Progress_Datas: (Float) -> Unit,
) {
    val dao_M1Produit               = appDatabase.dao_M1Produit()
    val dao_16CategorieProduit      = appDatabase.dao_16CategorieProduit()
    val dao_M3CouleurProduitInfos   = appDatabase.dao_M03CouleurProduitInfos()
    val dao_M13TarificationInfos    = appDatabase.dao_M13TarificationInfos()
    val dao_M14VentPeriode          = appDatabase.dao_M14VentPeriode()
    val dao_M8BonVent               = appDatabase.dao_M8BonVent()
    val dao_M10OperationVentCouleur = appDatabase.dao_M10OperationVentCouleur()
    val dao_M9AppCompt              = appDatabase.dao_M9AppCompt()

    // Scope dédié uniquement à l'init — annulé dès que initializeAllRepositories() termine.
    // Le VM n'en sait rien, aucune ressource tenue après la fin du progress.
    private val initScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        initScope.launch {
            Initializer_App4.initializeAllRepositories(
                context                     = context,
                appDatabase                 = appDatabase,
                on_Progress_Datas           = on_Progress_Datas,
                dao_M1Produit               = dao_M1Produit,
                dao_16CategorieProduit      = dao_16CategorieProduit,
                dao_M03CouleurProduitInfos  = dao_M3CouleurProduitInfos,
                dao_M13TarificationInfos    = dao_M13TarificationInfos,
                dao_M14VentPeriode          = dao_M14VentPeriode,
                dao_M8BonVent               = dao_M8BonVent,
                dao_M10OperationVentCouleur = dao_M10OperationVentCouleur,
                dao_M9AppCompt              = dao_M9AppCompt,
                callerScope= initScope,
            )
        }
    }
}
