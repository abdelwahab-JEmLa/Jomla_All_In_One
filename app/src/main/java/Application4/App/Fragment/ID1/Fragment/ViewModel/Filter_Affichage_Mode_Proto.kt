package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application4.App.Fragment.ID1.Fragment.ProductListFilterLogic

enum class Filter_Affichage_Mode_Proto(val mais_sort_order : ProductListFilterLogic.Sort_Order =
                                           ProductListFilterLogic.Sort_Order.Produits_Grouped_Par_Categories) {
    /** Normal product grid — echantillants hidden. 2-column layout. */
    Tablette_Produits_Seulement,
    /** Echantillants-only grid. 4-column layout. */
    Echants_Seulement,
    /** Products + echantillants shown together. 2-column layout. */
    Tablette_Et_Echants,

    /** Panier élargi : si une couleur a une vente active, affiche aussi toutes ses couleurs-sœurs (même produit parent). */
    Panie_Si_Couleur_Ac_Vent_Affiche_Tout_Ces_Freres(ProductListFilterLogic.Sort_Order.Vents_Creation),

    Panie(ProductListFilterLogic.Sort_Order.Vents_Creation),
}
