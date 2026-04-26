package Application5.App

import Application5.App.Repository.DataBaseInitFactory_SeparatedAppsCodingPattern_19Etudiant
import Application5.App.Repository.DataBaseInit_SeparatedDataBasesCodingPattern_M9AppCompt
import Application5.App.Repository.DataBaseInit_SeparatedDataBasesCodingPattern_Z_AppCompt
import Application5.App.Repository.Repo19Etudiant_SeparatedAppsCodingPattern
import Application5.App.Repository.Repo9AppCompt_SeparatedAppsCodingPattern
import Application5.App.Repository.W_DatabaseInitializationManager_SeparatedDataBasesCodingPattern
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Modules.Base.AppDatabase
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Prompt divideur de ce que je veut faire
 * Prototype de séparation d'un Fragment vers une application autonome (Separated App).
 *
 * Ce composable illustre le pattern de migration progressive :
 * au lieu de lancer un Fragment classique depuis l'app principale, on instancie
 * un ViewModel dédié ([A_ViewModel_SeparatedAppsCodingPattern]) qui possède ses propres
 * repositories et sa propre base de données — indépendants de l'ACentralFacade partagée.
 *
 * L'objectif est de pouvoir extraire ce fragment dans un module/app séparé
 * sans toucher au reste de l'application.
 */

@Stable
class ActiveDatas_SeparatedAppsCodingPattern {
    var active_M9Compt: M09AppCompt? by mutableStateOf(null)
}

@SuppressLint("StaticFieldLeak")
class A_ViewModel_SeparatedAppsCodingPattern(
    private val context: Context,
    val appDatabase: AppDatabase,
) : ViewModel() {
    val dataBaseInitFactory_19Etudiant =
        DataBaseInitFactory_SeparatedAppsCodingPattern_19Etudiant(appDatabase)

    // 2. Repo — exposes the reactive list consumed by the UI
    val repo19Etudiant = Repo19Etudiant_SeparatedAppsCodingPattern(
        context = context,
        dataBaseCreationFactory = dataBaseInitFactory_19Etudiant,
    )
    // 2. Repo — exposes the reactive list consumed by the UI
    val dataBaseInit_SeparatedDataBasesCodingPattern_M9AppCompt = DataBaseInit_SeparatedDataBasesCodingPattern_M9AppCompt(
        appDatabase.dao_M9AppCompt()
    )

    // 1. repo9AppCompt — needed by databaseInitializationManager to track overall loading progress
    val repo9AppCompt = Repo9AppCompt_SeparatedAppsCodingPattern(
        context = context,
        dataBaseInit_SeparatedDataBasesCodingPattern_M9AppCompt

    )

    // 3. Initialization manager — orchestrates all repo inits + progress tracking
    val databaseInitializationManager =
        W_DatabaseInitializationManager_SeparatedDataBasesCodingPattern(
            appComptComposeRepositoryPJ17 = repo9AppCompt,
            dataBaseInitZ_AppCompt = DataBaseInit_SeparatedDataBasesCodingPattern_Z_AppCompt(
                appDatabase
            ),
            dataBaseInitFactory_19Etudiant = dataBaseInitFactory_19Etudiant,
        )

    val active_Datas = ActiveDatas_SeparatedAppsCodingPattern()

    init {
        viewModelScope.launch {
            databaseInitializationManager.initializeAllRepositories(context)
        }
    }

    override fun onCleared() {
        super.onCleared()
        databaseInitializationManager.cancel()
    }
}
