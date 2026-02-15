package V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.M16CategorieProduit
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun DropDownItemWBaseDonne_1(
    nomFun: String,
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    context: Context = LocalContext.current
) {
    val  datas =repositorysMainGetter.repoM16CategorieProduit.datasValue
    var needsConfirmation by remember { mutableStateOf(false) }

    fun update_return(): List<M16CategorieProduit> {
        // Group categories by catalogue
        val categoriesByCatalogue = datas.groupBy { it.catalogueParentId }

        val updatedCategories = mutableListOf<M16CategorieProduit>()

        categoriesByCatalogue.forEach { (catalogueId, categories) ->
            // Sort categories by positionDouble, then by dernierTimeTampsSynchronisationAvecFireBase
            val sortedCategories = categories.sortedWith(
                compareBy<M16CategorieProduit> { it.positionDouble }
                    .thenByDescending { it.dernierTimeTampsSynchronisationAvecFireBase }
            )

            // Update positionDouble with incremental index + 1
            sortedCategories.forEachIndexed { index, category ->
                val updatedCategory = category.copy(
                    positionDouble = (index + 1).toDouble()
                )
                updatedCategories.add(updatedCategory)
            }
        }

        return updatedCategories
    }
    val filter = update_return()
        .filter { it.catalogueParentId == 2L }
    Card(
        modifier = Modifier
            .semantics(mergeDescendants = true) {

                set(value = filter
                    .map {
                    it.nom + " " +it.positionDouble
                }, key = SemanticsPropertyKey(""))
            }
            .semantics(mergeDescendants = true) {
                set(value = filter, key = SemanticsPropertyKey("update_return"))
            }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (needsConfirmation) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = if (needsConfirmation) Icons.Default.Warning else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (needsConfirmation) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            },
            text = {
                Text(
                    text = if (needsConfirmation) "Êtes-vous sûr?" else nomFun,
                    color = if (needsConfirmation) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            },
            onClick = {
                if (!needsConfirmation) {
                    needsConfirmation = true
                } else {
                    repositorysMainSetter.addOrUpdateDatas_M16CategorieProduit(update_return())

                    Toast.makeText(
                        context,
                        "Fonction '$nomFun' exécutée avec succès",
                        Toast.LENGTH_SHORT
                    ).show()

                    onDismissDropdown()
                }
            }
        )
    }
}
