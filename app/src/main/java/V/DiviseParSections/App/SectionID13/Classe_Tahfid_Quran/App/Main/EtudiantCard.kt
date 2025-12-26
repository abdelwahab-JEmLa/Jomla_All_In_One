package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main

import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.EtudiantDetailsDialog
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.MoulahadaSouloukSelectionDialog
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.SouraSelectionDialog
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.TakiyimSelectionDialog
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import android.text.format.DateUtils.isToday
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun EtudiantCard(
    etudiant: M19Etudiant,
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
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

    // Check if updated today
    val wasUpdatedToday = isToday(etudiant.dernierTimeTampsSynchronisationAvecFireBase)

    // Get last 3 observations for this student
    val repo20 = aCentralFacade.repositorysMainGetter.repo20ObsarvationEtudion
    val last3Observations by remember(etudiantId) {
        derivedStateOf {
            repo20.datasValue
                .filter { it.etudiant_keyID == etudiantId }
                .sortedByDescending { it.creationTimestamps }
                .take(3)
        }
    }

    // Compact card - Shows basic info with yellow background if updated today
    Card(
        modifier = modifier.clickable { showDetailsDialog = true },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (wasUpdatedToday) {
                Color(0xFFFFFDE7)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Chair icon at the top
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EventSeat,
                    contentDescription = "Chaise",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Student name
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

            // Age and absences row
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

                // Display absences with warning color if > 0
                if (etudiant.nmbr_absence_sans_justification > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "غياب: ${etudiant.nmbr_absence_sans_justification}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )

                        // Print icon toggle button
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

            // Display last 3 observations
            if (last3Observations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "آخر 3 سجلات:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    last3Observations.forEach { observation ->
                        val isAbsence = observation.type == V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion.Type.Raeeb

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (isAbsence) {
                                    Icon(
                                        imageVector = Icons.Default.PersonOff,
                                        contentDescription = "غياب",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Text(
                                    text = "${observation.min_soura.arabicName} → ${observation.ila_soura.arabicName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isAbsence) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }

                            Text(
                                text = observation.takyim.arabicName,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isAbsence) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.tertiary
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDetailsDialog) {
        EtudiantDetailsDialog(
            etudiant = etudiant,
            repo19Etudiant = repo19Etudiant,
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

    // Selection Dialogs
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
            onDismiss = {
                showTakiyimDialog = false
                showDetailsDialog = true
            },
            onSelect = { selectedTakiyim ->
                repo19Etudiant.upsert(
                    etudiant.copy(
                        dernier_takyim_dabte = selectedTakiyim,
                        dernier_Soura_Wassale_Laha = etudiant.mokarrare_hifde,
                        dernier_Soura_sater = etudiant.mokarrare_hifde_sater
                    )
                )
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

    // Istedrak Dialogs
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
            onDismiss = {
                showIstedrakTakiyimDialog = false
                showDetailsDialog = true
            },
            onSelect = { selectedTakiyim ->
                repo19Etudiant.upsert(
                    etudiant.copy(istedrak_kadim_Takyim_hali = selectedTakiyim)
                )
                showIstedrakTakiyimDialog = false
                showDetailsDialog = true
            }
        )
    }
}
