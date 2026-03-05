package EntreApps.Shared.Models.Home

import Application4.App.A.Start.Init.Initializer_Funcs_NewProtoPattern
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Modules.AppDatabase
import android.content.Context
import androidx.compose.runtime.Stable
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun find_ListM3CouleurInfos_By_Parent_Produit_KeyID(datas : List<M3CouleurProduitInfos>, parentBProduitInfosKeyID: String) =
    datas.filter { it.parentBProduitInfosKeyID == parentBProduitInfosKeyID }

@Stable
class RepositorysMainGetter_NewProtoPattern(
    private val context: Context,
    appDatabase: AppDatabase,
    on_Progress_Datas: (Float) -> Unit,
) {
    val repoScope = CoroutineScope(Dispatchers.IO)
    val dao_M1Produit = appDatabase.dao_M1Produit()
    val dao_16CategorieProduit = appDatabase.dao_16CategorieProduit()
    val dao_M3CouleurProduitInfos = appDatabase.dao_M3CouleurProduitInfos()
    val dao_M13TarificationInfos = appDatabase.dao_M13TarificationInfos()
    val dao_M14VentPeriode = appDatabase.dao_M14VentPeriode()
    val dao_M8BonVent = appDatabase.dao_M8BonVent()
    val dao_M10OperationVentCouleur = appDatabase.dao_M10OperationVentCouleur()
    val dao_M9AppCompt = appDatabase.dao_M9AppCompt()


    @Suppress("unused")
    private val initializer = Initializer_Funcs_NewProtoPattern(
        context = context,
        on_Progress_Datas = { progress ->
            on_Progress_Datas(progress)
        },
        dao_M1Produit,
        dao_16CategorieProduit,
        dao_M3CouleurProduitInfos,
        dao_M13TarificationInfos,
        dao_M14VentPeriode,
        dao_M8BonVent,
        dao_M10OperationVentCouleur,
        dao_M9AppCompt,
    )

    init {
        repoScope.launch {
            initializer.initializeAllRepositories()
        }
    }

    companion object {
        val centralRef = Firebase.database.getReference(
            "00_DataPrototype-04-02" + "/_1_developingRef" + "/C_InfosSqlDataBases"
        )

        const val images_central_Local_storageLink = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"


        fun getPushFireBase(ref: DatabaseReference) = ref.push().key.toString()
        inline fun Long?.ifNotNullOrZero(block: () -> Unit) { if (this != null && this != 0L) block() }
        inline fun String?.ifNotNullOrEmpty(block: () -> Unit) { if (!this.isNullOrEmpty()) block() }
        inline fun Boolean.ifTrue(block: () -> Unit) { if (this) block() }
        inline fun Boolean.ifFalse(block: () -> Unit) { if (!this) block() }
        fun String?.empty_If_Null(value: String = ""): String { return this ?: value }
    }
}
