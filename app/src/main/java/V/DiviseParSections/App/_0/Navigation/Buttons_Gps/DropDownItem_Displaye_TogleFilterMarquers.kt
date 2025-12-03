package V.DiviseParSections.App._0.Navigation.Buttons_Gps

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent.EtateActuellementEst
import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DropDownItem_3(
    nomFun: String = "Partager PDF",
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val printHandler = aCentralFacade.modulesCentral.printReceiptHandler
    val repositorysMainGetter = aCentralFacade.repositorysMainGetter

    // Récupérer les clients avec commandes confirmées
    val clients_avec_confirmed = repositorysMainGetter.repo2Client.datasValue
        .filter { cli ->
            repositorysMainGetter.repo8BonVent.datasValue.any {
                it.parent_M2Client_KeyID == cli.keyID &&
                        it.etateActuellementEst == EtateActuellementEst.A_COMMANDE_CONFIRME &&
                        it.parent_M14VentPeriod_KeyId == focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID
            }
        }

    val bonVents_OnCommande_ou_Leurclients_avec_confirmed =
        repositorysMainGetter.repo8BonVent.datasValue.filter { bonVent ->
            bonVent.etateActuellementEst == EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT &&
                    bonVent.parent_M14VentPeriod_KeyId == focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID &&
                    clients_avec_confirmed.any { it.keyID == bonVent.parent_M2Client_KeyID }
        }

    /**
     * FIXED TODO(1): Sauvegarde dans /storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne/BonsDeVente/[date]
     * FIXED TODO(2): Ajoute le nombre de pages au nom du fichier selon le nombre de lignes
     */
    fun savePdfToStorage(context: Context, pdfFile: File, clientName: String, productLineCount: Int) {
        try {
            // Créer le chemin personnalisé avec la date du jour
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = dateFormat.format(Date())

            // Chemin : /storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne/BonsDeVente/[date]
            val customPath = File(
                Environment.getExternalStorageDirectory(),
                "Abdelwahab_jeMla.com/IMGs/BaseDonne/BonsDeVente/$currentDate"
            )

            if (!customPath.exists()) {
                customPath.mkdirs()
            }

            // Déterminer le suffixe selon le nombre de lignes de produits
            val pageSuffix = when {
                productLineCount > 36 -> "_3صفحات"  // 3 pages
                productLineCount > 18 -> "_2صفحات"  // 2 pages
                else -> ""
            }

            // Créer le nom de fichier avec le suffixe de pages
            val fileName = "${clientName.replace(" ", "_")}${pageSuffix}.pdf"
            val destinationFile = File(customPath, fileName)

            // Copier le fichier
            FileInputStream(pdfFile).use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    context,
                    "PDF sauvegardé: ${destinationFile.absolutePath}",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    context,
                    "Erreur sauvegarde: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun shareWithWindows() {
        if (bonVents_OnCommande_ou_Leurclients_avec_confirmed.isEmpty()) {
            Toast.makeText(
                context,
                "Aucune commande confirmée à partager",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        isLoading = true
        scope.launch {
            try {
                bonVents_OnCommande_ou_Leurclients_avec_confirmed.forEach { bonVent ->

                    val relative_ListM10OperationVentCouleur =
                        repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter { vent ->
                            vent.parent_M8BonVent_KeyId == bonVent.keyID &&
                                    vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                                    vent.quantity > 0
                        }

                    if (relative_ListM10OperationVentCouleur.isEmpty()) {
                        return@forEach
                    }

                    // Compter le nombre de lignes de produits pour le suffixe
                    val productLineCount = relative_ListM10OperationVentCouleur
                        .groupBy { it.parent_M1Produit_KeyId }
                        .size

                    val client = repositorysMainGetter.repo2Client.datasValue.find {
                        it.keyID == bonVent.parent_M2Client_KeyID
                    }

                    // Générer le PDF
                    val result = printHandler.printPdfOnly(
                        context = context,
                        repo13TarificationInfos = repositorysMainGetter.repo13TarificationInfos,
                        repoM1Produit = repositorysMainGetter.repo1ProduitInfos,
                        repo3CouleurProduitInfos = repositorysMainGetter.repo03CouleurProduitInfos,
                        scope = scope,
                        relative_ListM10OperationVentCouleur = relative_ListM10OperationVentCouleur,
                        relative_bonVent = bonVent,
                        client = client
                    )

                    result.onSuccess { message ->
                        val filePath = message.substringAfter("PDF saved: ").substringBefore("\n")
                        val pdfFile = java.io.File(filePath)

                        if (pdfFile.exists()) {
                            // Sauvegarder avec le nombre de lignes pour déterminer le suffixe
                            savePdfToStorage(
                                context,
                                pdfFile,
                                client?.nom ?: "Client_${bonVent.keyID.takeLast(4)}",
                                productLineCount
                            )

                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(
                                    context,
                                    "PDF sauvegardé: ${client?.nom ?: "Client"}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    result.onFailure { error ->
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                context,
                                "Erreur pour ${client?.nom ?: "client"}: ${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                // Message final de succès
                CoroutineScope(Dispatchers.Main).launch {
                    val savedCount = bonVents_OnCommande_ou_Leurclients_avec_confirmed.size
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val currentDate = dateFormat.format(Date())
                    Toast.makeText(
                        context,
                        "$savedCount PDF(s) sauvegardé(s) dans Abdelwahab_jeMla.com/BonsDeVente/$currentDate",
                        Toast.LENGTH_LONG
                    ).show()
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
            }
        }
    }

    // Calculer le nombre total de ventes pour tous les bons confirmés
    val totalVentes = bonVents_OnCommande_ou_Leurclients_avec_confirmed.sumOf { bonVent ->
        repositorysMainGetter.repo10OperationVentCouleur.datasValue.count { vent ->
            vent.parent_M8BonVent_KeyId == bonVent.keyID &&
                    vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                    vent.quantity > 0
        }
    }

    Card(
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(
                    value = clients_avec_confirmed,
                    key = SemanticsPropertyKey("clients_avec_confirmed")
                )
            }
            .semantics(mergeDescendants = true) {
                set(
                    value = bonVents_OnCommande_ou_Leurclients_avec_confirmed,
                    key = SemanticsPropertyKey("bonVents_confirmed")
                )
            }
            .semantics(mergeDescendants = true) {
                set(
                    value = totalVentes,
                    key = SemanticsPropertyKey("total_ventes")
                )
            }
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
                    text = if (isLoading) {
                        "Partage en cours..."
                    } else {
                        "$nomFun (${bonVents_OnCommande_ou_Leurclients_avec_confirmed.size})"
                    },
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                if (!isLoading) {
                    shareWithWindows()
                }
            },
            enabled = !isLoading && bonVents_OnCommande_ou_Leurclients_avec_confirmed.isNotEmpty()
        )
    }
}
