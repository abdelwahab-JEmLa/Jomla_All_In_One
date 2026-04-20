package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View

import EntreApps.Shared.Models.Utilisateur
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravaille
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun MainItem_Windows(
    modifier: Modifier = Modifier,
    intervale: K_TempTravaille.IntervalesDeTravaille,
    viewModel: RecordingViewModel,
    viewModelInitApp: ViewModelInitApp = koinViewModel(),
    focusedValuesGetter: FocusedValuesGetter = koinInject()

) {
    val clientDataBaseSnapList = viewModelInitApp.clientDataBaseSnapList.find {
        it.id == intervale.idClientSiAchat
    }

    val lightGreen = intervale.typeTemp.color.copy(alpha = 1f)
        .compositeOver(androidx.compose.ui.graphics.Color.White)

    ElevatedCard(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                set(value = intervale, key = SemanticsPropertyKey(""))
            }
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = lightGreen )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = intervale.typeTemp.icon,
                        contentDescription = "Type: ${intervale.typeTemp.name}",
                        tint = intervale.typeTemp.color,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    ElevatedCard(
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Display vendor name
                            Text(
                                text =
                                    if (focusedValuesGetter.currentApp_ItsWorkChezGrossisst)
                                        Utilisateur.Abdelwahab_Osstad.nom_arab
                                    else
                                        intervale.utilisateur.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Arrow to type",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = intervale.typeTemp.nomArabe,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            if (clientDataBaseSnapList != null) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Arrow to client",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = clientDataBaseSnapList.nom,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))

                    val duration = K_TempTravaille.IntervalesDeTravaille.calculateDuration(
                        intervale.tempDepart,
                        intervale.temparrete
                    )
                    val s = " : الوقت"
                    Text(
                        text = "$duration$s",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    val isAbdelwahabLeGerant by viewModel.isAbdelwahabLeGerant.collectAsState()

                    if (isAbdelwahabLeGerant) {
                        IconButton(
                            onClick = {
                                viewModel.editIntervaleTemp(intervale)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit interval",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.deleteIntervaleTemp(intervale.vid) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete interval",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "${intervale.tempDepart} - ${intervale.temparrete}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
