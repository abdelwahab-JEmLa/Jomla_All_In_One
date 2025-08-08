package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Bottons.View.ButtonAutreEtates
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandler_Juil
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import Z_CodePartageEntreApps.Modules.DatesHandler
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Button_De_supprime_Avec_Securite(
    bon_Vent: M8BonVent,
    machina_li_t_supprime: Repo8BonVent,
) {
    IconButton(
        onClick = {
            // Implementation for secure deletion
        },
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun View_MainItem(
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = viewModel.aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = viewModel.aCentralFacade.repositorysMainSetter,
    fragmentNavigationHandler: FragmentNavigationHandler = aCentralFacade.modulesCentral.fragmentNavigationHandler,
    printReceiptHandler: PrintReceiptHandler_Juil = aCentralFacade.modulesCentral.printReceiptHandler,
    relative_M8BonVent: M8BonVent,
) {
    val activeCentralValues by remember { derivedStateOf { focusedValuesGetter.active_Central_Values } }
    val relative_M17Message =
        repositorysMainGetter.find_By_KeyID_M17MessageVocale(relative_M8BonVent.parent_M17Message_KeyID)
    val relative_Client =
        repositorysMainGetter.find_M2Client(relative_M8BonVent.parent_M2Client_KeyID)

    val hasVoiceMessage =
        relative_M17Message?.nomDeSonOriginaleFichie != null && relative_M17Message.nomDeSonOriginaleFichie != "null"
    val audioRecorderAndPlayHandler = viewModel.audioRecorderAndPlayHandler
    val datesHandler = DatesHandler()
    val etateActuellementEst = relative_M8BonVent.etateActuellementEst
    val activeM8BonVentId =
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeonVent_M8BonVent?.vid
    val blinkState = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val playbackProgress by audioRecorderAndPlayHandler.playbackProgress.collectAsState()

    // State for credit payment dialog
    var showCreditDialog by remember { mutableStateOf(false) }
    var targeted_M8Transaction_Pour_Credit by remember { mutableStateOf<M8BonVent?>(null) }

    // Fixed: Properly observe the StateFlow for repo17MessageVocale data
    val repo17MessageVocaleData by aCentralFacade.repositorysMainGetter.repo17MessageVocale.datasValue.collectAsState()

    val isCurrentlyPlaying = remember(
        playbackProgress.isPlaying,
        audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID
    ) {
        audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID == relative_M8BonVent.vid && playbackProgress.isPlaying
    }

    val isCurrentlyDownloading = remember(
        playbackProgress.isDownloading,
        audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID
    ) {
        audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID == relative_M8BonVent.vid && playbackProgress.isDownloading
    }

    val sumBonVents by remember {
        derivedStateOf {
            get_sum_Bon_Vents(repositorysMainGetter, relative_M8BonVent)
        }
    }

    LaunchedEffect(relative_M8BonVent.vid, isCurrentlyPlaying) {
        if (isCurrentlyPlaying) {
            try {
                while (isCurrentlyPlaying && audioRecorderAndPlayHandler.isPlaying()) {
                    audioRecorderAndPlayHandler.updatePlaybackProgress()
                    delay(100)
                    if (audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID != relative_M8BonVent.vid) {
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    DisposableEffect(relative_M8BonVent.vid) {
        onDispose {
            try {
                val currentSession = audioRecorderAndPlayHandler.getCurrentPlaybackSession()
                if (currentSession?.parentMessageVID == relative_M8BonVent.vid) {
                    audioRecorderAndPlayHandler.stopPlayback()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    if (etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
        && activeM8BonVentId != relative_M8BonVent.vid &&
        !M18CentralParametresOfAllApps.get_Default().itsDevMode
    ) {
        LaunchedEffect(key1 = Unit) {
            while (true) {
                blinkState.value = !blinkState.value
                delay(500)
            }
        }
    }

    Card(
        modifier = Modifier
            .getSemanticsTag(relative_M17Message, "")
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = etateActuellementEst.color)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .getSemanticsTag(relative_M17Message, "relative_M17Message")
                .getSemanticsTag(relative_M8BonVent, "relative_M8BonVent")
                .getSemanticsTag(
                    repo17MessageVocaleData,
                    "repo17MessageVocale"
                )
                .getSemanticsTag(
                    repo17MessageVocaleData
                        .sortedByDescending { it.creationTimestamps }
                        .map { it.getDebugInfos() },
                    "repo17MessageVocale_mapped"
                )
                .getSemanticsTag(
                    activeCentralValues,
                    "activeCentralValues"
                )
                .fillMaxWidth()
        ) {
            // Top row with delete button and sum card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Delete button
                IconButton(
                    onClick = {
                        viewModel.aCentralFacade.repositorysMainSetter.delete_M8BonVent(
                            relative_M8BonVent
                        )

                        val audioKeyToDelete = if (hasVoiceMessage) {
                            relative_M17Message?.nomDeSonOriginaleFichie ?: ""
                        } else {
                            relative_M8BonVent.vocaleKeyID
                        }

                        viewModel.deleteVoiceRecordingFromStorage(audioKeyToDelete) { success ->
                            if (success) {
                                viewModel.getter.repo8BonVent.delete(relative_M8BonVent)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Erreur lors de la suppression du message vocal",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                // CORRECTION 1: Une seule section pour les boutons crédit et print
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Bouton crédit - Une seule fois
                    if (etateActuellementEst == M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI) {
                        M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit.ButtonAutreEtates(
                            clickedClient = relative_Client?.id ?: 0L,
                            onClick = { bonVent ->
                                targeted_M8Transaction_Pour_Credit = bonVent
                                showCreditDialog = true
                            }
                        )
                    }

                    // Print Credit Button
                    if (etateActuellementEst == M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit ||
                        etateActuellementEst == M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI
                    ) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    printReceiptHandler.print_Credit(
                                        context = context,
                                        client = relative_Client,
                                        bonVent = relative_M8BonVent,
                                        scope = coroutineScope
                                    )

                                    Toast.makeText(
                                        context,
                                        "طباعة إيصال الدفع - Credit Receipt Printed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = "Print Credit Receipt",
                                tint = Color.White
                            )
                        }
                    }
                }

                // CORRECTION 2: Card avec hauteur réduite
                if (sumBonVents > 0.0) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (etateActuellementEst == M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit) {
                            // VERSION COMPACTE - Une seule Row au lieu de Column
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Montant restant (principal)
                                Text(
                                    text = String.format("%.2f", sumBonVents - relative_M8BonVent.versement),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sumBonVents - relative_M8BonVent.versement > 0) {
                                        Color.Red.copy(alpha = 0.9f)
                                    } else {
                                        Color.Green.copy(alpha = 0.9f)
                                    }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "دج متبقي",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Séparateur
                                Text(
                                    text = "|",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.6f)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Montant payé (compact)
                                Text(
                                    text = String.format("%.2f", relative_M8BonVent.versement),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "مدفوع",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format("%.2f", sumBonVents),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "دج",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .padding(top = 56.dp)
            ) {
                // Transaction info row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT) {
                        IconButton(
                            onClick = {
                                aCentralFacade.focusedActiveValuesFacade
                                    .focusedValuesSetter
                                    .setIN_M9CurrentApp_onVentM8BonVentKey(
                                        relative_M8BonVent
                                    )

                                fragmentNavigationHandler.navigateToCartScreen()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Select Transaction",
                                tint = if (activeM8BonVentId == relative_M8BonVent.vid) {
                                    Color.White
                                } else {
                                    if (blinkState.value) Color.Red else Color.Gray
                                }
                            )
                        }
                    }

                    Text(
                        text = " الوقت: ${
                            datesHandler.getDateAndTimStringAvecSeconds(
                                relative_M8BonVent.creationTimestamps
                            ).time
                        }",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = etateActuellementEst.nomArabe,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        color = Color.White
                    )

                    Text(
                        text = " --",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        color = Color.White
                    )

                    Text(
                        text = relative_M8BonVent.keyID.takeLast(4),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        color = Color.White,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // CORRECTION 1: SUPPRESSION de la section dupliquée des boutons crédit
                // Cette section était en double, elle est maintenant supprimée

                if (relative_M8BonVent.sum_De_Totale_Vents > 0.0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "المجموع: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = String.format("%.2f دج", relative_M8BonVent.sum_De_Totale_Vents),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Show credit amount if there's any credit made
                        if (relative_M8BonVent.sum_De_Credit_Fait > 0.0) {
                            Text(
                                text = "دفع: ",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = String.format(
                                    "%.2f دج",
                                    relative_M8BonVent.sum_De_Credit_Fait
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                fun update_etate_Listening_relative_M17Message(): Unit {
                    val new = relative_M17Message?.copy(
                        etate = M17MessageVocale.Etate.ECOUTE
                    )
                    if (new != null) {
                        repositorysMainSetter.upsert_M17MessageVocale(new)
                    }
                }

                // Voice message player section
                if (hasVoiceMessage) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play/Stop button
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    when {
                                        isCurrentlyPlaying -> {
                                            val stopResult =
                                                audioRecorderAndPlayHandler.stopPlayback()
                                            if (stopResult.isFailure) {
                                                val errorMessage =
                                                    "Erreur lors de l'arrêt: ${stopResult.exceptionOrNull()?.message}"
                                                Toast.makeText(
                                                    context,
                                                    errorMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        else -> {
                                            val audioSource =
                                                relative_M17Message?.nomDeSonOriginaleFichie
                                                    ?: ""

                                            val playResult =
                                                audioRecorderAndPlayHandler.startPlayback(
                                                    context = context,
                                                    parentMessageVID = relative_M8BonVent.vid,
                                                    firebaseUrl = audioSource,
                                                    onPlaybackComplete = {
                                                        // Update M8BonVent as before
                                                        if (!relative_M8BonVent.sonVocaleEstEcoute) {
                                                            val currentTimestamp =
                                                                datesHandler.getCurrentTimestamps()
                                                            viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.upsertUneDataEtReturnVID(
                                                                relative_M8BonVent.copy(
                                                                    sonVocaleEstEcoute = true,
                                                                    sonEcoutementEstFaitAutimestamps = currentTimestamp
                                                                )
                                                            ) {}
                                                        }

                                                        // NEW: Update M17MessageVocale to ECOUTE state
                                                        update_etate_Listening_relative_M17Message()
                                                    },
                                                    onPlaybackError = { errorMessage ->
                                                        Toast.makeText(
                                                            context,
                                                            "Erreur de lecture du message vocal: $errorMessage",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                )
                                            if (playResult.isFailure) {
                                                val errorMessage =
                                                    "Erreur lors du démarrage: ${playResult.exceptionOrNull()?.message}"
                                                Toast.makeText(
                                                    context,
                                                    errorMessage,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !isCurrentlyDownloading
                        ) {
                            Icon(
                                imageVector = when {
                                    isCurrentlyDownloading -> Icons.Default.PlayArrow
                                    isCurrentlyPlaying -> Icons.Default.Stop
                                    else -> Icons.Default.PlayArrow
                                },
                                contentDescription = when {
                                    isCurrentlyDownloading -> "Téléchargement en cours"
                                    isCurrentlyPlaying -> "Arrêter la lecture"
                                    else -> "Lecture du message vocal"
                                },
                                tint = Color.White
                            )
                        }

                        // Progress indicator with proper layout
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        ) {
                            when {
                                isCurrentlyDownloading -> {
                                    LinearProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = Color.White,
                                        trackColor = Color.White.copy(alpha = 0.3f)
                                    )
                                }

                                isCurrentlyPlaying && playbackProgress.duration > 0 -> {
                                    LinearProgressIndicator(
                                        progress = { playbackProgress.progress.coerceIn(0f, 1f) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = Color.White,
                                        trackColor = Color.White.copy(alpha = 0.3f)
                                    )
                                }

                                else -> {
                                    LinearProgressIndicator(
                                        progress = { 0f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = Color.White.copy(alpha = 0.5f),
                                        trackColor = Color.White.copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }

    // Credit Payment Dialog
    if (showCreditDialog) {
        CreditPaymentDialog(
            onDismiss = { showCreditDialog = false },
            onConfirm = { paymentAmount ->
                val updatedBonVent = targeted_M8Transaction_Pour_Credit?.copy(
                    sum_De_Credit_Fait = sumBonVents,
                    versement = paymentAmount,
                )

                if (updatedBonVent != null) {
                    repositorysMainSetter.upsertM8BonVent(updatedBonVent)
                }

                Toast.makeText(
                    context,
                    "تم إضافة الدفع بنجاح: ${String.format("%.2f", paymentAmount)} دج",
                    Toast.LENGTH_SHORT
                ).show()

                showCreditDialog = false
            },
            sumBonVents = sumBonVents
        )
    }
}
