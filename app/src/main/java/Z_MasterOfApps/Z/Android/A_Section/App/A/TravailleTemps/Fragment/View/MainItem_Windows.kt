package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View

import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.Model.K_TempTravaille
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MainItem_Windows(
    modifier: Modifier = Modifier,
    intervale: K_TempTravaille.IntervalesDeTravaille,
    viewModel: Windows__ViewModel // Add the viewModel parameter
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = intervale.typeTemp.icon,
                    contentDescription = "Type: ${intervale.typeTemp.name}",
                    tint = intervale.typeTemp.color,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = intervale.typeTemp.nomArabe,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${intervale.tempDepart} - ${intervale.temparrete}",
                    style = MaterialTheme.typography.bodyLarge
                )

                // Add delete button
                Spacer(modifier = Modifier.width(8.dp))
                val isAbdelwahabLeGerant by viewModel.isAbdelwahabLeGerant.collectAsState()

                if (isAbdelwahabLeGerant) {
                    // Add edit button
                    IconButton(
                        onClick = {
                            // Call your edit function here
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
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val duration = K_TempTravaille.IntervalesDeTravaille.calculateDuration(
                    intervale.tempDepart,
                    intervale.temparrete
                )
                val s = " : الوقت"
                Text(
                    text = "$duration$s",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "ID: ${intervale.idBonDeCetteIntervale}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
