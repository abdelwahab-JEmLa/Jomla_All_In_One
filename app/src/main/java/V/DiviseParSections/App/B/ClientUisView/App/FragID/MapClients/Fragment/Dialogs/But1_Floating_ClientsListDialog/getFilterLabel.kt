package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.But1_Floating_ClientsListDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel

fun getFilterLabel(mode: MapClientsViewModel.VisibleClientsNow): String = when (mode) {
   MapClientsViewModel.VisibleClientsNow.showAll -> "Tous les clients"
   MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit -> "Crédit (court terme)"
   MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term -> "Crédit (long terme)"
   MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Short_Term_Credit -> "Crédit Fournisseurs / Grossistes (court terme)"
   MapClientsViewModel.VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit -> "Crédit Fournisseurs / Grossistes (long terme)"
   MapClientsViewModel.VisibleClientsNow.Filter_Clients_De_Jamale_Avec_Credit -> "Clients de Jamale avec crédit"
   MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME -> "Commande confirmée"
   MapClientsViewModel.VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter -> "Commande livrée"
   MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR -> "Cible vendeur"
   MapClientsViewModel.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX -> "Cible & Passé"
   MapClientsViewModel.VisibleClientsNow.showNonAbsentClientsOnly -> "Clients non absents"
   MapClientsViewModel.VisibleClientsNow.affichePourCollecteurCommendes -> "Collecteur commandes"
   MapClientsViewModel.VisibleClientsNow.showAtayClients -> "Atay / Moukassarat"
   MapClientsViewModel.VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 -> "Cible pour 2"
   MapClientsViewModel.VisibleClientsNow.showAlimentionlients -> "Alimentation"
   MapClientsViewModel.VisibleClientsNow.showClientsWithConfirmedProducts -> "Produits confirmés"
   MapClientsViewModel.VisibleClientsNow.showNonDeletableClientsOnly -> "Clients non supprimables"
}
