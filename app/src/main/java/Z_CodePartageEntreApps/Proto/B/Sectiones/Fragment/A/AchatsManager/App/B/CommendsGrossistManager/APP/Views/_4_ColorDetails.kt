package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.View.A_GlideDisplayImageByKeyId_Proto_4_11
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ColorDetails_APP2_ID_2(
    composeKeyVID: Long,
    _0_HeadOfRepositorys_Repository_Model: _0_0_HeadOfRepositorys_Model,
) {
    val relative_1_1_CouleurAcheteOperation = _0_HeadOfRepositorys_Repository_Model
        ._1_1_CouleurAcheteOperation_Repository
        .modelDatasSnapList
        .find { it.vid == composeKeyVID }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "OpID (${composeKeyVID})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Couleur ID: ${relative_1_1_CouleurAcheteOperation?.couleurId_ParentVID ?: "N/A"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            A_GlideDisplayImageByKeyId_Proto_4_11(
                produitVID=relative_1_1_CouleurAcheteOperation?.parentProduitAchateOperationVID!! ,
                couleurVID = relative_1_1_CouleurAcheteOperation.couleurId_ParentVID
            )
        }
    }
}
