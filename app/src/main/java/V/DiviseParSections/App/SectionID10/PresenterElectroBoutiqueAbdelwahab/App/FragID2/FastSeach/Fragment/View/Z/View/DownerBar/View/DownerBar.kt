package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.DownerBar.View

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun PrevDB() {
    DownerBar()
}

@SuppressLint("AutoboxingStateCreation")
@Composable
fun DownerBar() {
    val default_Affiche_Est_Boit by remember { mutableStateOf(true) }

    var actuelle_Affiche_Est_Boit by remember { mutableStateOf(default_Affiche_Est_Boit) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CartonDisplayer(
                modifier = Modifier.weight(1f),
                actuelle_Affiche_Est_Boit,
            ) {
                actuelle_Affiche_Est_Boit = false
            }
            UnitParBoit(
                modifier = Modifier.weight(1f),
                actuelle_Affiche_Est_Boit,
            ) {
                actuelle_Affiche_Est_Boit = true
            }
        }
    }
}

@Composable
fun UnitParBoit(
    modifier: Modifier = Modifier,
    actuelle_Affiche: Boolean,
    onClick_actuelle_Affiche: () -> Unit,
) {
    var quantite by remember { mutableStateOf(0) }
    val nom = "Boit"

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.Blue.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Unité par $nom",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "$quantite Unite/Par $nom",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Text(
                    text = "Quantité par $nom",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (quantite > 0) quantite--
                    onClick_actuelle_Affiche()
                }) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Diminuer quantité",
                        tint = Color.Red
                    )
                }

                Text(
                    text = "$quantite",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    quantite++
                    onClick_actuelle_Affiche()
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Augmenter quantité",
                        tint = Color.Green
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (actuelle_Affiche) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Toggle Actuelle Affiche",
                    tint = if (actuelle_Affiche) Color.Yellow else Color.Gray
                )
            }
        }
    }
}

@Composable
private fun CartonDisplayer(
    modifier: Modifier = Modifier,
    actuelle_Affiche_Est_Boit: Boolean,
    onClick_actuelle_Affiche: () -> Unit,
) {
    var quantite_Carton by remember { mutableStateOf(5) }
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.Blue.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Carton",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "$quantite_Carton Boit/Par Carton",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Text(
                    text = "Quantité par carton",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (quantite_Carton > 0) quantite_Carton--
                    onClick_actuelle_Affiche()
                }) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Diminuer quantité carton",
                        tint = Color.Red
                    )
                }

                Text(
                    text = "$quantite_Carton",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    quantite_Carton++
                    onClick_actuelle_Affiche()
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Augmenter quantité carton",
                        tint = Color.Green
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (!actuelle_Affiche_Est_Boit) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Toggle Actuelle Affiche",
                    tint = if (!actuelle_Affiche_Est_Boit) Color.Yellow else Color.Gray
                )
            }
        }
    }
}
