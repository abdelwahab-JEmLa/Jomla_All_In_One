package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun DropDownItem_WhenIts_FragFastVent_4(
    nomFun: String,
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    context: Context = LocalContext.current
) {
    // Get current state
    val currentAppCompt = focusedValuesGetter.currentActive_M9AppCompt
    val image_detail_produit_s_affiche = currentAppCompt?.image_detail_produit_s_affiche ?: false

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (image_detail_produit_s_affiche) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = if (image_detail_produit_s_affiche) Icons.Default.SortByAlpha else Icons.Default.Sort,
                    contentDescription = null,
                    tint = if (image_detail_produit_s_affiche) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            },
            text = {
                Text(
                    text = if (image_detail_produit_s_affiche)
                        "إخفاء تفاصيل الصور" // Hide image details
                    else
                        "عرض تفاصيل الصور", // Show image details
                    color = if (image_detail_produit_s_affiche) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            },
            onClick = {
                // Toggle the image_detail_produit_s_affiche state
                currentAppCompt?.let { appCompt ->
                    val updatedAppCompt = appCompt.copy(
                        image_detail_produit_s_affiche = !image_detail_produit_s_affiche
                    )
                    repositorysMainSetter.update_M9AppCompt(updatedAppCompt)

                    Toast.makeText(
                        context,
                        if (!image_detail_produit_s_affiche)
                            "تم تفعيل عرض تفاصيل الصور" // Image details enabled
                        else
                            "تم إخفاء تفاصيل الصور", // Image details hidden
                        Toast.LENGTH_SHORT
                    ).show()
                }

                onDismissDropdown()
            }
        )
    }
}
