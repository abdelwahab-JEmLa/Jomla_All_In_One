package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.Z.List.UI.Proto.View.Proto

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.Z.List.UI.Proto.View.Proto.Card.Card_Affiche_Infos
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.M3CouleurProduitInfos
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
private fun PrevDBP2(

) {
    /* val viewModel= ViewModelsProduit_T1 = koinViewModel()

      DownerBarP2(produit = viewModel.aCentralFacade.repositorysMainGetter.repoM1ProduitInfos.datasValue.firstOrNull {
          it.nom.contains("liya")
      }, m3Couleur = viewModel.aCentralFacade.repositorysMainGetter.repo3CouleurProduitInfos.datasValue.find {
          it.parentBProduitInfosKeyID == viewModel.aCentralFacade.repositorysMainGetter.repoM1ProduitInfos.datasValue.firstOrNull {
              it.nom.contains("liya")
          }?.keyID
      }, vent = viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue.find {
          it.parentM3CouleurProduitInfosKeyID == viewModel.aCentralFacade.repositorysMainGetter.repo3CouleurProduitInfos.datasValue.find {
              it.parentBProduitInfosKeyID == viewModel.aCentralFacade.repositorysMainGetter.repoM1ProduitInfos.datasValue.firstOrNull {
                  it.nom.contains("liya")
              }?.keyID
          }?.keyID
      })       */
}

@SuppressLint("AutoboxingStateCreation")
@Composable
fun DownerBarP2(
    viewModel: ViewModelsProduit_T1 = koinViewModel(),
    produit: ArticlesBasesStatsTable?,
    m3Couleur: M3CouleurProduitInfos?,
    vent: M10OperationVentCouleur?,
) {
    // Reduced vertical spacing and made it more compact
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(vertical = 4.dp), // Reduced from 8.dp
        verticalArrangement = Arrangement.spacedBy(8.dp) // Reduced from 16.dp
    ) {
        Vent_Quantitys(
            produit = produit,
            m3Couleur = m3Couleur,
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
    vent: M10OperationVentCouleur?,
    m3Couleur: M3CouleurProduitInfos?,
    produit: ArticlesBasesStatsTable?,
    vm: ViewModelsProduit_T1,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight() // Changed from default height
            .padding(horizontal = 8.dp, vertical = 4.dp), // Reduced padding
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Reduced elevation
        shape = RoundedCornerShape(8.dp) // Reduced corner radius
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // Reduced from 16.dp
            verticalArrangement = Arrangement.spacedBy(8.dp) // Reduced from 12.dp
        ) {
            // Smaller title
            Text(
                text = "Vent Quantitys Du Vent ${vent?.parentM1ProduitDebugInfos}",
                style = MaterialTheme.typography.titleSmall, // Changed from titleMedium
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Compact controls row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp), // Fixed height to prevent expansion
                horizontalArrangement = Arrangement.spacedBy(8.dp), // Reduced spacing
                verticalAlignment = Alignment.CenterVertically
            ) {
                Vent_Par_Carton(
                    m3Couleur = m3Couleur,
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

            // Compact quantity display
            if (vent_quantityParBoit != null && vent_quantityParBoit > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(), // Wrap content instead of expanding
                    colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(6.dp) // Smaller corner radius
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp), // Reduced padding
                        verticalArrangement = Arrangement.spacedBy(2.dp) // Reduced spacing
                    ) {
                        Text(
                            text = "Quantité: $vent_quantityParBoit boîtes",
                            style = MaterialTheme.typography.bodySmall, // Smaller text
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Cartons: $vent_quantity_Par_Carton",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Compact confirm button
            if (vent != null && vent_quantityParBoit != null && vent_quantityParBoit > 0) {
                Button(
                    onClick = {
                        vm.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.addOrUpdateData(
                            vent.copy(
                                etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ChoisiQuantityConfirme
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp), // Fixed height for button
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    shape = RoundedCornerShape(6.dp) // Smaller corner radius
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Confirmer vente",
                            tint = Color.White
                        )
                        Text(
                            text = "Confirmer",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
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
    m3Couleur: M3CouleurProduitInfos?,
    produit: ArticlesBasesStatsTable?,
    viewModel: ViewModelsProduit_T1,
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

    Card(
        modifier = modifier.height(80.dp), // Fixed height
        colors = CardDefaults.cardColors(
            containerColor = if (shouldGrayOut)
                Color.Gray.copy(alpha = 0.3f)
            else
                Color.Blue.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(6.dp) // Smaller corner radius
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp), // Reduced padding
            verticalArrangement = Arrangement.spacedBy(4.dp) // Reduced spacing
        ) {
            Text(
                text = "Carton",
                style = MaterialTheme.typography.labelSmall, // Smaller text
                color = if (shouldGrayOut) Color.Gray else Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "$vent_quantity_Par_Carton Boit",
                style = MaterialTheme.typography.bodySmall,
                color = if (shouldGrayOut) Color.Gray else Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

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
                        contentDescription = "Diminuer",
                        tint = if (shouldGrayOut) Color.Gray else Color.Red
                    )
                }

                Text(
                    text = "$vent_quantity_Par_Carton",
                    style = MaterialTheme.typography.titleSmall,
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
                        contentDescription = "Augmenter",
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
    vm: ViewModelsProduit_T1,
    modifier: Modifier = Modifier,
) {
    val vent_quantity_Par_Boit =
        (produit?.quantite_Boit_Par_Carton ?: 1) * (vent?.quantity_Par_Carton ?: 0)
    val m3Couleur =
        vm.aCentralFacade.repositorysMainGetter.repo3CouleurProduitInfos.datasValue.find {
            it.parentBProduitInfosKeyID == produit?.keyID
        }

    Card(
        modifier = modifier.height(80.dp), // Fixed height
        colors = CardDefaults.cardColors(containerColor = Color.Blue.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(6.dp) // Smaller corner radius
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp), // Reduced padding
            verticalArrangement = Arrangement.spacedBy(4.dp) // Reduced spacing
        ) {
            Text(
                text = "Unité",
                style = MaterialTheme.typography.labelSmall, // Smaller text
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "$vent_quantity_Par_Boit Boit",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

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
                        contentDescription = "Diminuer",
                        tint = Color.Red
                    )
                }

                Text(
                    text = "$vent_quantity_Par_Boit",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    lanceVent(vm, produit, m3Couleur, vent_quantity_Par_Boit + 1)
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Augmenter",
                        tint = Color.Green
                    )
                }
            }
        }
    }
}

private fun lanceVent(
    viewModel: ViewModelsProduit_T1,
    produit: ArticlesBasesStatsTable?,
    m3Couleur: M3CouleurProduitInfos?,
    newQuant: Int
) {
    val setRepositorys = viewModel.aCentralFacade.repositorysMainSetter
    val getterFocusedVarsHandlerFacade =
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val parentM1ProduitDebugInfos = produit?.getDebugInfos() ?: "null"

    val findVent =
        viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue.find {
            it.parentM3CouleurProduitInfosKeyID == m3Couleur?.keyID
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
