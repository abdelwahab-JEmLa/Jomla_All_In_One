package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.DropDownItem_Imprime_pdf_communication_ac_parent
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But4.DropDownItem_Imprime_pdf_List_Talaba
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But5.DropDownItem_Imprime_pdf_Case_A_Cochet
import android.text.format.DateUtils.isToday
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonOff
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun FabDropdownMenu_WhenIts_FragmentEducation(
    onDismissDropdown: () -> Unit,
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
) {
    val context = LocalContext.current
    var showTextField by remember { mutableStateOf(true) }
    var studentName by remember { mutableStateOf("") }

    val repo19 = aCentralFacade.repositorysMainGetter.repo19Etudiant

    // Count students not updated today
    val studentsNotUpdatedToday by remember {
        derivedStateOf {
            repo19.datasValue.filter { etudiant ->
                !isToday(etudiant.dernierTimeTampsSynchronisationAvecFireBase)
            }
        }
    }

    fun add() {
        if (studentName.isNotBlank()) {
            val newStudent = M19Etudiant(nom = studentName.trim())
            repositorysMainSetter.add_M19Etudiant(newStudent)
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
            // FIXED: Add button to mark all non-updated students as absent
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

            DropDownItem_Imprime_pdf_List_Talaba()
            DropDownItem_Imprime_pdf_communication_ac_parent()
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
