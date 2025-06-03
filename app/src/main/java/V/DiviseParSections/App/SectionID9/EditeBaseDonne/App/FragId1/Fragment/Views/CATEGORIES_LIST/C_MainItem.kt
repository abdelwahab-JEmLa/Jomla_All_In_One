package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.CATEGORIES_LIST

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.CATEGORIES_LIST.Dialogs.CategorySelectionDialog
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.CategoriesTabelle
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.DisponibilityEtates
import Z_CodePartageEntreApps.Modules.Glide.A_GlideDisplayImageByKeyId_Proto_5
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainItemEditeCategories(
    produit: ArticlesBasesStatsTable,
    availableCategories: List<Long>,
    onCategoryChanged: (ArticlesBasesStatsTable) -> Unit,
    modifier: Modifier = Modifier,
    categoriesMap: Map<Long, CategoriesTabelle> = emptyMap(),
    onAddCategory: ((String) -> Unit)? = null,
    onUpdateCategory: ((Long, String) -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.clickable { showDialog = true },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            A_GlideDisplayImageByKeyId_Proto_5(
                produitVID = produit.id,
                modifier = Modifier.weight(1f),
                produitNom = produit.nom,
                size = 80.dp,
                product = produit,
                refreshImage = produit.actualiseSonImageTest2
            )
            Text(
                text = produit.nom,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Add availability toggle button
            DisponibilityToggleButton(
                currentState = produit.disponibilityEtates,
                onToggle = {
                    val updatedProduct = produit.toggleDisponibilityEtates()
                    onCategoryChanged(updatedProduct)
                },
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }

    if (showDialog) {
        CategorySelectionDialog(
            product = produit,
            onCategorySelected = { newId ->
                onCategoryChanged(produit.copy(idParentCategorie = newId))
                showDialog = false
            },
            onDismiss = { showDialog = false },
            onAddCategory = onAddCategory,
            onUpdateCategory = onUpdateCategory,
            categoriesMap = categoriesMap,
            availableCategories = availableCategories
        )
    }
}
@Composable
fun DisponibilityToggleButton(
    currentState: DisponibilityEtates,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (color, icon, text) = when (currentState) {
        DisponibilityEtates.DISPO -> Triple(
            Color.Green,
            "✓",
            "Dispo"
        )
        DisponibilityEtates.NON_DISPO -> Triple(
            Color.Red,
            "✗",
            "Non Dispo"
        )
        DisponibilityEtates.PETITE_PROBABILITY -> Triple(
            Color.Blue,
            "?",
            "Possible"
        )
    }

    Button(
        onClick = onToggle,
        modifier = modifier.height(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.2f),
            contentColor = color
        ),
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$icon $text",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp
        )
    }
}
