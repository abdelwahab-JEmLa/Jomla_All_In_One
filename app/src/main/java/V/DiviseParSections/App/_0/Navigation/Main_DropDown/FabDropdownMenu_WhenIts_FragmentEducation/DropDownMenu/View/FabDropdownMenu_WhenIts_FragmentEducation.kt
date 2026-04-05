package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View

import EntreApps.Shared.Models.Components.Ousstad_Tahfid
import EntreApps.Shared.Models.Compts
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.DropDownItem_Imprime_pdf_communication_ac_parent
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.DropDownItem_Imprime_pdf_collecte_numeros_whatsapp
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.DropDownItem_Send_Cards_WhatsApp_Parent
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But4.DropDownItem_Imprime_pdf_List_Talaba
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But5.DropDownItem_Imprime_pdf_Case_A_Cochet
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID6.DropDownItem_ID6
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID8.DropDownItem_ButID8
import android.text.format.DateUtils.isToday
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun FabDropdownMenu_WhenIts_FragmentEducation(
    onDismissDropdown: () -> Unit,
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    val context = LocalContext.current
    var showTextField by remember { mutableStateOf(true) }
    var studentName by remember { mutableStateOf("") }
    var showOussstadSelection by remember { mutableStateOf(false) }

    val repo19 = aCentralFacade.repositorysMainGetter.repo19Etudiant
    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val activeOusstad = activeCentralValues.active_Ousstad_Tahfid

    // Count students not updated today
    val studentsNotUpdatedToday by remember {
        derivedStateOf {
            repo19.datasValue.filter { etudiant ->
                !isToday(etudiant.dernierTimeTampsSynchronisationAvecFireBase)
            }
        }
    }

    // Get active Ousstad and determine parent key
    fun getActiveOussstadKey(): String {
        val params = M00CentralParametresOfAllApps()

        return when (activeOusstad) {
            Ousstad_Tahfid.Abdelwahab_Osstad -> Compts.AbdelwahabTravailleChezGros_KeyId.keyId
            Ousstad_Tahfid.Amine_Madrassa -> params.amine_madrasa_Compt_KeyId
            Ousstad_Tahfid.Kissm_Intikali -> "Kissm_Intikali"
            Ousstad_Tahfid.Non_Defini_Actuellemen -> "Non_Defini_Actuellemen"
            null -> Compts.AbdelwahabTravailleChezGros_KeyId.keyId// Default fallback
        }
    }

    fun updateActiveOusstad(ousstad: Ousstad_Tahfid) {
        focusedValuesGetter.update_activeCentralValues(
            activeCentralValues.copy(active_Ousstad_Tahfid = ousstad)
        )

        Toast.makeText(
            context,
            "تم تحديد الأستاذ النشط: ${ousstad.nom_arab}",
            Toast.LENGTH_SHORT
        ).show()

        showOussstadSelection = false
    }

    fun add() {
        if (studentName.isNotBlank()) {
            // Set active Ousstad as parent
            val activeOussstadKey = getActiveOussstadKey()

            val newStudent = M19Etudiant(
                nom = studentName.trim(),
                parent_ousstad_key = activeOussstadKey
            )

            repositorysMainSetter.add_M19Etudiant(newStudent)

            Toast.makeText(
                context,
                "تمت إضافة الطالب بنجاح",
                Toast.LENGTH_SHORT
            ).show()

            studentName = ""
            showTextField = false
            onDismissDropdown()
        }
    }

    fun markAllAsAbsent() {
        val studentsToMark = studentsNotUpdatedToday

        if (studentsToMark.isEmpty()) {
            Toast.makeText(
                context,
                "جميع الطلاب محدثون اليوم",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        var successCount = 0
        studentsToMark.forEach { etudiant ->
            try {
                // Create absence observation for this student
                val absenceObservation = M20ObsarvationEtudion.get_default().copy(
                    type = M20ObsarvationEtudion.Type.Raeeb,
                    etudiant_keyID = etudiant.keyID,
                    min_soura = etudiant.dernier_Soura_Wassale_Laha,
                    min_aya = etudiant.dernier_Soura_sater,
                    ila_soura = etudiant.mokarrare_hifde,
                    ila_aya = if (etudiant.mokarrare_hifde_sater == 0) {
                        etudiant.mokarrare_hifde.rakme_akher_aya
                    } else {
                        etudiant.mokarrare_hifde_sater
                    },
                    takyim = M19Etudiant.Takiyim.Lam_Yahfed,
                    parent_ousstad_key = etudiant.parent_ousstad_key,
                    creationTimestamps = System.currentTimeMillis()
                )

                repositorysMainSetter.upsert_M20ObsarvationEtudion(absenceObservation)
                successCount++
            } catch (e: Exception) {
                // Log error but continue with other students
            }
        }

        Toast.makeText(
            context,
            "تم تسجيل الغياب لـ $successCount طالب",
            Toast.LENGTH_LONG
        ).show()

        onDismissDropdown()
    }

    Box(
        modifier = modifier
            .offset(y = (-90).dp)
    ) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = onDismissDropdown,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            // NEW: Active Ousstad Selection Section
            Card(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "الأستاذ النشط:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Text(
                            text = activeOusstad?.nom_arab ?: "غير محدد",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        text = {
                            Text(
                                text = "تغيير الأستاذ النشط",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        },
                        onClick = { showOussstadSelection = !showOussstadSelection }
                    )

                    // Show Ousstad selection options
                    if (showOussstadSelection) {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))

                        Ousstad_Tahfid.values().forEach { ousstad ->
                            DropdownMenuItem(
                                leadingIcon = {
                                    if (activeOusstad == ousstad) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.size(20.dp))
                                    }
                                },
                                text = {
                                    Text(
                                        text = ousstad.nom_arab,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (activeOusstad == ousstad) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        },
                                        color = if (activeOusstad == ousstad) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                },
                                onClick = { updateActiveOusstad(ousstad) }
                            )
                        }
                    }
                }
            }

            Divider()

            // Mark all non-updated students as absent
            Card(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (studentsNotUpdatedToday.isNotEmpty()) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PersonOff,
                            contentDescription = null,
                            tint = if (studentsNotUpdatedToday.isNotEmpty()) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    },
                    text = {
                        Text(
                            text = if (studentsNotUpdatedToday.isNotEmpty()) {
                                "تسجيل الغياب للطلاب غير المحدثين (${studentsNotUpdatedToday.size})"
                            } else {
                                "تسجيل الغياب للطلاب غير المحدثين"
                            },
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = { markAllAsAbsent() },
                    enabled = studentsNotUpdatedToday.isNotEmpty()
                )
            }

            Divider()

            DropDownItem_ButID8()
            DropDownItem_ID6()
            DropDownItem_Imprime_pdf_List_Talaba()
            DropDownItem_Imprime_pdf_communication_ac_parent()
            DropDownItem_Imprime_pdf_collecte_numeros_whatsapp()
            DropDownItem_Send_Cards_WhatsApp_Parent()
            DropDownItem_Imprime_pdf_Case_A_Cochet()

            Divider()

            // Show text field when toggled
            if (showTextField) {
                Divider()

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .width(250.dp)
                ) {
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text("Nom de l'étudiant") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(
                                onClick = { add() },
                                enabled = studentName.isNotBlank()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Confirmer",
                                    tint = if (studentName.isNotBlank())
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
