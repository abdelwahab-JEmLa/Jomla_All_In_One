package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.A.Main.Components.Ui

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
 fun Dialog_Filter_Client(
    uiState: GrossistAchatSec12FragID1_ViewModel.UiState,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sélectionner un Client",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Client List
                LazyColumn_Client(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun LazyColumn_Client(
    modifier: Modifier = Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel
) {
    val clients = viewModel.aCentralFacade.repositorysMainGetter.repo2Client.datasValue
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(clients) { client ->
            Item_Client(
                client = client,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun Item_Client(
    modifier: Modifier = Modifier,
    client: M2Client,
    viewModel: GrossistAchatSec12FragID1_ViewModel
) {
    val repo11AchatOperation = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation

    /*  val isSelected = isSelected     //<--
      //TODO(1): regle pour que le selected soit  Client(val m2Client: M2Client)
      Card(
          modifier = modifier
              .fillMaxWidth()
              .clickable {
                  repo11AchatOperation.filterQuery.value =
                      Repo11AchatOperation.FilterQuery.Client(client)  //->
                  //TODO(FIXME):Fix erreur Type mismatch.
                  //Required:
                  //Repo11AchatOperation.FilterQuery.NO_FILTER
                  //Found:
                  //Repo11AchatOperation.FilterQuery.Clie
              },
          colors = CardDefaults.cardColors(
              containerColor = if (isSelected)
                  MaterialTheme.colorScheme.primaryContainer
              else
                  MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.cardElevation(
              defaultElevation = if (isSelected) 8.dp else 2.dp
          )
      ) {
          Row(
              modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
          ) {
              Column {
                  Text(
                      text = client.nom,
                      style = MaterialTheme.typography.bodyLarge,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSelected)
                          MaterialTheme.colorScheme.onPrimaryContainer
                      else
                          MaterialTheme.colorScheme.onSurface
                  )
              }

              if (isSelected) {
                  Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = "Sélectionné",
                      tint = MaterialTheme.colorScheme.onPrimaryContainer
                  )
              }
          }
      }           */
}
