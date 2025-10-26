package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun DropDownItem_WindowsShare_WithCredit(
    nomFun: String = "Partager PDF Crédit",
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    var showCreditInput by remember { mutableStateOf(false) }
    var versementText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val printHandler = aCentralFacade.modulesCentral.printReceiptHandler

    val activeVents = focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter { vent ->
            vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                    vent.quantity > 0
        }

    val currentBonVent = focusedValuesGetter.activeOnVent_M8BonVent
    val totalBon = currentBonVent?.sum_De_Totale_Vents ?: 0.0

    fun shareWithCreditInfo(versement: Double) {
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
                // Calculate credit
                val credit = totalBon - versement

                // Update bonVent with versement and credit info
                currentBonVent?.let { bonVent ->
                    val updatedBonVent = bonVent.copy(
                        versement_fait = versement,
                        credit_fait = credit,
                        affiche_le_verssement_au_prochen_print = true,
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )
                    
                    // Update in repository
                    aCentralFacade.repositorysMainSetter.update_M8BonVent(updatedBonVent)
                }

                val result = printHandler.printPdfOnly(
                    context = context,
                    repo13TarificationInfos = aCentralFacade.repositorysMainGetter.repo13TarificationInfos,
                    repoM1Produit = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
                    repo3CouleurProduitInfos = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
                    client = focusedValuesGetter.activeOnVentM2ClientInfos,
                    scope = scope,
                    relative_ListM10OperationVentCouleur = activeVents,
                    bonVent = currentBonVent?.copy(
                        versement_fait = versement,
                        credit_fait = credit,
                        affiche_le_verssement_au_prochen_print = true
                    )
                )

                result.onSuccess { message ->
                    val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
                    val pdfFile = java.io.File(filePath)
                    if (pdfFile.exists()) {
                        printHandler.sharePdfWithWindowsApps(context, pdfFile)

                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                context,
                                "PDF avec crédit partagé avec succès\nVersement: ${versement}Da\nCrédit: ${credit}Da",
                                Toast.LENGTH_LONG
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
                showCreditInput = false
                versementText = ""
                onDismissDropdown()
            }
        }
    }

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLoading) {
                MaterialTheme.colorScheme.secondaryContainer
            } else if (showCreditInput) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            if (showCreditInput) {
                // Show input field for versement
                OutlinedTextField(
                    value = versementText,
                    onValueChange = { versementText = it },
                    label = { Text("Versement (Da)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.padding(8.dp),
                    enabled = !isLoading,
                    supportingText = {
                        val versement = versementText.toDoubleOrNull() ?: 0.0
                        val credit = totalBon - versement
                        Text("Total: ${totalBon}Da | Crédit: ${credit}Da")
                    }
                )

                // Confirm button
                DropdownMenuItem(
                    leadingIcon = {
                        if (isLoading) {
                            CircularProgressIndicator(
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
                            text = if (isLoading) "Partage en cours..." else "Confirmer",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        if (!isLoading) {
                            val versement = versementText.toDoubleOrNull()
                            if (versement != null && versement >= 0) {
                                shareWithCreditInfo(versement)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Montant invalide",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    enabled = !isLoading
                )
            } else {
                // Initial button to show input
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    text = {
                        Text(
                            text = nomFun,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        showCreditInput = true
                    }
                )
            }
        }
    }
}
