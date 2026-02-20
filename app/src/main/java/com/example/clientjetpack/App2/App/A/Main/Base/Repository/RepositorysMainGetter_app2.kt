package com.example.clientjetpack.App2.App.A.Main.Base.Repository

import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.M16CategorieProduit
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
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
    val dao_M3CouleurProduitInfos = appDatabase.dao_M3CouleurProduitInfos()

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

    private val _datas_M16CategorieProduit = mutableStateOf<List<M16CategorieProduit>>(emptyList())
    val datasValue_M16CategorieProduit by derivedStateOf { _datas_M16CategorieProduit.value }

    private val _datas_M1Produit = mutableStateOf<List<ArticlesBasesStatsTable>>(emptyList())
    val datasValue_M1Produit by derivedStateOf { _datas_M1Produit.value }

    private val _datas_M3CouleurProduitInfos =
        mutableStateOf<List<M3CouleurProduitInfos>>(emptyList())
    val datasValue_M3CouleurProduitInfos by derivedStateOf { _datas_M3CouleurProduitInfos.value }

    init {
        repoScope.launch {
            initializer.initializeAllRepositories()
            dao_16CategorieProduit.getAllFlow().collect { _datas_M16CategorieProduit.value = it }
            dao_M1Produit.getAllFlow().collect { _datas_M1Produit.value = it }
            dao_M3CouleurProduitInfos.getAllFlow()
                .collect { _datas_M3CouleurProduitInfos.value = it }
        }
    }

    companion object {
        val centralRef = Firebase.database.getReference(
            "00_DataPrototype-04-02" + "/_1_developingRef" + "/C_InfosSqlDataBases"
        )

        fun getPushFireBase(ref: DatabaseReference) = ref.push().key.toString()
        inline fun Long?.ifNotNullOrZero(block: () -> Unit) { if (this != null && this != 0L) block() }
        inline fun String?.ifNotNullOrEmpty(block: () -> Unit) { if (!this.isNullOrEmpty()) block() }
        inline fun Boolean.ifTrue(block: () -> Unit) { if (this) block() }
        inline fun Boolean.ifFalse(block: () -> Unit) { if (!this) block() }
        fun String?.empty_If_Null(value: String = ""): String { return this ?: value }
    }
}
