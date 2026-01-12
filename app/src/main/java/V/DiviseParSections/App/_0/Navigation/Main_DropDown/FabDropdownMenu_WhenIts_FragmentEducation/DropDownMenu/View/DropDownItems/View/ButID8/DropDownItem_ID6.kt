package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Utilisateur
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.Repo20ObsarvationEtudion
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import org.koin.compose.koinInject

@Composable
fun DropDownItem_ButID8(
    nomFun: String = "Toggler_Affich_Mois_Tahfid",
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    repo20Observation: Repo20ObsarvationEtudion = aCentralFacade.repositorysMainGetter.repo20ObsarvationEtudion,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    context: Context = LocalContext.current
) {     //<--
//TODO(1): fait au click de toggle active . displaye_dialog_mois_moinAcPlus_6_du_current

    var isLoading by remember { mutableStateOf(false) }
    var generationStatus by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val currentUtilisateur = remember(focusedValuesGetter.active_Central_Values) {
        focusedValuesGetter.active_Central_Values.active_filter_du_utilisateur
            ?: Utilisateur.Admin
    }

    val activeStudentsCount = remember(repo19Etudiant.datasValue) {
        repo19Etudiant.datasValue.count { !it.exclue_de_l_affiche_au_classe }
    }
}
