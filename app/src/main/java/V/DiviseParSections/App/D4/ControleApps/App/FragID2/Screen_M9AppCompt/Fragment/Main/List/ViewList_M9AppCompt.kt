package V.DiviseParSections.App.D4.ControleApps.App.FragID2.Screen_M9AppCompt.Fragment.Main.List

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.SectionDivider
import V.DiviseParSections.App.D4.ControleApps.App.FragID2.Screen_M9AppCompt.Fragment.Main.List.Z.View.View_M9AppCompt
import V.DiviseParSections.App.D4.ControleApps.App.FragID2.Screen_M9AppCompt.Fragment.ViewModel_M9AppCompt
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Repo18CentralParametresOfAllApps
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun ViewList_M9AppCompt(
    viewModel: ViewModel_M9AppCompt,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repo18CentralParametresOfAllApps: Repo18CentralParametresOfAllApps = aCentralFacade.repositorysMainGetter.repo18CentralParametresOfAllApps,
) {
    val list_M9AppCompt = viewModel.aCentralFacade.repositorysMainGetter.repo9AppCompt.datasValue
    val dataValue = repo18CentralParametresOfAllApps.dataValue

    val au_Lence_Set_Compt_Ac_KeyId =
        dataValue
            ?.au_Lence_Set_Compt_Ac_KeyId

    val currentActive_AppCompt = focusedValuesGetter.currentActive_M9AppCompt

    LazyColumn(
        modifier = Modifier
            .getSemanticsTag(currentActive_AppCompt, "currentActive_AppCompt")
            .getSemanticsTag(au_Lence_Set_Compt_Ac_KeyId, "au_Lence_Set_Compt_Ac_KeyId")
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            SectionDivider(color = Color.Red)

            Text(
                text = "_M9AppCompt",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Count: ${list_M9AppCompt.size}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            SectionDivider()
        }

        if (list_M9AppCompt.isNotEmpty()) {
            items(list_M9AppCompt) {
                View_M9AppCompt(
                    viewModel = viewModel,
                    relative_M9AppCompt = it,
                )
            }
        } else {
            item {
                Text(
                    text = "Aucune data trouvée",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

    }
}
