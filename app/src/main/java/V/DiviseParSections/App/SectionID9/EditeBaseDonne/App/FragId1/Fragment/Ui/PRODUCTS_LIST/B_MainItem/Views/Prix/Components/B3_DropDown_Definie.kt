package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Views.Prix.Components

import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TypeChoisiDropdownCard(
    modifier: Modifier = Modifier,
    relative_M13Tariffication: M13TarificationInfos,
    selectedType: M13TarificationInfos.TypeChoisi,
    onTypeSelected: (M13TarificationInfos.TypeChoisi) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = selectedType.couleur // Background color from entity
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    selectedType.iconVector?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = selectedType.couleur_Text,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = selectedType.nomArabe,
                        style = MaterialTheme.typography.bodyMedium,
                        color = selectedType.couleur_Text,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = relative_M13Tariffication.prixCurrency.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = selectedType.couleur_Text,
                        fontWeight = FontWeight.Medium
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = selectedType.couleur_Text
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = selectedType.couleur_Text.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))

                val allowedTypes = listOf(
                    M13TarificationInfos.TypeChoisi.PRIX_BASE,
                    M13TarificationInfos.TypeChoisi.DefiniParGerant
                )

                allowedTypes.forEach { type ->
                    if (type != selectedType) {
                        TypeChoisiMenuItem(
                            type = type,
                            onSelected = {
                                onTypeSelected(type)
                                expanded = false
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TypeChoisiMenuItem(
    type: M13TarificationInfos.TypeChoisi,
    onSelected: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        shape = RoundedCornerShape(8.dp),
        color = type.couleur
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            type.iconVector?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = type.couleur_Text, // Icon color from entity text color
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = type.nomArabe,
                style = MaterialTheme.typography.bodySmall,
                color = type.couleur_Text // Text color from entity
            )
        }
    }
}
