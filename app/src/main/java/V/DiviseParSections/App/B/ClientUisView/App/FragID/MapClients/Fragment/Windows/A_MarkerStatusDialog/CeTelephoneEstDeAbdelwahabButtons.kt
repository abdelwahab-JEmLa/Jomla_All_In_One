package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
 fun CeTelephoneEstDeAbdelwahabButtons(
    coroutineScope: CoroutineScope,
    relatedClients: B_ClientDataBase?,
    viewModel: ViewModel_MapClients_App2FragID1,
    onDismiss: () -> Unit,
    context: Context,
) {
    // Cible button
    val Cible = B_ClientDataBase.DernierEtatAAffiche.Cible
    FilledTonalButton(
        onClick = {
            coroutineScope.launch {
                relatedClients?.actuelleEtat = Cible
                viewModel.updateData(relatedClients!!)
                onDismiss()
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(
                ContextCompat.getColor(
                    context,
                    Cible.color
                )
            ).copy(alpha = 0.2f),
            contentColor = Color(
                ContextCompat.getColor(
                    context,
                    Cible.color
                )
            )
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = Cible.nomArabe,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(Cible.nomArabe)
        }
    }

    // CIBLE_PRIORITE_2 button
    val CIBLE_PRIORITE_2 = B_ClientDataBase.DernierEtatAAffiche.CIBLE_PRIORITE_2
    FilledTonalButton(
        onClick = {
            coroutineScope.launch {
                relatedClients?.actuelleEtat = CIBLE_PRIORITE_2
                viewModel.updateData(relatedClients!!)
                onDismiss()
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(
                ContextCompat.getColor(
                    context,
                    CIBLE_PRIORITE_2.color
                )
            ).copy(alpha = 0.2f),
            contentColor = Color(
                ContextCompat.getColor(
                    context,
                    CIBLE_PRIORITE_2.color
                )
            )
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = CIBLE_PRIORITE_2.nomArabe,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(CIBLE_PRIORITE_2.nomArabe)
        }
    }

    // CIBLE_POUR_2 button
    val CIBLE_POUR_2 = B_ClientDataBase.DernierEtatAAffiche.CIBLE_POUR_2
    FilledTonalButton(
        onClick = {
            coroutineScope.launch {
                relatedClients?.actuelleEtat = CIBLE_POUR_2
                viewModel.updateData(relatedClients!!)
                onDismiss()
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(
                ContextCompat.getColor(
                    context,
                    CIBLE_POUR_2.color
                )
            ).copy(alpha = 0.2f),
            contentColor = Color(
                ContextCompat.getColor(
                    context,
                    CIBLE_POUR_2.color
                )
            )
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = CIBLE_POUR_2.nomArabe,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(CIBLE_POUR_2.nomArabe)
        }
    }

    // A_EVITE button
    val A_EVITE = B_ClientDataBase.DernierEtatAAffiche.A_EVITE
    FilledTonalButton(
        onClick = {
            coroutineScope.launch {
                relatedClients?.actuelleEtat = A_EVITE
                viewModel.updateData(relatedClients!!)
                onDismiss()
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(
                ContextCompat.getColor(
                    context,
                    A_EVITE.color
                )
            ).copy(alpha = 0.2f),
            contentColor = Color(
                ContextCompat.getColor(
                    context,
                    A_EVITE.color
                )
            )
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = A_EVITE.nomArabe,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(A_EVITE.nomArabe)
        }
    }
}
