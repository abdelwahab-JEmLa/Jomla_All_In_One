package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.But1_Floating_Separated_FragMap_Button_1

import EntreApps.Shared.Models.Home.ActiveCentralValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.ui.graphics.vector.ImageVector

fun getModeIcon(mode: ActiveCentralValues.Click_On_Marque): ImageVector = when (mode) {
   ActiveCentralValues.Click_On_Marque.Standart -> Icons.Default.Info
   ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients -> Icons.Default.Add
   ActiveCentralValues.Click_On_Marque.Affiche_OnCommand_VentPeriod_Transaction -> Icons.Default.ShoppingCart
   ActiveCentralValues.Click_On_Marque.Lence_New_Command -> Icons.Default.Add
   ActiveCentralValues.Click_On_Marque.Call -> Icons.Default.Call
   ActiveCentralValues.Click_On_Marque.Navigate -> Icons.Default.Explore
   ActiveCentralValues.Click_On_Marque.Marck_Ferme -> Icons.Default.Close
   ActiveCentralValues.Click_On_Marque.Marck_Command_Livret -> Icons.Default.LocalShipping
   ActiveCentralValues.Click_On_Marque.Cree_et_envoi_whatsapp_pdf -> Icons.Default.Share
   ActiveCentralValues.Click_On_Marque.Delete_Client -> Icons.Default.Delete
   ActiveCentralValues.Click_On_Marque.Passe_Client -> Icons.Default.CheckCircle
   ActiveCentralValues.Click_On_Marque.Livre_Client -> Icons.Default.Check
   ActiveCentralValues.Click_On_Marque.Set_Client_Court_Terme -> Icons.Default.Person
   ActiveCentralValues.Click_On_Marque.Set_Client_Long_Terme -> Icons.Default.Person
   ActiveCentralValues.Click_On_Marque.Set_Fournisseur_Court_Terme -> Icons.Default.Store
   ActiveCentralValues.Click_On_Marque.Set_Fournisseur_Long_Terme -> Icons.Default.Store
   ActiveCentralValues.Click_On_Marque.Toggle_Client_De_Jamale -> Icons.Default.Person
   ActiveCentralValues.Click_On_Marque.Toggle_Non_Deletable -> Icons.Default.Lock
}

fun getModeLabel(mode: ActiveCentralValues.Click_On_Marque): String = when (mode) {
    ActiveCentralValues.Click_On_Marque.Standart -> "Standard"
    ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients -> "Ajouter Ciblage"
    ActiveCentralValues.Click_On_Marque.Affiche_OnCommand_VentPeriod_Transaction -> "Afficher Commande"
    ActiveCentralValues.Click_On_Marque.Lence_New_Command -> "Lancer Nouvelle Commande"
    ActiveCentralValues.Click_On_Marque.Call -> "Appeler Client"
    ActiveCentralValues.Click_On_Marque.Navigate -> "Navigation GPS"
    ActiveCentralValues.Click_On_Marque.Marck_Ferme -> "Marquer Fermé"
    ActiveCentralValues.Click_On_Marque.Marck_Command_Livret -> "Marquer Livré"
    ActiveCentralValues.Click_On_Marque.Cree_et_envoi_whatsapp_pdf -> "Envoyer PDF WhatsApp"
    ActiveCentralValues.Click_On_Marque.Delete_Client -> "Supprimer Client"
    ActiveCentralValues.Click_On_Marque.Passe_Client -> "Passer le client"
    ActiveCentralValues.Click_On_Marque.Livre_Client -> "Livrer le client"
    ActiveCentralValues.Click_On_Marque.Set_Client_Court_Terme -> "Client (court terme)"
    ActiveCentralValues.Click_On_Marque.Set_Client_Long_Terme -> "Client (long terme)"
    ActiveCentralValues.Click_On_Marque.Set_Fournisseur_Court_Terme -> "Fournisseur (court terme)"
    ActiveCentralValues.Click_On_Marque.Set_Fournisseur_Long_Terme -> "Fournisseur (long terme)"
    ActiveCentralValues.Click_On_Marque.Toggle_Client_De_Jamale -> "Client de Jamale"
    ActiveCentralValues.Click_On_Marque.Toggle_Non_Deletable -> "Non-Supprimable"
}

fun getModeDescription(mode: ActiveCentralValues.Click_On_Marque): String = when (mode) {
   ActiveCentralValues.Click_On_Marque.Standart -> "Afficher les détails du client"
   ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients -> "Ajouter à la liste de ciblage"
   ActiveCentralValues.Click_On_Marque.Affiche_OnCommand_VentPeriod_Transaction -> "Voir le bon de commande actif"
   ActiveCentralValues.Click_On_Marque.Lence_New_Command -> "Créer et ouvrir directement une nouvelle commande"
   ActiveCentralValues.Click_On_Marque.Call -> "Lancer un appel téléphonique"
   ActiveCentralValues.Click_On_Marque.Navigate -> "Ouvrir dans Google Maps"
   ActiveCentralValues.Click_On_Marque.Marck_Ferme -> "Marquer le client comme fermé"
   ActiveCentralValues.Click_On_Marque.Marck_Command_Livret -> "Marquer la commande comme livrée"
   ActiveCentralValues.Click_On_Marque.Cree_et_envoi_whatsapp_pdf -> "Créer et envoyer le bon PDF via WhatsApp"
   ActiveCentralValues.Click_On_Marque.Delete_Client -> "Supprimer définitivement le client de la carte"
   ActiveCentralValues.Click_On_Marque.Passe_Client -> "Créer un bon Passe_Pour_Current_vent_period pour ce client"
   ActiveCentralValues.Click_On_Marque.Livre_Client -> "Créer un bon COMMANDE_LIVRAI pour ce client"
   ActiveCentralValues.Click_On_Marque.Set_Client_Court_Terme -> "Définir comme Client, crédit court terme"
   ActiveCentralValues.Click_On_Marque.Set_Client_Long_Terme -> "Définir comme Client, crédit long terme"
   ActiveCentralValues.Click_On_Marque.Set_Fournisseur_Court_Terme -> "Définir comme Fournisseur/Grossiste, crédit court terme"
   ActiveCentralValues.Click_On_Marque.Set_Fournisseur_Long_Terme -> "Définir comme Fournisseur/Grossiste, crédit long terme"
   ActiveCentralValues.Click_On_Marque.Toggle_Client_De_Jamale -> "Basculer son statut Client de Jamale"
   ActiveCentralValues.Click_On_Marque.Toggle_Non_Deletable -> "Verrouiller/déverrouiller le client et ses transactions (non supprimables)"
}
