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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
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

    val relative_BonVent = focusedValuesGetter.activeOnVent_M8BonVent
    val totalBon = relative_BonVent?.sum_De_Totale_Vents ?: 0.0

    fun shareWithCreditInfo(versement: Double) {
        if (activeVents.isEmpty()) {
            Toast.makeText(
                context,
                "Aucun article à partager",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (relative_BonVent == null) {
            Toast.makeText(
                context,
                "Bon de vente introuvable",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        isLoading = true
        scope.launch {
            try {
                val credit = totalBon - versement

                val updatedBonVent = relative_BonVent.copy(
                    versement_fait = versement,       // Le versement fait
                    versement = versement,             // Alternativefield
                    credit_fait = credit,              // Le crédit calculé
                    affiche_le_verssement_au_prochen_print = true,  // FLAG IMPORTANT
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )

                // Mettre à jour dans le repository AVANT de générer le PDF
                aCentralFacade.repositorysMainSetter.update_M8BonVent(updatedBonVent)

                // Attendre que la mise à jour soit complète
                kotlinx.coroutines.delay(500)

                val result = printHandler.printPdfOnly(
                    context = context,
                    repo13TarificationInfos = aCentralFacade.repositorysMainGetter.repo13TarificationInfos,
                    repoM1Produit = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
                    repo3CouleurProduitInfos = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
                    client = focusedValuesGetter.activeOnVentM2ClientInfos,
                    scope = scope,
                    showCreditSection = true,
                    relative_ListM10OperationVentCouleur = activeVents,
                    bonVent = updatedBonVent  // Passer le bonVent mis à jour
                )

                result.onSuccess { message ->
                    val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
                    val pdfFile = java.io.File(filePath)

                    if (pdfFile.exists()) {
                        // Partager le PDF avec les apps Windows
                        printHandler.sharePdfWithWindowsApps(context, pdfFile)

                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                context,
                                "PDF avec crédit partagé avec succès\n" +
                                        "Total: ${String.format("%.1f", totalBon)}Da\n" +
                                        "Versement: ${String.format("%.1f", versement)}Da\n" +
                                        "Crédit: ${String.format("%.1f", credit)}Da",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                context,
                                "Erreur: Fichier PDF non trouvé",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                result.onFailure { error ->
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            context,
                            "Erreur lors de la génération: ${error.message}",
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
                kotlinx.coroutines.delay(1000)
                isLoading = false
                showCreditInput = false
                versementText = ""
                onDismissDropdown()
            }
        }
    }

    Card(
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(value = relative_BonVent, key = SemanticsPropertyKey("relative_BonVent"))
            }
            .padding(horizontal = 8.dp, vertical = 4.dp),
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
        Column(modifier = Modifier.padding(4.dp)) {
            if (showCreditInput) {
                OutlinedTextField(
                    value = versementText,
                    onValueChange = { versementText = it },
                    label = { Text("Versement (Da)") },
                    placeholder = { Text("0.0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.padding(8.dp),
                    enabled = !isLoading,
                    supportingText = {
                        val versement = versementText.toDoubleOrNull() ?: 0.0
                        val credit = totalBon - versement
                        Text(
                            text = "Total: ${String.format("%.1f", totalBon)}Da | " +
                                    "Crédit: ${String.format("%.1f", credit)}Da",
                            color = if (credit < 0) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    },
                    isError = (versementText.toDoubleOrNull() ?: 0.0) > totalBon
                )

                // Bouton de confirmation
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
                            text = if (isLoading) "Génération en cours..." else "Confirmer et Partager",
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
                                    "Veuillez entrer un montant valide",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    enabled = !isLoading && versementText.toDoubleOrNull() != null
                )
            } else {
                // Bouton initial pour afficher le champ de saisie
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
