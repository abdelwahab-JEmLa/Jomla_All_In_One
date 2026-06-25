package V.DiviseParSections.App._0.Navigation.Main_DropDown

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.R

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics

@Composable
fun FabButton(
    showWarningState: Boolean,
    isFabVisible: Boolean,
    its_Targeted_Frag: Boolean,
    affiche_ProduitDataBaseEdites: Boolean,
    onToggleFabVisibility: () -> Unit,
    onShowDropdown: () -> Unit,
    onToggleProduitDataBaseEdites: (Boolean) -> Unit,
    on_pour_update_affiche_buttons_lien_unite_couleur_au_couleut_parent: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier
            .offset(y = (-28).dp)
            .size(56.dp),
        shape = CircleShape,
    ) {
        Box {
            if (showWarningState) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFDC2626), // Red-600
                                    Color(0xFFB91C1C)  // Red-700
                                )
                            ),
                            shape = CircleShape
                        )
                        .clickable {
                            when (its_Targeted_Frag) {
                                false -> dropdownExpanded = true
                                true -> onShowDropdown()
                            }
                        }
                        .semantics(mergeDescendants = true) {
                            set(value = affiche_ProduitDataBaseEdites, key = SemanticsPropertyKey("affiche_ProduitDataBaseEdites"))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else {
                // Original logo image
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            when (its_Targeted_Frag) {
                                false -> dropdownExpanded = true
                                true -> onShowDropdown()
                            }
                        },
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Icon(
                    imageVector = if (isFabVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle FAB",
                    modifier = Modifier.align(Alignment.Center),
                    tint = Color.White
                )
            }

            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Affiche édition produits")
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = affiche_ProduitDataBaseEdites,
                                onCheckedChange = { newVal ->
                                    onToggleProduitDataBaseEdites(newVal)
                                    on_pour_update_affiche_buttons_lien_unite_couleur_au_couleut_parent(newVal)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    },
                    onClick = {
                        val newVal = !affiche_ProduitDataBaseEdites
                        onToggleProduitDataBaseEdites(newVal)
                        on_pour_update_affiche_buttons_lien_unite_couleur_au_couleut_parent(newVal)
                        dropdownExpanded = false
                    }
                )
            }
        }
    }
}
