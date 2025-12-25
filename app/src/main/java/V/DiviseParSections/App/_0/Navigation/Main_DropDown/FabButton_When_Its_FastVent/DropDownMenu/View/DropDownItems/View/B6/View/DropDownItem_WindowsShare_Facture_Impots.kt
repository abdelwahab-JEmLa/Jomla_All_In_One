package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B6.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

// Company header configuration
enum class CompanyHeader(val displayName: String) {
    JOMLA("Jomla.com"),
    BELFORT("Belfort Gros Confisserie")
}

@Composable
fun DropDownItem_ThermiquePrint(
    nomFun: String = "Imprimer Bluetooth",
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    context: Context = LocalContext.current
) {
    var selectedHeader by remember {
        mutableStateOf(
            if (focusedValuesGetter.currentApp_ItsWorkChezGrossisst) CompanyHeader.BELFORT else
                CompanyHeader.JOMLA
        )
    }
    val scope = rememberCoroutineScope()
    val printHandler = aCentralFacade.modulesCentral.printReceiptHandler

    val activeVents = focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter { vent ->
            vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                    vent.quantity > 0
        }

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = nomFun,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    // Company header toggle button
                    FloatingActionButton(
                        onClick = {
                            selectedHeader = if (selectedHeader == CompanyHeader.JOMLA) {
                                CompanyHeader.BELFORT
                            } else {
                                CompanyHeader.JOMLA
                            }
                        },
                        modifier = Modifier.size(32.dp),
                        containerColor = when (selectedHeader) {
                            CompanyHeader.JOMLA -> Color(0xFF4CAF50)
                            CompanyHeader.BELFORT -> Color(0xFFFF9800)
                        },
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = "Changer l'en-tête: ${selectedHeader.displayName}",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                    }
                }
            },
            onClick = {
                if (activeVents.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Aucun article à imprimer",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@DropdownMenuItem
                }

                scope.launch {
                    try {
                        aCentralFacade.repositorysMainSetter.update_M8BonVent(
                            focusedValuesGetter.activeOnVent_M8BonVent?.copy(
                                affiche_le_verssement_au_prochen_print = false
                            )
                        )

                        delay(300)

                        printHandler.printBluetoothOnly(
                            context = context,
                            repo13TarificationInfos = aCentralFacade.repositorysMainGetter.repo13TarificationInfos,
                            repoM1Produit = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
                            repo3CouleurProduitInfos = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
                            client = focusedValuesGetter.activeOnVentM2ClientInfos,
                            scope = scope,
                            relative_ListM10OperationVentCouleur = activeVents,
                            bonVent = focusedValuesGetter.activeOnVent_M8BonVent,
                            companyHeader = selectedHeader.displayName
                        )

                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                context,
                                "Impression Bluetooth lancée",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        onDismissDropdown()

                    } catch (e: Exception) {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                context,
                                "Erreur Bluetooth: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        e.printStackTrace()
                    }
                }
            }
        )
    }
}
