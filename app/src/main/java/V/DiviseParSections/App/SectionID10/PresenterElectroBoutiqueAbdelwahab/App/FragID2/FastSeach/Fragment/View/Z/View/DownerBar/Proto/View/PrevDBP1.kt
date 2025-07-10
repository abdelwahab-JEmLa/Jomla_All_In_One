package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.DownerBar.Proto.View

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.DownerBar.Proto.View.Card.Card_Affiche_Infos
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Preview
@Composable
private fun PrevDBP2() {
    DownerBarP2()
}

@SuppressLint("AutoboxingStateCreation")
@Composable
private fun DownerBarP2() {
    val produit by remember { mutableStateOf(Produit()) }
    val vent by remember { mutableStateOf(Vent()) }

    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Vent_Quantitys(
            produit = produit,
            vent = vent
        )

        Card_Affiche_Infos { qtyBoitParCarton ->
            produit.updateQtyBoitParCarton(qtyBoitParCarton)
        }
    }
}

@Stable
class Produit {
    private val _qty_Boit_Par_Carton = MutableStateFlow(10)
    val qty_Boit_Par_Carton: StateFlow<Int> = _qty_Boit_Par_Carton

    fun updateQtyBoitParCarton(newValue: Int) {
        _qty_Boit_Par_Carton.value = newValue
    }
}

@Stable
class Vent {
    private val _qty_Boit_Vendu = MutableStateFlow(0)
    val qty_Boit_Vendu: StateFlow<Int> = _qty_Boit_Vendu

    var qty_Carton_Vendu by mutableStateOf(0)

    fun updateQty_Boit_Vendu(newValue: Int) {
        _qty_Boit_Vendu.value = newValue
    }

    fun update_qty_Carton_Vendu(newValue: Int) {
        qty_Carton_Vendu = newValue
    }
}

@Composable
private fun Vent_Quantitys(
    modifier: Modifier = Modifier,
    produit: Produit,
    vent: Vent
) {
    var vent_quantityCarton by remember { mutableStateOf(0) }
    val qtyBoitParCarton by produit.qty_Boit_Par_Carton.collectAsState()
    val qtyVendu by vent.qty_Boit_Vendu.collectAsState()

    val quantityBoit_On_Vent_Carton by remember {
        derivedStateOf {
            vent_quantityCarton * qtyBoitParCarton
        }
    }

    // Calculate carton quantity based on individual boxes sold
    val quantity_Carton_On_Vent_Par_Boit by remember {
        derivedStateOf {
            if (qtyBoitParCarton > 0) {
                qtyVendu / qtyBoitParCarton
            } else {
                0
            }
        }
    }

    // Check if individual boxes can form complete cartons
    val hasPartialCarton by remember {
        derivedStateOf {
            if (qtyBoitParCarton > 0) {
                qtyVendu % qtyBoitParCarton != 0
            } else {
                false
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Vent Quantitys",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Vent_Par_Carton(
                    modifier = Modifier.weight(1f),
                    hasPartialCarton = hasPartialCarton,
                    qtyBoitParCarton = qtyBoitParCarton,
                    qtyVendu = qtyVendu
                ) { newQuantity ->
                    vent_quantityCarton = newQuantity
                }

                Vent_Par_Boit(
                    modifier = Modifier.weight(1f),
                    quantityBoit_On_Vent_Carton
                ) { newQuantity ->
                    vent.updateQty_Boit_Vendu(qtyVendu + newQuantity)
                    vent.update_qty_Carton_Vendu(quantity_Carton_On_Vent_Par_Boit)
                }
            }

            if (qtyVendu > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Quantité vendue: $qtyVendu boîtes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Cartons complets: $quantity_Carton_On_Vent_Par_Boit",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Button(
                onClick = {
                    vent.updateQty_Boit_Vendu(quantityBoit_On_Vent_Carton)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                shape = RoundedCornerShape(8.dp),
                enabled = quantityBoit_On_Vent_Carton > 0
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirmer vente",
                    tint = Color.White
                )
                Text(
                    text = "Confirmer la vente",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun Vent_Par_Carton(
    modifier: Modifier = Modifier,
    hasPartialCarton: Boolean,
    qtyBoitParCarton: Int,
    qtyVendu: Int,
    onClick_Vent_Change_PurchaseQty: (Int) -> Unit,
) {
    var quantity by remember { mutableStateOf(0) }

    // Determine if the carton display should be grayed out
    val shouldGrayOut = hasPartialCarton && qtyBoitParCarton > 0

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (shouldGrayOut)
                Color.Gray.copy(alpha = 0.3f)
            else
                Color.Blue.copy(alpha = 0.1f)
        ),
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
                color = if (shouldGrayOut) Color.Gray else Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "$quantity Boit/Par Carton",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (shouldGrayOut) Color.Gray else Color.Black
                )
                Text(
                    text = "Quantité par carton",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                if (shouldGrayOut) {
                    Text(
                        text = "Cartons incomplets détectés",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (quantity > 0) {
                            quantity--
                            if (quantity == 0) {
                                onClick_Vent_Change_PurchaseQty(0)
                            } else {
                                onClick_Vent_Change_PurchaseQty(quantity)
                            }
                        }
                    },
                    enabled = !shouldGrayOut
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Diminuer quantité carton",
                        tint = if (shouldGrayOut) Color.Gray else Color.Red
                    )
                }

                Text(
                    text = "$quantity",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (shouldGrayOut) Color.Gray else Color.Black,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        quantity++
                        onClick_Vent_Change_PurchaseQty(quantity)
                    },
                    enabled = !shouldGrayOut
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Augmenter quantité carton",
                        tint = if (shouldGrayOut) Color.Gray else Color.Green
                    )
                }
            }
        }
    }
}

@Composable
private fun Vent_Par_Boit(
    modifier: Modifier = Modifier,
    int_quantity: Int,
    onClick_To_Vent: (Int) -> Unit,
) {
    var quantity by remember { mutableStateOf(0) }

    val nom = "Vent_Par_Boit"

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
                    text = "$quantity Boit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Text(
                    text = "Quantité calculée: $int_quantity",
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
                    if (quantity > 0) {
                        quantity--
                        onClick_To_Vent(quantity)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Diminuer quantité",
                        tint = Color.Red
                    )
                }

                Text(
                    text = "$quantity",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    quantity++
                    onClick_To_Vent(quantity)
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Augmenter quantité",
                        tint = Color.Green
                    )
                }
            }
        }
    }
}

