package Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
 fun Item_2_Menu(
    title: String,
    aCentralFacade: ACentralFacade = koinInject(),
    isLoading: Boolean,
    old_Datas: List<OldDataBase_M1>,
    new_Datas: List<ArticlesBasesStatsTable> = emptyList(),
    onClick_TO_Close_Menu: () -> Unit,
) {
    var safeCountClick by remember { mutableIntStateOf(0) }

    val title_Ac_Securite = when {
        isLoading -> "Chargement..."
        safeCountClick == 0 -> title
        else -> "Es-tu sûr de faire cela ?"
    }

    Card(
        modifier = Modifier
            .getSemanticsTag(old_Datas, "old_Datas")
            .getSemanticsTag(new_Datas, "new_Datas")
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            text = { Text(title_Ac_Securite) },
            onClick = {
                if (isLoading) return@DropdownMenuItem

                if (safeCountClick == 0) {
                    safeCountClick++
                } else {
                    if (new_Datas.isNotEmpty()) {
                        batchFireBaseUpdateArticlesBasesStatsTable(new_Datas)
                    }
                    onClick_TO_Close_Menu()
                }
            }
        )
    }
}
