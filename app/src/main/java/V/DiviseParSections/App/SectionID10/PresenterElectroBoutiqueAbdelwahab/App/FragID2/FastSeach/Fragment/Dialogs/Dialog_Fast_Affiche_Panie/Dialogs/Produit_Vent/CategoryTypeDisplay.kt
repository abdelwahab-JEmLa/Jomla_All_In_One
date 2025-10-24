package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Produit_Vent

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun CategoryTypeDisplay(
    produit: ArticlesBasesStatsTable,
    category: CategoriesTabelle?,
    modifier: Modifier = Modifier,
    repositorysMainSetter: RepositorysMainSetter
) {
    var textValue by remember(produit.nom_type_categorie) {
        mutableStateOf(
            produit.nom_type_categorie.ifEmpty { category?.nom ?: "" }
        )
    }

    // Check if nom_type_categorie matches category name
    val isMatching = produit.nom_type_categorie.isNotEmpty() &&
            produit.nom_type_categorie == category?.nom

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isMatching) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        OutlinedTextField(
            value = textValue,
            onValueChange = { newValue ->
                textValue = newValue
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = {
                Text("Type de catégorie")
            },
            placeholder = {
                Text(category?.nom ?: "Catégorie")
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isMatching) FontWeight.Bold else FontWeight.Normal
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = if (isMatching) Color.White else MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = if (isMatching) Color.White else MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = if (isMatching) Color.White else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (isMatching) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.outline,
                focusedLabelColor = if (isMatching) Color.White else MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = if (isMatching) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = if (isMatching) Color.White else MaterialTheme.colorScheme.primary,
                focusedPlaceholderColor = if (isMatching) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.5f
                ),
                unfocusedPlaceholderColor = if (isMatching) Color.White.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.4f
                )
            ),
            trailingIcon = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Clear button - Shows when text is not empty
                    if (textValue.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                textValue = ""
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Effacer le texte",
                                tint = if (isMatching) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Reset to category name button
                    if (textValue != category?.nom && category?.nom != null) {
                        IconButton(
                            onClick = {
                                textValue = category.nom
                                repositorysMainSetter.upsert_M1Produit(
                                    produit.copy(
                                        nom_type_categorie = category.nom,
                                        dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Réinitialiser à la catégorie",
                                tint = if (isMatching) Color.White else MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    // Save button - Shows when text has changed from saved value
                    if (textValue != produit.nom_type_categorie) {
                        IconButton(
                            onClick = {
                                repositorysMainSetter.upsert_M1Produit(
                                    produit.copy(
                                        nom_type_categorie = textValue,
                                        dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Sauvegarder",
                                tint = if (isMatching) Color.White else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (textValue != produit.nom_type_categorie) {
                        repositorysMainSetter.upsert_M1Produit(
                            produit.copy(
                                nom_type_categorie = textValue,
                                dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                            )
                        )
                    }
                }
            )
        )
    }
}
