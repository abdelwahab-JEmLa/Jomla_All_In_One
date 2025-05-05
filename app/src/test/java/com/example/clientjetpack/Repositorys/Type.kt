package com.example.clientjetpack.Repositorys

import com.example.clientjetpack.R
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
enum class Type(val color: Int, val nomArabe: String) {
    NON_DEFINI(android.R.color.white, "غير محدد"),

    ON_MODE_COMMEND_ACTUELLEMENT(android.R.color.holo_green_light, "تم تنفيذ المطلوب في "),
    A_COMMANDE_CONFIRME(android.R.color.holo_purple, "تم تاكيد الطلبية"),
    PourVoirPanie(android.R.color.holo_red_light, "للنظر"),
    COMMANDE_LIVRAI(android.R.color.holo_blue_dark, "تم أيصال منتجاته"),

    AVEC_MARCHANDISE(R.color.couleur1, "عندو سلعة"),
    ACHETEUR_NON_DISPO(R.color.c2, "الشاري غائب"),
    FERME(android.R.color.darker_gray, "مغلق"),

    A_EVITE(android.R.color.black, "اقترح ان يتجنب لمدة اسبوعين"),

    RAPPORT_AU_ENREGESTREMENT_VOCALE(android.R.color.black, "التقرير قي التسجيل الصوتي "),

    ON_MODE_VOIRE_PANIE_ARTICLES(android.R.color.holo_blue_dark, "في معاينة السلة"),

    Cible(android.R.color.holo_red_light, "Cible"),
    CIBLE_PRIORITE_2(android.R.color.holo_orange_dark, "CIBLE_PRIORITE_2"),
    CIBLE_PRIORITE_3(android.R.color.holo_green_light, "CIBLE_PRIORITE_3"),
    CIBLE_POUR_2(android.R.color.holo_blue_dark, "CIBLE_POUR_2"),
}
