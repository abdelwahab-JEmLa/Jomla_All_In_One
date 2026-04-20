package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Views.Prix.Components

import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
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
    relative_M1Produit: M01Produit,
    relative_M13Tariffication: M13TarificationInfos,
    selectedType: M13TarificationInfos.TypeChoisi,
    onTypeSelected: (M13TarificationInfos.TypeChoisi) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val alternativeType = if (selectedType == M13TarificationInfos.TypeChoisi.Prix_Detaille) {
        M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService
    } else {
        M13TarificationInfos.TypeChoisi.Prix_Detaille
    }

    val selectedTypePrice = when (selectedType) {
        M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService -> {
            relative_M1Produit.prixVent.toString()
        }
        M13TarificationInfos.TypeChoisi.Prix_Detaille -> {
            val prixCurrency = relative_M13Tariffication.prixCurrency.takeIf { it > 0.0 }
            prixCurrency?.toString() ?: "non definie"
        }
        else -> "0.0"
    }

    val alternativeTypePrice = when (alternativeType) {
        M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService -> {
            relative_M1Produit.prixVent.toString()
        }
        M13TarificationInfos.TypeChoisi.Prix_Detaille -> {
            val prixCurrency = relative_M13Tariffication.prixCurrency.takeIf { it > 0.0 }
            prixCurrency?.toString() ?: "non definie"
        }
        else -> "0.0"
    }

    Card(
        modifier = modifier
            .getSemanticsTag(relative_M13Tariffication, "relative_M13Tariffication")
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = selectedType.couleur
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            if (!expanded) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expanded = true
                        },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = selectedType.couleur.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
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
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = selectedTypePrice,
                            style = MaterialTheme.typography.bodyMedium,
                            color = selectedType.couleur_Text,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onTypeSelected(alternativeType)
                        },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = alternativeType.couleur.copy(alpha = 0.7f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            alternativeType.iconVector?.let { icon ->
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = alternativeType.couleur_Text,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = alternativeType.nomArabe,
                                style = MaterialTheme.typography.bodySmall,
                                color = alternativeType.couleur_Text
                            )
                        }

                        Text(
                            text = alternativeTypePrice,
                            style = MaterialTheme.typography.bodyMedium,
                            color = alternativeType.couleur_Text,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Small expand button at the bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ExpandMore,
                        contentDescription = "Expand",
                        tint = selectedType.couleur_Text.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }

            } else {
                // Expanded state: Show header with collapse button and detailed options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = false },
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
                    }

                    Icon(
                        imageVector = Icons.Filled.ExpandLess,
                        contentDescription = "Collapse",
                        tint = selectedType.couleur_Text
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = selectedType.couleur_Text.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))

                DropDownItems(
                    relative_M1Produit = relative_M1Produit,
                    relative_M13Tariffication = relative_M13Tariffication,
                    selectedType = selectedType,
                    onSelected = {
                        onTypeSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun FloatingAlternativePriceCard(
    alternativeType: M13TarificationInfos.TypeChoisi,
    relative_M1Produit: M01Produit,
    relative_M13Tariffication: M13TarificationInfos,
    onDismiss: () -> Unit,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = alternativeType.couleur.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                alternativeType.iconVector?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = alternativeType.couleur_Text,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = alternativeType.nomArabe,
                    style = MaterialTheme.typography.bodySmall,
                    color = alternativeType.couleur_Text
                )
            }

            val price = when (alternativeType) {
                M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService -> {
                    relative_M1Produit.prixVent.toString()
                }
                M13TarificationInfos.TypeChoisi.Prix_Detaille -> {
                    val prixCurrency = relative_M13Tariffication.prixCurrency.takeIf { it > 0.0 }
                    prixCurrency?.toString() ?: "non definie"
                }

                else->"0.0"
            }

            Text(
                text = price,
                style = MaterialTheme.typography.bodyMedium,
                color = alternativeType.couleur_Text,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DropDownItems(
    relative_M1Produit: M01Produit,
    relative_M13Tariffication: M13TarificationInfos,
    selectedType: M13TarificationInfos.TypeChoisi,
    onSelected: (M13TarificationInfos.TypeChoisi) -> Unit
) {
    // Prix_SupperGro_Et_PresentationService option
    M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService.let { type ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onSelected(type)
                },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = type.couleur
            )
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
                        tint = type.couleur_Text,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = type.nomArabe,
                    style = MaterialTheme.typography.bodySmall,
                    color = type.couleur_Text
                )

                val prixCurrency = relative_M1Produit.prixVent

                Text(
                    text = prixCurrency.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = type.couleur_Text,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }

    M13TarificationInfos.TypeChoisi.Prix_Detaille.let { type ->
        Card(
            modifier = Modifier
                .getSemanticsTag(relative_M13Tariffication, "relative_M13Tariffication")
                .fillMaxWidth()
                .clickable {
                    onSelected(type)
                },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = type.couleur
            )
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
                        tint = type.couleur_Text,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = type.nomArabe,
                    style = MaterialTheme.typography.bodySmall,
                    color = type.couleur_Text
                )

                val prixCurrency = relative_M13Tariffication.prixCurrency.takeIf { it > 0.0 }

                Text(
                    text =" ${prixCurrency ?: "non definie"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = type.couleur_Text,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}
