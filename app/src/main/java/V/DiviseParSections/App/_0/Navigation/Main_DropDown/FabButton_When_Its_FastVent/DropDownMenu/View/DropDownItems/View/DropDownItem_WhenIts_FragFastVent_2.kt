package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun DropDownItem_WhenIts_FragFastVent_2(
    nomFun: String,
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    context: Context = LocalContext.current
) {
    var needsConfirmation by remember { mutableStateOf(false) }

    fun updateProductsByPosition() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentData = repositorysMainGetter.repo1ProduitInfos.datasValue

                val sortedData = currentData.sortedWith(
                    compareBy<ArticlesBasesStatsTable> { it.position_store_3jamale }
                        .thenByDescending { it.dernier_timeTamps_position_store_3jamale }
                        .thenBy { it.its_Carton }
                        .thenBy { it.cartonState }
                        .thenByDescending { it.dernierTimeTampsSynchronisationAvecFireBase }
                        .thenBy { it.idParentCategorie }
                        .thenBy { it.nom }
                )

                sortedData.forEachIndexed { index, product ->
                    val updatedProduct = product.copy(
                        position_store_3jamale = index + 1,
                        dernier_timeTamps_position_store_3jamale = System.currentTimeMillis(),
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )

                    repositorysMainSetter.update2_M1Produit(updatedProduct)
                }

                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        context,
                        "Successfully updated ${sortedData.size} confectionery products",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        context,
                        "Error updating products: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                needsConfirmation = false
            }
        }
    }

    Card(
        modifier = Modifier
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
                    imageVector = if (needsConfirmation) Icons.Default.Warning else Icons.Default.FilterList,
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
                    updateProductsByPosition()

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


@Composable
fun DropDownItem_WindowsShare(
    nomFun: String = "Partager PDF",
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val printHandler = aCentralFacade.modulesCentral.printReceiptHandler

    val activeVents = focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter { vent ->
            vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                    vent.quantity > 0
        }

    fun shareWithWindows() {
        if (activeVents.isEmpty()) {
            Toast.makeText(
                context,
                "Aucun article à partager",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        isLoading = true
        scope.launch {
            try {
                aCentralFacade.repositorysMainSetter.update_M8BonVent(focusedValuesGetter.activeOnVent_M8BonVent?.copy(affiche_le_verssement_au_prochen_print = false))

                kotlinx.coroutines.delay(500)

                val result = printHandler.printPdfOnly(
                    context = context,
                    repo13TarificationInfos = aCentralFacade.repositorysMainGetter.repo13TarificationInfos,
                    repoM1Produit = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
                    repo3CouleurProduitInfos = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
                    client = focusedValuesGetter.activeOnVentM2ClientInfos,
                    scope = scope,
                    relative_ListM10OperationVentCouleur = activeVents,
                    relative_bonVent = focusedValuesGetter.activeOnVent_M8BonVent
                )

                result.onSuccess { message ->
                    val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
                    val pdfFile = java.io.File(filePath)
                    if (pdfFile.exists()) {
                        printHandler.sharePdfWithWindowsApps(context, pdfFile)

                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                context,
                                "PDF partagé avec succès",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                result.onFailure { error ->
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            context,
                            "Erreur: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        context,
                        "Erreur lors du partage: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                e.printStackTrace()
            } finally {
                kotlinx.coroutines.delay(2000)
                isLoading = false
                onDismissDropdown()
            }
        }
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLoading) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.padding(4.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            text = {
                Text(
                    text = if (isLoading) "Partage en cours..." else nomFun,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                if (!isLoading) {
                    shareWithWindows()
                }
            },
            enabled = !isLoading
        )
    }
}
