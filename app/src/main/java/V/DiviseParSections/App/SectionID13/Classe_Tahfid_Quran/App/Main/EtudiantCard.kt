package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main

import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.EtudiantDetailsDialog
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.A_Takiyim.TakiyimSelectionDialog
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.MoulahadaSouloukSelectionDialog
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.SouraSelectionDialog
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.processTakiyimEvaluation
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Ousstad_Tahfid
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.Repo20ObsarvationEtudion
import android.text.format.DateUtils.isToday
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun EtudiantCard(
    etudiant: M19Etudiant,
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    repo20Observation: Repo20ObsarvationEtudion = aCentralFacade.repositorysMainGetter.repo20ObsarvationEtudion,
    modifier: Modifier = Modifier
) {
    val etudiantId = etudiant.keyID

    var showDetailsDialog by remember(etudiantId) { mutableStateOf(false) }
    var showSouraDialog by remember(etudiantId) { mutableStateOf(false) }
    var showMokarrareDialog by remember(etudiantId) { mutableStateOf(false) }
    var showTakiyimDialog by remember(etudiantId) { mutableStateOf(false) }
    var showMoulahada3alaSouloukDialog by remember(etudiantId) { mutableStateOf(false) }
    var showIstedrakSouraDialog by remember(etudiantId) { mutableStateOf(false) }
    var showIstedrakMokarrareDialog by remember(etudiantId) { mutableStateOf(false) }
    var showIstedrakTakiyimDialog by remember(etudiantId) { mutableStateOf(false) }

    // FIXED TODO(1): Add Ousstad transfer dialog state
    var showOussstadTransferDialog by remember(etudiantId) { mutableStateOf(false) }
    var showOussstadDropdownMenu by remember(etudiantId) { mutableStateOf(false) }

    val wasUpdatedToday = isToday(etudiant.dernierTimeTampsSynchronisationAvecFireBase)

    val observations = remember(repo20Observation.datasValue) { repo20Observation.datasValue }
    val absenceCount = remember(etudiant, observations) {
        etudiant.calculateUnjustifiedAbsences(observations)
    }

    Card(
        modifier = modifier.clickable { showDetailsDialog = true },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (wasUpdatedToday) Color(0xFFFFFDE7) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EventSeat,
                    contentDescription = "Chaise",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${etudiant.positon_don_classe}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = etudiant.nom.ifBlank { "---" },
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = etudiant.prenom.ifBlank { "---" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${etudiant.age} سنة",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                if (absenceCount > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "غياب: $absenceCount",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )

                        IconButton(
                            onClick = {
                                repo19Etudiant.upsert(
                                    etudiant.copy(imprime_justification = !etudiant.imprime_justification)
                                )
                            },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = "Imprimer justification",
                                tint = if (etudiant.imprime_justification) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                },
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // FIXED TODO(1): Add Ousstad transfer button at bottom
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { showOussstadDropdownMenu = true },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "تحويل للأستاذ",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showOussstadDropdownMenu,
                    onDismissRequest = { showOussstadDropdownMenu = false }
                ) {
                    Ousstad_Tahfid.values().forEach { ousstad ->
                        DropdownMenuItem(
                            text = { Text(ousstad.nom_arab) },
                            onClick = {
                                showOussstadDropdownMenu = false
                                showOussstadTransferDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // FIXED TODO(1): Ousstad Transfer Confirmation Dialog
    if (showOussstadTransferDialog) {
        var selectedOusstad by remember { mutableStateOf<Ousstad_Tahfid?>(null) }

        AlertDialog(
            onDismissRequest = { showOussstadTransferDialog = false },
            title = { Text("تحويل الطالب للأستاذ") },
            text = {
                Column {
                    Text("اختر الأستاذ الجديد:")
                    Spacer(modifier = Modifier.height(8.dp))

                    Ousstad_Tahfid.values().forEach { ousstad ->
                        TextButton(
                            onClick = { selectedOusstad = ousstad },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(ousstad.nom_arab)
                                if (selectedOusstad == ousstad) {
                                    Icon(
                                        imageVector = Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedOusstad?.let { newOusstad ->
                            // Update student's parent_ousstad_key
                            val updatedEtudiant = etudiant.copy(
                                parent_ousstad_key = newOusstad.key
                            )
                            repo19Etudiant.upsert(updatedEtudiant)

                            // Update all observations for this student
                            observations
                                .filter { it.etudiant_keyID == etudiant.keyID }
                                .forEach { observation ->
                                    val updatedObservation = observation.copy(
                                        parent_ousstad_key = newOusstad.key
                                    )
                                    repo20Observation.upsert(updatedObservation)
                                }
                        }
                        showOussstadTransferDialog = false
                    },
                    enabled = selectedOusstad != null
                ) {
                    Text("تأكيد")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOussstadTransferDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showDetailsDialog) {
        EtudiantDetailsDialog(
            etudiant = etudiant,
            repo19Etudiant = repo19Etudiant,
            repo20Observation = repo20Observation,
            onDismiss = { showDetailsDialog = false },
            onShowSouraDialog = {
                showDetailsDialog = false
                showSouraDialog = true
            },
            onShowMokarrareSouraDialog = {
                showDetailsDialog = false
                showMokarrareDialog = true
            },
            onShowMokarrareDialog = {
                showDetailsDialog = false
                showMokarrareDialog = true
            },
            onShowTakiyimDialog = {
                showDetailsDialog = false
                showTakiyimDialog = true
            },
            onShowMoulahada3alaSouloukDialog = {
                showDetailsDialog = false
                showMoulahada3alaSouloukDialog = true
            },
            onShowIstedrakSouraDialog = {
                showDetailsDialog = false
                showIstedrakSouraDialog = true
            },
            onShowIstedrakMokarrareDialog = {
                showDetailsDialog = false
                showIstedrakMokarrareDialog = true
            },
            onShowIstedrakTakiyimDialog = {
                showDetailsDialog = false
                showIstedrakTakiyimDialog = true
            }
        )
    }

    if (showSouraDialog) {
        SouraSelectionDialog(
            currentSoura = etudiant.dernier_Soura_Wassale_Laha,
            onDismiss = {
                showSouraDialog = false
                showDetailsDialog = true
            },
            onSelect = { selectedSoura ->
                repo19Etudiant.upsert(
                    etudiant.copy(
                        mokarrare_hifde = etudiant.dernier_Soura_Wassale_Laha,
                        mokarrare_hifde_sater = etudiant.dernier_Soura_sater,
                        dernier_Soura_Wassale_Laha = selectedSoura,
                        dernier_Soura_sater = 1
                    )
                )
                showSouraDialog = false
                showDetailsDialog = true
            }
        )
    }

    if (showMokarrareDialog) {
        SouraSelectionDialog(
            currentSoura = etudiant.mokarrare_hifde,
            onDismiss = {
                showMokarrareDialog = false
                showDetailsDialog = true
            },
            onSelect = { selectedSoura ->
                repo19Etudiant.upsert(
                    etudiant.copy(
                        mokarrare_hifde = selectedSoura,
                        mokarrare_hifde_sater = 1
                    )
                )
                showMokarrareDialog = false
                showDetailsDialog = true
            }
        )
    }

    if (showTakiyimDialog) {
        TakiyimSelectionDialog(
            currentTakiyim = etudiant.dernier_takyim_dabte,
            etudiantKeyID = etudiant.keyID,
            onDismiss = {
                showTakiyimDialog = false
                showDetailsDialog = true
            },
            onSelect = { selectedTakiyim, selectedMoulahadat ->
                val updatedEtudiant = processTakiyimEvaluation(
                    etudiant = etudiant,
                    selectedTakiyim = selectedTakiyim,
                    selectedMoulahadat = selectedMoulahadat,
                    aCentralFacade = aCentralFacade
                )
                repo19Etudiant.upsert(updatedEtudiant)
                showTakiyimDialog = false
                showDetailsDialog = true
            }
        )
    }

    if (showMoulahada3alaSouloukDialog) {
        MoulahadaSouloukSelectionDialog(
            currentMoulahada = etudiant.moulahada_3ala_soulouk,
            onDismiss = {
                showMoulahada3alaSouloukDialog = false
                showDetailsDialog = true
            },
            onSelect = { selectedMoulahada ->
                repo19Etudiant.upsert(etudiant.copy(moulahada_3ala_soulouk = selectedMoulahada))
                showMoulahada3alaSouloukDialog = false
                showDetailsDialog = true
            }
        )
    }

    if (showIstedrakSouraDialog) {
        SouraSelectionDialog(
            currentSoura = etudiant.istedrak_kadim_Akher_Soura_Wassale_Laha,
            onDismiss = {
                showIstedrakSouraDialog = false
                showDetailsDialog = true
            },
            onSelect = { selectedSoura ->
                repo19Etudiant.upsert(
                    etudiant.copy(istedrak_kadim_Akher_Soura_Wassale_Laha = selectedSoura)
                )
                showIstedrakSouraDialog = false
                showDetailsDialog = true
            }
        )
    }

    if (showIstedrakMokarrareDialog) {
        SouraSelectionDialog(
            currentSoura = etudiant.istedrak_kadim_Moukarare,
            onDismiss = {
                showIstedrakMokarrareDialog = false
                showDetailsDialog = true
            },
            onSelect = { selectedSoura ->
                repo19Etudiant.upsert(
                    etudiant.copy(istedrak_kadim_Moukarare = selectedSoura)
                )
                showIstedrakMokarrareDialog = false
                showDetailsDialog = true
            }
        )
    }

    if (showIstedrakTakiyimDialog) {
        TakiyimSelectionDialog(
            currentTakiyim = etudiant.istedrak_kadim_Takyim_hali,
            etudiantKeyID = null,
            onDismiss = {
                showIstedrakTakiyimDialog = false
                showDetailsDialog = true
            },
            onSelect = { selectedTakiyim, _ ->
                repo19Etudiant.upsert(
                    etudiant.copy(istedrak_kadim_Takyim_hali = selectedTakiyim)
                )
                showIstedrakTakiyimDialog = false
                showDetailsDialog = true
            }
        )
    }
}
