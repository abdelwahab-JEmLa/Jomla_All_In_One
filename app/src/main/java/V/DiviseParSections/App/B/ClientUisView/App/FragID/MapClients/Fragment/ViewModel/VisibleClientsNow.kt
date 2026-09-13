package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel

import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Store
import androidx.compose.ui.graphics.Color

enum class VisibleClientsNow(val icon: Any, val couleur: Color = Color.Companion.White) { //<--
//TODO(1): ajotu  its_limited_a900 = false
    // Les 4 filtres crédit : client / fournisseur, court terme / long terme.
    // "Long terme" = crédit ouvert depuis plus de CREDIT_LONG_TERM_THRESHOLD_DAYS
    // jours (voir HandleFilter.kt) — seuil à valider/ajuster côté métier.
    Filter_Leur_Last_TRX_Est_Credit(Icons.Default.Map, Color.Companion.Red),
    Filter_Leur_Last_TRX_Est_Credit_Long_Term(Icons.Default.Map, Color(0xFFB71C1C)),
    Filter_Fournisseurs_Short_Term_Credit(Icons.Default.Store, Color(0xFFFF9800)),
    Filter_Fournisseurs_Long_Term_Credit(Icons.Default.Store, Color(0xFFE65100)),
    // Clients de Jamale (its_Client_De_Jamale == true) ayant un crédit en
    // cours, tous flags court/long terme confondus — recoupement des 4
    // filtres crédit ci-dessus avec le flag Jamale plutôt qu'un nouveau

    // couple court/long terme dédié.
    Filter_Clients_De_Jamale_Avec_Credit(Icons.Default.Map, Color(0xFF009688)),
    Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME(Icons.Default.Map, Color.Companion.Red),
    AFFICHE_COMMANDE_LIVRAI_Filter(Icons.Default.Filter, Color.Companion.Blue),
    AFFICHE_CIBLE_POUR_VENDEUR(Icons.Default.Map, Color.Companion.Red),
    CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX(Icons.Default.SettingsBackupRestore, Color.Companion.Blue),
    showNonAbsentClientsOnly(LottieJsonGetterR_Raw_Icons.reacticonanimatedjsonurl),
    affichePourCollecteurCommendes(LottieJsonGetterR_Raw_Icons.afficheFenetre),
    showAtayClients(LottieJsonGetterR_Raw_Icons.atay),
    showClientsOnlyAcEtateCIBLE_POUR_2(Icons.Default.CheckCircleOutline),
    showAlimentionlients(LottieJsonGetterR_Raw_Icons.alimentation),
    showClientsWithConfirmedProducts(LottieJsonGetterR_Raw_Icons.reacticonanimatedjsonurl),
    showNonDeletableClientsOnly(Icons.Default.CheckCircleOutline, Color(0xFF616161)),
    showAll(LottieJsonGetterR_Raw_Icons.reacticonanimatedjsonurl);
}
