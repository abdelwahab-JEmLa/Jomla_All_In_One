package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import Z_CodePartageEntreApps.Modules.CameraHandler.CameraFABProtoJuin3
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun AddNewCouleur(
    modifier: Modifier = Modifier,
    produit: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1,
    size: Dp = 120.dp,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
) {
    var isEditing by remember { mutableStateOf(false) }
    var colorName by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val haptic = LocalHapticFeedback.current

    fun getNextColorIndex(): Int {
        val existingColors = viewModel.aCentralFacade.repositorysMainGetter
            .repo03CouleurProduitInfos.datasValue
            .filter { it.parentBProduitOldID == produit.id }

        for (i in 1..9) {
            val exists = existingColors.any { it.indexCouleurDansAncienProto == i }
            if (!exists) return i
        }
        return existingColors.size + 1
    }

    fun handleAddNewCouleur() {
        if (colorName.isNotBlank()) {
            val colorIndex = getNextColorIndex()

            val newCouleur = M3CouleurProduitInfos.get_default().copy(
                aAffiche = M3CouleurProduitInfos.Type.Nom,
                nomCouleurStrSiSonImageDispo = colorName.trim(),
                indexCouleurDansAncienProto = colorIndex,
                parentBProduitOldID = produit.id,
                parentBProduitInfosKeyID = produit.keyID,
                parentId1ProduitInfosDebugName = produit.nom,
                processPositioningInFactory = M3CouleurProduitInfos.ProcessPositioningInFactory.CreeDepuitRechercheRapid
            )

            viewModel.aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.addOrUpdateData(
                newCouleur
            )

            val updatedProduit = when (colorIndex) {
                1 -> produit.copy(couleur1 = newCouleur.keyID)
                2 -> produit.copy(couleur2 = newCouleur.keyID)
                3 -> produit.copy(couleur3 = newCouleur.keyID)
                4 -> produit.copy(couleur4 = newCouleur.keyID)
                5 -> produit.copy(couleur5 = newCouleur.keyID)
                6 -> produit.copy(couleur6 = newCouleur.keyID)
                7 -> produit.copy(couleur7 = newCouleur.keyID)
                8 -> produit.copy(couleur8 = newCouleur.keyID)
                9 -> produit.copy(couleur9 = newCouleur.keyID)
                else -> produit // fallback, should not happen with getNextColorIndex logic
            }

            repositorysMainSetter.upsert_M1Produit(updatedProduit)

            colorName = ""
            isEditing = false

            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    fun handleAddCameraColor() {
        val colorIndex = getNextColorIndex()

        // Create a color with image type but no name (empty string)
        val newCouleur = M3CouleurProduitInfos.get_default().copy(
            aAffiche = M3CouleurProduitInfos.Type.Image,
            nomCouleurStrSiSonImageDispo = "", // No name for camera-captured color
            nomImageFichieSansEtansion = "${produit.id}_$colorIndex", // Generate image filename
            indexCouleurDansAncienProto = colorIndex,
            parentBProduitOldID = produit.id,
            parentBProduitInfosKeyID = produit.keyID,
            parentId1ProduitInfosDebugName = produit.nom,
            processPositioningInFactory = M3CouleurProduitInfos.ProcessPositioningInFactory.CreeDepuitRechercheRapid
        )

        viewModel.aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.addOrUpdateData(
            newCouleur
        )

        val updatedProduit = when (colorIndex) {
            1 -> produit.copy(couleur1 = newCouleur.keyID)
            2 -> produit.copy(couleur2 = newCouleur.keyID)
            3 -> produit.copy(couleur3 = newCouleur.keyID)
            4 -> produit.copy(couleur4 = newCouleur.keyID)
            5 -> produit.copy(couleur5 = newCouleur.keyID)
            6 -> produit.copy(couleur6 = newCouleur.keyID)
            7 -> produit.copy(couleur7 = newCouleur.keyID)
            8 -> produit.copy(couleur8 = newCouleur.keyID)
            9 -> produit.copy(couleur9 = newCouleur.keyID)
            else -> produit
        }

        repositorysMainSetter.upsert_M1Produit(updatedProduit)
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Card(
        modifier = modifier
            .size(size)
            .clickable {
                if (!isEditing) {
                    isEditing = true
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isEditing) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            }
        ),
        border = if (isEditing) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Camera button at top start
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .clickable {
                        handleAddCameraColor()
                    }
            ) {
                CameraFABProtoJuin3(
                    size = 24.dp,
                    aCentralFacade = aCentralFacade
                )
            }

            if (isEditing) {
                OutlinedTextField(
                    value = colorName,
                    onValueChange = { newText ->
                        // Capitalize first letter of each word
                        val capitalizedText = newText.split(" ").joinToString(" ") { word ->
                            if (word.isNotEmpty()) {
                                word.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase() else it.toString()
                                }
                            } else {
                                word
                            }
                        }
                        colorName = capitalizedText
                    },
                    placeholder = {
                        Text(
                            text = "Nom couleur",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            handleAddNewCouleur()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                LaunchedEffect(isEditing) {
                    if (isEditing) {
                        focusRequester.requestFocus()
                    }
                }

                LaunchedEffect(isEditing) {
                    if (isEditing) {
                        delay(30000) // 30 seconds
                        if (colorName.isBlank()) {
                            isEditing = false
                        }
                    }
                }
            } else {
                Text(
                    text = "nouvelle\ncouleur",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = 45f
                    },
                    maxLines = 2
                )
            }
        }
    }
}
