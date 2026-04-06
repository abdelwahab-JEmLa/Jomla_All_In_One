package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Archive.A_PolygonCreateur.View
   /*
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.E1SecteurDeClients
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import kotlinx.coroutines.launch

@Composable
fun SecteurDialog(
    viewModel: MapClientsViewModel,
    onDismiss: () -> Unit,
) {
    val secteurs by viewModel.secteurs.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "قطاعات العملاء", // ClientAchteur Sectors in Arabic
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // List_GroupeAchatProduit of sectors
                if (secteurs.isEmpty()) {
                    Text(
                        text = "لا توجد قطاعات حاليا", // No sectors currently
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                    ) {
                        items(secteurs) { secteur ->
                            SecteurItem(
                                secteur = secteur,
                                onActiveChange = { active ->
                                    coroutineScope.launch {
                                        viewModel.updateSecteurActive(secteur.vid, active)
                                    }
                                }
                            )
                        }
                    }
                }

                // Add new sector button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    FloatingActionButton(
                        onClick = { viewModel.showAddSecteurDialog() },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Sector")
                    }
                }

                // Close button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("إغلاق") // Close
                }
            }
        }
    }
}

@Composable
fun SecteurItem(
    secteur: E1SecteurDeClients,
    onActiveChange: (Boolean) -> Unit,
) {
    val colorValue = try {
        Color(secteur.couleur.toColorInt())
    } catch (e: Exception) {
        Color.Blue
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color indicator
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(colorValue)
        )

        // Sector name
        Text(
            text = secteur.nom,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        )

        // Active checkbox
        Checkbox(
            checked = secteur.ouvert,
            onCheckedChange = { onActiveChange(it) },
            colors = CheckboxDefaults.colors(checkedColor = colorValue)
        )
    }
}

@Composable
fun AddSecteurDialog(
    viewModel: MapClientsViewModel,
    onDismiss: () -> Unit,
) {
    var sectorName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#ff0000ff") } // Default blue color
    val predefinedColors = listOf("#ff0000ff", "#ffff0000", "#ff00ff00", "#ffffff00") // Blue, Red, Green, Yellow
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "إضافة قطاع جديد", // Add new sector in Arabic
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Name input field
                OutlinedTextField(
                    value = sectorName,
                    onValueChange = { sectorName = it },
                    label = { Text("اسم القطاع") }, // Sector name
                    modifier = Modifier.fillMaxWidth()
                )

                // Color selection
                Text("اختر اللون:", modifier = Modifier.padding(top = 8.dp)) // Choose color

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    predefinedColors.forEach { colorHex ->
                        val color = try {
                            Color(colorHex.toColorInt())
                        } catch (e: Exception) {
                            Color.Gray
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color)
                                .border(
                                    width = if (selectedColor == colorHex) 3.dp else 1.dp,
                                    color = if (selectedColor == colorHex) Color.Black else Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedColor = colorHex }
                        ) {
                            if (selectedColor == colorHex) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(24.dp)
                                )
                            }
                        }
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text("إلغاء") // Cancel
                    }

                    Button(
                        onClick = {
                            if (sectorName.isNotBlank()) {
                                coroutineScope.launch {
                                    viewModel.addNewSector(sectorName, selectedColor)
                                    onDismiss()
                                }
                            }
                        },
                        enabled = sectorName.isNotBlank(),
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text("موافق") // OK
                    }
                }
            }
        }
    }
}
                                  */
