package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.But1_Floating_ClientsListDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.VisibleClientsNow

fun getFilterLabel(mode: VisibleClientsNow): String = when (mode) {
   VisibleClientsNow.showAll -> "Tous les clients"
   VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit -> "Crédit (court terme)"
   VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term -> "Crédit (long terme)"
   VisibleClientsNow.Filter_Fournisseurs_Short_Term_Credit -> "Crédit Fournisseurs / Grossistes (court terme)"
   VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit -> "Crédit Fournisseurs / Grossistes (long terme)"
   VisibleClientsNow.Filter_Clients_De_Jamale_Avec_Credit -> "Clients de Jamale avec crédit"
   VisibleClientsNow.Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME -> "Commande confirmée"
   VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter -> "Commande livrée"
   VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR -> "Cible vendeur"
   VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX -> "Cible & Passé"
   VisibleClientsNow.showNonAbsentClientsOnly -> "Clients non absents"
   VisibleClientsNow.affichePourCollecteurCommendes -> "Collecteur commandes"
   VisibleClientsNow.showAtayClients -> "Atay / Moukassarat"
   VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 -> "Cible pour 2"
   VisibleClientsNow.showAlimentionlients -> "Alimentation"
   VisibleClientsNow.showClientsWithConfirmedProducts -> "Produits confirmés"
   VisibleClientsNow.showNonDeletableClientsOnly -> "Clients non supprimables"
}
