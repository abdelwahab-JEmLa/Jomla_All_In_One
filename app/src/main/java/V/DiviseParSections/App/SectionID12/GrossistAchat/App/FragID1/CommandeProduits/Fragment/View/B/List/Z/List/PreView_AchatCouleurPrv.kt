package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject

@Preview
@Composable
private fun View_AchatCouleurPrv(
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
) {
    val firstAchatOperation by remember {
        derivedStateOf {
            repositorysMainGetter.repo11AchatOperation.datasValue.firstOrNull()
        }
    }

    firstAchatOperation?.let { data ->
        View_AchatCouleur(relative_M11AchatOperation = data)
    } ?: run {
        // Show loading state
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Aucune donnée d'achat disponible",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}
