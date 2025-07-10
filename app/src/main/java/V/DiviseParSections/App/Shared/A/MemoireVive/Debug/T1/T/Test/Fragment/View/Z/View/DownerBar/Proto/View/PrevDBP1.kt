package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.DownerBar.Proto.View

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.DownerBar.Proto.View.Card.Card_Affiche_Infos
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.ViewModel.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.M3CouleurProduitInfos
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun PrevDBP2() {
    DownerBarP2()
}

@SuppressLint("AutoboxingStateCreation")
@Composable
private fun DownerBarP2(
    viewModel: ViewModelMainFastSearchProduitPourVent = koinViewModel(),
) {
    val produit = viewModel.aCentralFacade.repositorysMainGetter.repoM1ProduitInfos.datasValue.firstOrNull { it.nom.contains("liya") }
    val m3Couleur = viewModel.aCentralFacade.repositorysMainGetter.repo3CouleurProduitInfos.datasValue.find {
        it.parentBProduitInfosKeyID == produit?.keyID
    }
    val vent =viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue.find {
        it.parentM3CouleurProduitInfosKeyID==m3Couleur?.keyID
    }

    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Vent_Quantitys(
            produit = produit,
            vm = viewModel,
            vent = vent,
        )
        Card_Affiche_Infos(
            produit = produit,
            vm = viewModel,
        )
    }
}

@Composable
private fun Vent_Quantitys(
    modifier: Modifier = Modifier,
    produit: ArticlesBasesStatsTable?,
    vm: ViewModelMainFastSearchProduitPourVent,
    vent: M10OperationVentCouleur?,
) {
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
                    vent = vent,
                    produit = produit,
                    viewModel = vm,
                    modifier = Modifier.weight(1f),
                )

                Vent_Par_Boit(
                    vent = vent,
                    produit = produit,
                    vm = vm,
                    modifier = Modifier.weight(1f),
                )
            }

            val vent_quantityParBoit = vent?.quantity_Par_Boit
            val vent_quantity_Par_Carton = vent?.quantity_Par_Carton

            if (vent_quantityParBoit != null) {
                if (vent_quantityParBoit > 0) {
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
                                text = "Quantité vendue: $vent_quantityParBoit boîtes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "Cartons complets: $vent_quantity_Par_Carton",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            if (vent != null) {
                if (vent_quantityParBoit != null) {
                    Button(
                        onClick = {
                            vm.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.addOrUpdateData(vent.copy(
                                etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ChoisiQuantityConfirme
                            ))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        shape = RoundedCornerShape(8.dp),
                        enabled = vent_quantityParBoit > 0
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
    }
}

@Composable
private fun Vent_Par_Carton(
    vent: M10OperationVentCouleur?,
    produit: ArticlesBasesStatsTable?,
    viewModel: ViewModelMainFastSearchProduitPourVent,
    modifier: Modifier = Modifier,
) {
    val vent_quantity_Par_Carton = vent?.quantity_Par_Carton ?: 0

    val hasPartialCarton by remember {
        derivedStateOf {
            val quantiteParCarton = produit?.quantite_Boit_Par_Carton ?: 0
            if (quantiteParCarton > 0) {
                vent_quantity_Par_Carton % quantiteParCarton != 0
            } else {
                false
            }
        }
    }

    val shouldGrayOut = hasPartialCarton && vent_quantity_Par_Carton > 0

    val m3Couleur = viewModel.aCentralFacade.repositorysMainGetter.repo3CouleurProduitInfos.datasValue.find {
        it.parentBProduitInfosKeyID == produit?.keyID
    }

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
                    text = "$vent_quantity_Par_Carton Boit/Par Carton",
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
                        lanceVent(viewModel, produit, m3Couleur, vent_quantity_Par_Carton - 1)
                    },
                    enabled = !shouldGrayOut && vent_quantity_Par_Carton > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Diminuer quantité carton",
                        tint = if (shouldGrayOut) Color.Gray else Color.Red
                    )
                }

                Text(
                    text = "$vent_quantity_Par_Carton",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (shouldGrayOut) Color.Gray else Color.Black,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        lanceVent(viewModel, produit, m3Couleur, vent_quantity_Par_Carton + 1)
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
    vent: M10OperationVentCouleur?,
    produit: ArticlesBasesStatsTable?,
    vm: ViewModelMainFastSearchProduitPourVent,
    modifier: Modifier = Modifier,
) {
    val vent_quantity_Par_Boit = (produit?.quantite_Boit_Par_Carton ?: 1) * (vent?.quantity_Par_Carton ?: 1)
    val m3Couleur = vm.aCentralFacade.repositorysMainGetter.repo3CouleurProduitInfos.datasValue.find {
        it.parentBProduitInfosKeyID == produit?.keyID
    }

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
                    text = "$vent_quantity_Par_Boit Boit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Text(
                    text = "Quantité calculée: $vent_quantity_Par_Boit",
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
                    if (vent_quantity_Par_Boit > 0) {
                        lanceVent(vm, produit, m3Couleur, vent_quantity_Par_Boit - 1)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Diminuer quantité",
                        tint = Color.Red
                    )
                }

                Text(
                    text = "$vent_quantity_Par_Boit",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    lanceVent(vm, produit, m3Couleur, vent_quantity_Par_Boit + 1)
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

private fun lanceVent(
    viewModel: ViewModelMainFastSearchProduitPourVent,
    produit: ArticlesBasesStatsTable?,
    m3Couleur: M3CouleurProduitInfos?,
    newQuant: Int
) {
    val setRepositorys = viewModel.aCentralFacade.repositorysMainSetter
    val getterFocusedVarsHandlerFacade = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val parentM1ProduitDebugInfos = produit?.getDebugInfos() ?: "null"

    val findVent =viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue.find {
        it.parentM3CouleurProduitInfosKeyID==m3Couleur?.keyID
    }

    val defaultM10Vent = m3Couleur?.let {
        getterFocusedVarsHandlerFacade.getDefaultM10VentOperation()?.copy(
            parentM1ProduitInfosKeyId = produit?.keyID ?: "null",
            parentM1ProduitDebugInfos = parentM1ProduitDebugInfos,
            parentM3CouleurProduitInfosKeyID = "key", // Replace with actual key extraction
            parentM3CouleurProduitDebugInfos = parentM1ProduitDebugInfos + "indexCouleurDansAncienProto", // Replace with actual property
            quantity_Par_Boit = newQuant
        )
    }

    // Update existing or create new vent operation
    findVent?.let { existingVent ->
        setRepositorys.update_IfExist_M10OperationVentCouleur(
            existingVent.copy(
                quantity_Par_Boit = if (newQuant > 0) newQuant else 0,
                quantity_Par_Carton = if (newQuant > 0) newQuant else 0
            )
        )
    } ?: run {
        defaultM10Vent?.let { defaultVent ->
            setRepositorys.add_New_M10OperationVentCouleur(defaultVent)
        }
    }
}
